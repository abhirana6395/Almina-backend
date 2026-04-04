package com.almina.ecommerce.service;

import com.almina.ecommerce.dto.auth.AdminLoginRequest;
import com.almina.ecommerce.dto.auth.AuthResponse;
import java.util.Map;

public interface AdminService {
    AuthResponse login(AdminLoginRequest request);
    Map<String, Object> getDashboardAnalytics();
}
