package com.almina.ecommerce.service.impl;

import com.almina.ecommerce.dto.user.UpdateProfileRequest;
import com.almina.ecommerce.dto.user.UserResponse;
import com.almina.ecommerce.exception.ResourceNotFoundException;
import com.almina.ecommerce.mapper.EntityMapper;
import com.almina.ecommerce.repository.UserRepository;
import com.almina.ecommerce.service.UserService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final CurrentUserService currentUserService;
    private final UserRepository userRepository;
    private final EntityMapper entityMapper;

    @Override
    public UserResponse getProfile() {
        return entityMapper.toUserResponse(currentUserService.getCurrentUser());
    }

    @Override
    @Transactional
    public UserResponse updateProfile(UpdateProfileRequest request) {
        var user = currentUserService.getCurrentUser();
        user.setFullName(request.fullName());
        user.setPhoneNumber(request.phoneNumber());
        user.setAvatarUrl(request.avatarUrl());
        user.setAddressLine1(request.addressLine1());
        user.setAddressLine2(request.addressLine2());
        user.setCity(request.city());
        user.setState(request.state());
        user.setPostalCode(request.postalCode());
        user.setCountry(request.country());
        return entityMapper.toUserResponse(userRepository.save(user));
    }

    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream().map(entityMapper::toUserResponse).toList();
    }

    @Override
    @Transactional
    public UserResponse toggleUserStatus(Long userId) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setActive(!user.isActive());
        return entityMapper.toUserResponse(userRepository.save(user));
    }
}
