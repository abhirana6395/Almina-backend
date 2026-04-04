package com.almina.ecommerce.dto.auth;

import com.almina.ecommerce.entity.OtpPurpose;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record OtpVerifyRequest(
        @Email @NotBlank String email,
        @NotBlank String otp,
        @NotNull OtpPurpose purpose
) {
}
