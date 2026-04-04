package com.almina.ecommerce.dto.auth;

import com.almina.ecommerce.dto.user.UserResponse;

public record AuthResponse(
        String token,
        UserResponse user
) {
}
