package com.almina.ecommerce.repository;

import com.almina.ecommerce.entity.Cart;
import com.almina.ecommerce.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUser(User user);
}
