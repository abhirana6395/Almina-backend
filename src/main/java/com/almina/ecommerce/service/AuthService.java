package com.almina.ecommerce.service;

import com.almina.ecommerce.dto.auth.AuthResponse;
import com.almina.ecommerce.dto.auth.RegisterRequest;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
}
