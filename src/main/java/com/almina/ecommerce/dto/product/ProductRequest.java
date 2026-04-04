package com.almina.ecommerce.dto.product;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

public record ProductRequest(
        @NotBlank String name,
        @NotBlank String sku,
        @NotBlank String description,
        @NotNull @DecimalMin("0.01") BigDecimal price,
        BigDecimal discountPrice,
        @NotNull @Min(0) Integer stockQuantity,
        String gender,
        Boolean featured,
        Boolean trending,
        @NotNull Long categoryId,
        List<String> images,
        List<String> sizes,
        List<String> colors
) {
}
