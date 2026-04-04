package com.almina.ecommerce.dto.user;

public record UserResponse(
        Long id,
        String fullName,
        String email,
        String phoneNumber,
        String avatarUrl,
        String addressLine1,
        String addressLine2,
        String city,
        String state,
        String postalCode,
        String country,
        boolean active,
        String role
) {
}
