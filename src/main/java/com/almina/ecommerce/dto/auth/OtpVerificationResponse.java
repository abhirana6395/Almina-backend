package com.almina.ecommerce.dto.auth;

public record OtpVerificationResponse(
        boolean verified,
        String message,
        String registrationToken,
        AuthResponse auth
) {
}
