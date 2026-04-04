package com.almina.ecommerce.service;

import com.almina.ecommerce.dto.user.UpdateProfileRequest;
import com.almina.ecommerce.dto.user.UserResponse;
import java.util.List;

public interface UserService {
    UserResponse getProfile();
    UserResponse updateProfile(UpdateProfileRequest request);
    List<UserResponse> getAllUsers();
    UserResponse toggleUserStatus(Long userId);
}
