package com.almina.ecommerce.service.impl;

import com.almina.ecommerce.dto.order.AddressRequest;
import com.almina.ecommerce.dto.order.OrderCreateRequest;
import com.almina.ecommerce.entity.Cart;
import com.almina.ecommerce.entity.Order;
import com.almina.ecommerce.entity.OrderItem;
import com.almina.ecommerce.entity.OrderStatus;
import com.almina.ecommerce.entity.PaymentMethod;
import com.almina.ecommerce.entity.PaymentStatus;
import com.almina.ecommerce.entity.Product;
import com.almina.ecommerce.entity.User;
import com.almina.ecommerce.exception.BadRequestException;
import com.almina.ecommerce.mapper.EntityMapper;
import com.almina.ecommerce.repository.CartRepository;
import com.almina.ecommerce.repository.OrderRepository;
import com.almina.ecommerce.repository.ProductRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class OrderWorkflowService {

    private final ProductRepository productRepository;
    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final EntityMapper entityMapper;

    public Cart requireCheckoutCart(User user) {
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new BadRequestException("Cart not found"));
        if (cart.getItems().isEmpty()) {
            throw new BadRequestException("Cart is empty");
        }
        return cart;
    }

    public Order buildDraft(Cart cart, User user, OrderCreateRequest request, PaymentMethod paymentMethod,
                            PaymentStatus paymentStatus, OrderStatus orderStatus) {
        Order order = new Order();
        initializeOrder(order, user, request.shippingAddress(), paymentMethod, paymentStatus, orderStatus, false);
        order.setSubtotal(cart.getSubtotal());
        applyTotals(order);

        cart.getItems().forEach(cartItem -> {
            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProduct(cartItem.getProduct());
            item.setQuantity(cartItem.getQuantity());
            item.setSelectedSize(cartItem.getSelectedSize());
            item.setSelectedColor(cartItem.getSelectedColor());
            item.setUnitPrice(entityMapper.resolvedPrice(cartItem.getProduct()));
            item.setTotalPrice(item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
            order.getItems().add(item);
        });
        return order;
    }

    public Order buildDirectDraft(Product product, int quantity, User user, AddressRequest shippingAddress,
                                  PaymentMethod paymentMethod, PaymentStatus paymentStatus, OrderStatus orderStatus) {
        Order order = new Order();
        initializeOrder(order, user, shippingAddress, paymentMethod, paymentStatus, orderStatus, true);

        OrderItem item = new OrderItem();
        item.setOrder(order);
        item.setProduct(product);
        item.setQuantity(quantity);
        item.setUnitPrice(entityMapper.resolvedPrice(product));
        item.setTotalPrice(item.getUnitPrice().multiply(BigDecimal.valueOf(quantity)));
        order.getItems().add(item);

        order.setSubtotal(item.getTotalPrice());
        applyTotals(order);
        return order;
    }

    public void validateStock(Order order) {
        for (OrderItem item : order.getItems()) {
            Product product = item.getProduct();
            if (product.getStockQuantity() == null || product.getStockQuantity() < item.getQuantity()) {
                throw new BadRequestException("Insufficient stock for " + product.getName());
            }
        }
    }

    @Transactional
    public Order persist(Order order) {
        validateStock(order);
        return orderRepository.save(order);
    }

    @Transactional
    public Order persistAndFinalize(Order order, Cart cart) {
        validateStock(order);
        finalizeInventoryAndCart(order, cart);
        return orderRepository.save(order);
    }

    @Transactional
    public Order finalizeExistingOrder(Order order) {
        validateStock(order);
        if (order.isDirectCheckout()) {
            finalizeInventory(order);
        } else {
            Cart cart = cartRepository.findByUser(order.getUser())
                    .orElseThrow(() -> new BadRequestException("Cart not found"));
            finalizeInventoryAndCart(order, cart);
        }
        return orderRepository.save(order);
    }

    private void initializeOrder(Order order, User user, AddressRequest shippingAddress, PaymentMethod paymentMethod,
                                 PaymentStatus paymentStatus, OrderStatus orderStatus, boolean directCheckout) {
        order.setUser(user);
        order.setOrderNumber("ALM-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        order.setStatus(orderStatus);
        order.setPaymentMethod(paymentMethod);
        order.setPaymentStatus(paymentStatus);
        order.setDirectCheckout(directCheckout);
        order.setShippingName(shippingAddress.fullName());
        order.setShippingPhone(shippingAddress.phone());
        order.setShippingAddressLine1(shippingAddress.addressLine1());
        order.setShippingAddressLine2(shippingAddress.addressLine2());
        order.setShippingCity(shippingAddress.city());
        order.setShippingState(shippingAddress.state());
        order.setShippingPostalCode(shippingAddress.postalCode());
        order.setShippingCountry(shippingAddress.country());
    }

    private void applyTotals(Order order) {
        order.setShippingFee(new BigDecimal("99.00"));
        order.setTax(order.getSubtotal().multiply(new BigDecimal("0.18")).setScale(2, RoundingMode.HALF_UP));
        order.setTotalAmount(order.getSubtotal().add(order.getShippingFee()).add(order.getTax()));
    }

    private void finalizeInventory(Order order) {
        for (OrderItem item : order.getItems()) {
            Product product = item.getProduct();
            product.setStockQuantity(product.getStockQuantity() - item.getQuantity());
            productRepository.save(product);
        }
    }

    private void finalizeInventoryAndCart(Order order, Cart cart) {
        finalizeInventory(order);
        cart.getItems().clear();
        cart.setSubtotal(BigDecimal.ZERO);
        cartRepository.save(cart);
    }
}
