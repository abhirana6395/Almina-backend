package com.almina.ecommerce.controller;

import com.almina.ecommerce.dto.user.UpdateProfileRequest;
import com.almina.ecommerce.dto.user.UserResponse;
import com.almina.ecommerce.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public UserResponse getProfile() {
        return userService.getProfile();
    }

    @PutMapping("/me")
    public UserResponse updateProfile(@RequestBody UpdateProfileRequest request) {
        return userService.updateProfile(request);
    }
}
