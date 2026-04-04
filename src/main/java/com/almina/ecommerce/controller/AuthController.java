package com.almina.ecommerce.controller;

import com.almina.ecommerce.dto.auth.AuthResponse;
import com.almina.ecommerce.dto.auth.OtpSendRequest;
import com.almina.ecommerce.dto.auth.OtpVerificationResponse;
import com.almina.ecommerce.dto.auth.OtpVerifyRequest;
import com.almina.ecommerce.dto.auth.RegisterRequest;
import com.almina.ecommerce.service.AuthService;
import com.almina.ecommerce.service.OtpService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final OtpService otpService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/send-otp")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void sendOtp(@Valid @RequestBody OtpSendRequest request) {
        otpService.sendOtp(request);
    }

    @PostMapping("/verify-otp")
    public OtpVerificationResponse verifyOtp(@Valid @RequestBody OtpVerifyRequest request) {
        return otpService.verifyOtp(request);
    }
}
