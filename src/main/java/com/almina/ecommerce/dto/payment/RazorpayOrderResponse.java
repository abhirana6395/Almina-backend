package com.almina.ecommerce.dto.payment;

public record RazorpayOrderResponse(
        String orderId,
        Long amount,
        String key,
        String currency,
        Long appOrderId,
        String orderNumber
) {
}
