package com.almina.ecommerce.dto.common;

public record ApiResponse<T>(
        String message,
        T data
) {
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(message, data);
    }
}
