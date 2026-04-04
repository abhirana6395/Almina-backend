package com.almina.ecommerce.dto.review;

public record ReviewRequest(
        Long productId,
        Integer rating,
        String comment
) {
}
