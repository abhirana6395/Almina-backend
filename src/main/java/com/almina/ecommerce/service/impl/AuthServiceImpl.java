package com.almina.ecommerce.service.impl;

import com.almina.ecommerce.dto.auth.AuthResponse;
import com.almina.ecommerce.dto.auth.RegisterRequest;
import com.almina.ecommerce.entity.Cart;
import com.almina.ecommerce.entity.Role;
import com.almina.ecommerce.entity.User;
import com.almina.ecommerce.exception.BadRequestException;
import com.almina.ecommerce.mapper.EntityMapper;
import com.almina.ecommerce.repository.CartRepository;
import com.almina.ecommerce.repository.UserRepository;
import com.almina.ecommerce.security.JwtService;
import com.almina.ecommerce.service.AuthService;
import com.almina.ecommerce.service.OtpService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final EntityMapper entityMapper;
    private final OtpService otpService;

    @Override
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new BadRequestException("Email is already registered");
        }
        otpService.validateRegistrationToken(request.email(), request.verificationToken());

        User user = new User();
        user.setFullName(request.fullName());
        user.setEmail(request.email().toLowerCase());
        user.setPhoneNumber(request.phoneNumber());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setActive(true);
        user.setRole(Role.USER);
        User savedUser = userRepository.save(user);

        Cart cart = new Cart();
        cart.setUser(savedUser);
        cartRepository.save(cart);

        return new AuthResponse(jwtService.generateToken(savedUser), entityMapper.toUserResponse(savedUser));
    }
}
