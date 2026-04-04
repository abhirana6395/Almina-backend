package com.almina.ecommerce.dto.cart;

import com.almina.ecommerce.dto.product.ProductResponse;
import java.math.BigDecimal;

public record CartItemResponse(
        Long id,
        ProductResponse product,
        Integer quantity,
        String selectedSize,
        String selectedColor,
        BigDecimal lineTotal
) {
}
