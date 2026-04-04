package com.almina.ecommerce.service;

import com.almina.ecommerce.dto.auth.OtpSendRequest;
import com.almina.ecommerce.dto.auth.OtpVerificationResponse;
import com.almina.ecommerce.dto.auth.OtpVerifyRequest;

public interface OtpService {
    void sendOtp(OtpSendRequest request);
    OtpVerificationResponse verifyOtp(OtpVerifyRequest request);
    void validateRegistrationToken(String email, String verificationToken);
}
