package com.almina.ecommerce.dto.auth;

import com.almina.ecommerce.entity.OtpPurpose;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record OtpSendRequest(
        @Email @NotBlank String email,
        @NotNull OtpPurpose purpose
) {
}
