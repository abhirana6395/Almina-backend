package com.almina.ecommerce.dto.order;

import com.almina.ecommerce.entity.PaymentMethod;
import com.almina.ecommerce.entity.PaymentStatus;
import com.almina.ecommerce.entity.OrderStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
        Long id,
        String orderNumber,
        OrderStatus status,
        PaymentMethod paymentMethod,
        PaymentStatus paymentStatus,
        BigDecimal subtotal,
        BigDecimal shippingFee,
        BigDecimal tax,
        BigDecimal totalAmount,
        LocalDateTime createdAt,
        List<OrderItemResponse> items
) {
}
