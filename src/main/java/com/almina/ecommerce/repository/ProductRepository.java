package com.almina.ecommerce.repository;

import com.almina.ecommerce.entity.Product;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    Optional<Product> findByIdAndIsDeletedFalse(Long id);
    Page<Product> findByFeaturedTrueAndIsDeletedFalse(Pageable pageable);
    Page<Product> findByTrendingTrueAndIsDeletedFalse(Pageable pageable);
}
