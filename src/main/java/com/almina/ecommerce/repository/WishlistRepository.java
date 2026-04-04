package com.almina.ecommerce.repository;

import com.almina.ecommerce.entity.Product;
import com.almina.ecommerce.entity.User;
import com.almina.ecommerce.entity.WishlistItem;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WishlistRepository extends JpaRepository<WishlistItem, Long> {
    List<WishlistItem> findByUserOrderByCreatedAtDesc(User user);
    Optional<WishlistItem> findByUserAndProduct(User user, Product product);
}
