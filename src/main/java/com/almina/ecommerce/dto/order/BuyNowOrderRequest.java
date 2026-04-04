package com.almina.ecommerce.dto.order;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record BuyNowOrderRequest(
        @NotNull Long productId,
        @NotNull @Min(1) Integer quantity
) {
}
