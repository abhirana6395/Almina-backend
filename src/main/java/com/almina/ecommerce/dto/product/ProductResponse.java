package com.almina.ecommerce.dto.product;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record ProductResponse(
        Long id,
        String name,
        String sku,
        String description,
        BigDecimal price,
        BigDecimal discountPrice,
        Integer stockQuantity,
        String gender,
        Boolean featured,
        Boolean trending,
        Double averageRating,
        CategoryDto category,
        List<String> images,
        List<String> sizes,
        List<String> colors,
        LocalDateTime createdAt
) {
}
