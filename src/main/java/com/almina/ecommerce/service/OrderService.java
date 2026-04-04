package com.almina.ecommerce.service;

import com.almina.ecommerce.dto.order.OrderCreateRequest;
import com.almina.ecommerce.dto.order.OrderResponse;
import com.almina.ecommerce.entity.OrderStatus;
import java.util.List;

public interface OrderService {
    OrderResponse placeOrder(OrderCreateRequest request);
    List<OrderResponse> getUserOrders();
    List<OrderResponse> getAllOrders();
    OrderResponse updateOrderStatus(Long orderId, OrderStatus status);
}
