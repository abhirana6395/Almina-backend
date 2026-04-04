package com.almina.ecommerce.service.impl;

import com.almina.ecommerce.dto.auth.AuthResponse;
import com.almina.ecommerce.dto.auth.OtpSendRequest;
import com.almina.ecommerce.dto.auth.OtpVerificationResponse;
import com.almina.ecommerce.dto.auth.OtpVerifyRequest;
import com.almina.ecommerce.entity.Otp;
import com.almina.ecommerce.entity.OtpPurpose;
import com.almina.ecommerce.exception.BadRequestException;
import com.almina.ecommerce.mapper.EntityMapper;
import com.almina.ecommerce.repository.OtpRepository;
import com.almina.ecommerce.repository.UserRepository;
import com.almina.ecommerce.security.JwtService;
import com.almina.ecommerce.service.OtpService;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OtpServiceImpl implements OtpService {

    private final OtpRepository otpRepository;
    private final UserRepository userRepository;
    private final JavaMailSender javaMailSender;
    private final JwtService jwtService;
    private final EntityMapper entityMapper;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.otp.expiry-minutes}")
    private long expiryMinutes;

    @Value("${app.otp.sender-name}")
    private String senderName;

    @Override
    public void sendOtp(OtpSendRequest request) {
        if (request.purpose() == OtpPurpose.REGISTER && userRepository.existsByEmail(request.email())) {
            throw new BadRequestException("Email is already registered");
        }
        if (request.purpose() == OtpPurpose.LOGIN && userRepository.findByEmail(request.email()).isEmpty()) {
            throw new BadRequestException("No account found for this email");
        }

        Otp otp = new Otp();
        otp.setEmail(request.email().toLowerCase());
        otp.setOtp(generateOtp());
        otp.setPurpose(request.purpose());
        otp.setExpiryTime(LocalDateTime.now().plusMinutes(expiryMinutes));
        otp.setVerified(false);
        otp.setConsumed(false);
        otpRepository.save(otp);
        sendEmail(otp);
    }

    @Override
    public OtpVerificationResponse verifyOtp(OtpVerifyRequest request) {
        Otp otp = otpRepository.findTopByEmailAndPurposeOrderByIdDesc(request.email().toLowerCase(), request.purpose())
                .orElseThrow(() -> new BadRequestException("OTP not found. Please request a new one."));

        if (otp.isConsumed()) {
            throw new BadRequestException("OTP already used. Please request a new one.");
        }
        if (otp.getExpiryTime().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("OTP expired. Please request a new one.");
        }
        if (!otp.getOtp().equals(request.otp())) {
            throw new BadRequestException("Invalid OTP");
        }

        otp.setVerified(true);
        otp.setVerifiedAt(LocalDateTime.now());

        if (request.purpose() == OtpPurpose.LOGIN) {
            var user = userRepository.findByEmail(request.email().toLowerCase())
                    .orElseThrow(() -> new BadRequestException("No account found for this email"));
            otp.setConsumed(true);
            otpRepository.save(otp);
            AuthResponse authResponse = new AuthResponse(jwtService.generateToken(user), entityMapper.toUserResponse(user));
            return new OtpVerificationResponse(true, "Login successful", null, authResponse);
        }

        String verificationToken = UUID.randomUUID().toString();
        otp.setVerificationToken(verificationToken);
        otpRepository.save(otp);
        return new OtpVerificationResponse(true, "OTP verified", verificationToken, null);
    }

    @Override
    public void validateRegistrationToken(String email, String verificationToken) {
        Otp otp = otpRepository.findTopByEmailAndPurposeOrderByIdDesc(email.toLowerCase(), OtpPurpose.REGISTER)
                .orElseThrow(() -> new BadRequestException("No verified OTP found"));
        if (!otp.isVerified() || otp.isConsumed()) {
            throw new BadRequestException("OTP verification required");
        }
        if (otp.getVerifiedAt() == null || otp.getVerifiedAt().plusMinutes(10).isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Verification token expired. Please verify again.");
        }
        if (otp.getVerificationToken() == null || !otp.getVerificationToken().equals(verificationToken)) {
            throw new BadRequestException("Invalid verification token");
        }
        otp.setConsumed(true);
        otpRepository.save(otp);
    }

    private String generateOtp() {
        return String.format("%06d", new SecureRandom().nextInt(1_000_000));
    }

    private void sendEmail(Otp otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(otp.getEmail());
        message.setSubject(senderName + " verification code");
        message.setText("Your ALMINA OTP is " + otp.getOtp() + ". It expires in " + expiryMinutes + " minutes.");
        javaMailSender.send(message);
    }
}
