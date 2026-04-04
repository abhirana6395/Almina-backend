package com.almina.ecommerce.repository;

import com.almina.ecommerce.entity.Product;
import com.almina.ecommerce.entity.Review;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByProductOrderByCreatedAtDesc(Product product);
}
