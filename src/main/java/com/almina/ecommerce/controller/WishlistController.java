package com.almina.ecommerce.controller;

import com.almina.ecommerce.dto.product.ProductResponse;
import com.almina.ecommerce.service.WishlistService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/wishlist")
@RequiredArgsConstructor
public class WishlistController {

    private final WishlistService wishlistService;

    @GetMapping
    public List<ProductResponse> getWishlist() {
        return wishlistService.getWishlist();
    }

    @PostMapping("/{productId}")
    public List<ProductResponse> toggleWishlist(@PathVariable Long productId) {
        return wishlistService.toggleWishlist(productId);
    }
}
