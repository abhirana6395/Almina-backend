package com.almina.ecommerce.repository;

import com.almina.ecommerce.entity.Payment;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByProviderOrderId(String providerOrderId);
}
