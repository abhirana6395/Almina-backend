package com.almina.ecommerce.dto.product;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CouponDto(
        Long id,
        String code,
        BigDecimal discountPercentage,
        BigDecimal maxDiscountAmount,
        LocalDateTime validUntil,
        Boolean active
) {
}
