package com.almina.ecommerce.service.impl;

import com.almina.ecommerce.dto.product.ProductResponse;
import com.almina.ecommerce.exception.ResourceNotFoundException;
import com.almina.ecommerce.mapper.EntityMapper;
import com.almina.ecommerce.repository.ProductRepository;
import com.almina.ecommerce.repository.WishlistRepository;
import com.almina.ecommerce.service.WishlistService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WishlistServiceImpl implements WishlistService {

    private final CurrentUserService currentUserService;
    private final ProductRepository productRepository;
    private final WishlistRepository wishlistRepository;
    private final EntityMapper entityMapper;

    @Override
    public List<ProductResponse> getWishlist() {
        return wishlistRepository.findByUserOrderByCreatedAtDesc(currentUserService.getCurrentUser()).stream()
                .filter(item -> !item.getProduct().isDeleted())
                .map(item -> entityMapper.toProductResponse(item.getProduct()))
                .toList();
    }

    @Override
    public List<ProductResponse> toggleWishlist(Long productId) {
        var user = currentUserService.getCurrentUser();
        var product = productRepository.findByIdAndIsDeletedFalse(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        wishlistRepository.findByUserAndProduct(user, product)
                .ifPresentOrElse(wishlistRepository::delete, () -> {
                    var item = new com.almina.ecommerce.entity.WishlistItem();
                    item.setUser(user);
                    item.setProduct(product);
                    wishlistRepository.save(item);
                });
        return getWishlist();
    }
}
