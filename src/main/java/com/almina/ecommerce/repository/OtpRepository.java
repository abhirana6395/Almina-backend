package com.almina.ecommerce.repository;

import com.almina.ecommerce.entity.Otp;
import com.almina.ecommerce.entity.OtpPurpose;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OtpRepository extends JpaRepository<Otp, Long> {
    Optional<Otp> findTopByEmailAndPurposeOrderByIdDesc(String email, OtpPurpose purpose);
}
