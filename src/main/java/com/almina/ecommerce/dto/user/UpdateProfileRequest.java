package com.almina.ecommerce.dto.user;

public record UpdateProfileRequest(
        String fullName,
        String phoneNumber,
        String avatarUrl,
        String addressLine1,
        String addressLine2,
        String city,
        String state,
        String postalCode,
        String country
) {
}
