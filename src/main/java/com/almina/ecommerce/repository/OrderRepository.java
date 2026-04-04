package com.almina.ecommerce.repository;

import com.almina.ecommerce.entity.Order;
import com.almina.ecommerce.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserOrderByCreatedAtDesc(User user);
    Optional<Order> findByRazorpayOrderId(String razorpayOrderId);
}
