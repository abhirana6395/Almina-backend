package com.almina.ecommerce.dto.product;

public record CategoryDto(
        Long id,
        String name,
        String slug,
        String imageUrl,
        String description
) {
}
