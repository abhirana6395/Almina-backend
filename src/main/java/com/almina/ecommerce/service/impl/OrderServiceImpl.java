package com.almina.ecommerce.service.impl;

import com.almina.ecommerce.dto.order.OrderCreateRequest;
import com.almina.ecommerce.dto.order.OrderResponse;
import com.almina.ecommerce.entity.Order;
import com.almina.ecommerce.entity.OrderStatus;
import com.almina.ecommerce.entity.PaymentMethod;
import com.almina.ecommerce.entity.PaymentStatus;
import com.almina.ecommerce.exception.BadRequestException;
import com.almina.ecommerce.exception.ResourceNotFoundException;
import com.almina.ecommerce.mapper.EntityMapper;
import com.almina.ecommerce.repository.OrderRepository;
import com.almina.ecommerce.service.OrderService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final CurrentUserService currentUserService;
    private final OrderRepository orderRepository;
    private final EntityMapper entityMapper;
    private final OrderWorkflowService orderWorkflowService;

    @Override
    public OrderResponse placeOrder(OrderCreateRequest request) {
        if (request.paymentMethod() != PaymentMethod.COD) {
            throw new BadRequestException("Use Razorpay create-order endpoint for online payments");
        }

        var user = currentUserService.getCurrentUser();
        var cart = orderWorkflowService.requireCheckoutCart(user);
        var order = orderWorkflowService.buildDraft(cart, user, request, PaymentMethod.COD, PaymentStatus.PENDING, OrderStatus.CONFIRMED);
        return entityMapper.toOrderResponse(orderWorkflowService.persistAndFinalize(order, cart));
    }

    @Override
    public List<OrderResponse> getUserOrders() {
        return orderRepository.findByUserOrderByCreatedAtDesc(currentUserService.getCurrentUser()).stream()
                .map(entityMapper::toOrderResponse)
                .toList();
    }

    @Override
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll().stream().map(entityMapper::toOrderResponse).toList();
    }

    @Override
    public OrderResponse updateOrderStatus(Long orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        order.setStatus(status);
        return entityMapper.toOrderResponse(orderRepository.save(order));
    }
}
