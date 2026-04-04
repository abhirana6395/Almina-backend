package com.almina.ecommerce.dto.payment;

public record RazorpayVerifyRequest(
        Long appOrderId,
        String razorpayOrderId,
        String razorpayPaymentId,
        String razorpaySignature
) {
}
