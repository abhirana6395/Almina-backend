package com.almina.ecommerce.service.impl;

import com.almina.ecommerce.entity.User;
import com.almina.ecommerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CurrentUserService {

    private final UserRepository userRepository;

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getName())) {
            throw new InsufficientAuthenticationException("Authentication required");
        }

        Long userId;
        try {
            userId = Long.parseLong(authentication.getName());
        } catch (NumberFormatException exception) {
            throw new InsufficientAuthenticationException("Invalid authenticated user");
        }

        return userRepository.findById(userId)
                .orElseThrow(() -> new InsufficientAuthenticationException("Authenticated user no longer exists"));
    }
}
