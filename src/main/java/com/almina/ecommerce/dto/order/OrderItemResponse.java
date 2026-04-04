package com.almina.ecommerce.dto.order;

import java.math.BigDecimal;

public record OrderItemResponse(
        Long id,
        Long productId,
        String productName,
        String imageUrl,
        Integer quantity,
        String selectedSize,
        String selectedColor,
        BigDecimal unitPrice,
        BigDecimal totalPrice
) {
}
