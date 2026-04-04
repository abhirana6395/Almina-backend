package com.almina.ecommerce.service.impl;

import com.almina.ecommerce.dto.order.AddressRequest;
import com.almina.ecommerce.dto.order.BuyNowOrderRequest;
import com.almina.ecommerce.dto.order.OrderResponse;
import com.almina.ecommerce.dto.payment.PaymentOrderRequest;
import com.almina.ecommerce.dto.payment.RazorpayOrderResponse;
import com.almina.ecommerce.dto.payment.RazorpayVerifyRequest;
import com.almina.ecommerce.entity.OrderStatus;
import com.almina.ecommerce.entity.Payment;
import com.almina.ecommerce.entity.PaymentProvider;
import com.almina.ecommerce.entity.PaymentMethod;
import com.almina.ecommerce.entity.PaymentStatus;
import com.almina.ecommerce.exception.BadRequestException;
import com.almina.ecommerce.exception.ResourceNotFoundException;
import com.almina.ecommerce.mapper.EntityMapper;
import com.almina.ecommerce.repository.OrderRepository;
import com.almina.ecommerce.repository.PaymentRepository;
import com.almina.ecommerce.repository.ProductRepository;
import com.almina.ecommerce.service.PaymentService;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HexFormat;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final ProductRepository productRepository;
    private final CurrentUserService currentUserService;
    private final OrderWorkflowService orderWorkflowService;
    private final EntityMapper entityMapper;

    @Value("${app.razorpay.key-id}")
    private String razorpayKeyId;

    @Value("${app.razorpay.key-secret}")
    private String razorpayKeySecret;

    @Override
    @Transactional
    public RazorpayOrderResponse createRazorpayOrder(PaymentOrderRequest request) {
        var user = currentUserService.getCurrentUser();
        var cart = orderWorkflowService.requireCheckoutCart(user);
        var order = orderWorkflowService.buildDraft(
                cart,
                user,
                new com.almina.ecommerce.dto.order.OrderCreateRequest(request.shippingAddress(), PaymentMethod.RAZORPAY),
                PaymentMethod.RAZORPAY,
                PaymentStatus.PENDING,
                OrderStatus.PENDING
        );
        validateRequestedAmount(request.amount(), order.getTotalAmount());
        return createPendingRazorpayOrder(order);
    }

    @Override
    @Transactional
    public RazorpayOrderResponse createBuyNowOrder(BuyNowOrderRequest request) {
        var user = currentUserService.getCurrentUser();
        var product = productRepository.findByIdAndIsDeletedFalse(request.productId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        var order = orderWorkflowService.buildDirectDraft(
                product,
                request.quantity(),
                user,
                resolveShippingAddress(user),
                PaymentMethod.RAZORPAY,
                PaymentStatus.PENDING,
                OrderStatus.PENDING
        );
        return createPendingRazorpayOrder(order);
    }

    @Override
    @Transactional
    public OrderResponse verifyRazorpayPayment(RazorpayVerifyRequest request) {
        var user = currentUserService.getCurrentUser();
        var order = orderRepository.findById(request.appOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        if (!order.getUser().getId().equals(user.getId())) {
            throw new BadRequestException("You cannot verify another user's order");
        }
        if (order.getPaymentMethod() != PaymentMethod.RAZORPAY) {
            throw new BadRequestException("This order is not a Razorpay order");
        }
        Payment payment = paymentRepository.findByProviderOrderId(request.razorpayOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Payment record not found"));

        if (!request.razorpayOrderId().equals(order.getRazorpayOrderId())) {
            throw new BadRequestException("Razorpay order mismatch");
        }

        if (order.getStatus() == OrderStatus.CONFIRMED && order.getPaymentStatus() == PaymentStatus.SUCCESS) {
            return entityMapper.toOrderResponse(order);
        }

        String expected = hmacSha256(request.razorpayOrderId() + "|" + request.razorpayPaymentId(), razorpayKeySecret);
        if (!expected.equals(request.razorpaySignature())) {
            payment.setStatus(PaymentStatus.FAILED);
            paymentRepository.save(payment);
            order.setPaymentStatus(PaymentStatus.FAILED);
            order.setStatus(OrderStatus.CANCELLED);
            orderRepository.save(order);
            throw new BadRequestException("Invalid payment signature");
        }
        payment.setProviderPaymentId(request.razorpayPaymentId());
        payment.setSignature(request.razorpaySignature());
        payment.setStatus(PaymentStatus.SUCCESS);
        paymentRepository.save(payment);

        order.setPaymentStatus(PaymentStatus.SUCCESS);
        order.setStatus(OrderStatus.CONFIRMED);
        return entityMapper.toOrderResponse(orderWorkflowService.finalizeExistingOrder(order));
    }

    private RazorpayOrderResponse createPendingRazorpayOrder(com.almina.ecommerce.entity.Order order) {
        orderWorkflowService.validateStock(order);

        try {
            RazorpayClient client = new RazorpayClient(razorpayKeyId, razorpayKeySecret);
            long amountInPaise = order.getTotalAmount().multiply(BigDecimal.valueOf(100)).longValue();
            JSONObject payload = new JSONObject();
            payload.put("amount", amountInPaise);
            payload.put("currency", "INR");
            payload.put("receipt", order.getOrderNumber());
            Order razorpayOrder = client.orders.create(payload);

            order.setRazorpayOrderId(razorpayOrder.get("id"));
            var savedOrder = orderWorkflowService.persist(order);
            savePendingPayment(savedOrder, razorpayOrder.get("id"));

            return new RazorpayOrderResponse(
                    razorpayOrder.get("id"),
                    amountInPaise,
                    razorpayKeyId,
                    "INR",
                    savedOrder.getId(),
                    savedOrder.getOrderNumber()
            );
        } catch (Exception exception) {
            throw new BadRequestException("Unable to create Razorpay order: " + exception.getMessage());
        }
    }

    private void savePendingPayment(com.almina.ecommerce.entity.Order order, String providerOrderId) {
        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setAmount(order.getTotalAmount());
        payment.setProvider(PaymentProvider.RAZORPAY);
        payment.setProviderOrderId(providerOrderId);
        payment.setStatus(PaymentStatus.PENDING);
        payment.setCreatedAt(LocalDateTime.now());
        payment.setUpdatedAt(LocalDateTime.now());
        paymentRepository.save(payment);
    }

    private AddressRequest resolveShippingAddress(com.almina.ecommerce.entity.User user) {
        if (isBlank(user.getFullName())
                || isBlank(user.getPhoneNumber())
                || isBlank(user.getAddressLine1())
                || isBlank(user.getCity())
                || isBlank(user.getState())
                || isBlank(user.getPostalCode())
                || isBlank(user.getCountry())) {
            throw new BadRequestException("Complete your profile address before using Buy Now");
        }

        return new AddressRequest(
                user.getFullName(),
                user.getPhoneNumber(),
                user.getAddressLine1(),
                user.getAddressLine2(),
                user.getCity(),
                user.getState(),
                user.getPostalCode(),
                user.getCountry()
        );
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private void validateRequestedAmount(BigDecimal requestedAmount, BigDecimal actualAmount) {
        if (requestedAmount == null) {
            return;
        }

        if (requestedAmount.compareTo(actualAmount) != 0) {
            throw new BadRequestException("Order total mismatch");
        }
    }

    private String hmacSha256(String data, String secret) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            return HexFormat.of().formatHex(mac.doFinal(data.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception exception) {
            throw new BadRequestException("Unable to verify signature");
        }
    }
}
