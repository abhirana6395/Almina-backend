package com.almina.ecommerce.service;

import com.almina.ecommerce.dto.product.ProductResponse;
import java.util.List;

public interface WishlistService {
    List<ProductResponse> getWishlist();
    List<ProductResponse> toggleWishlist(Long productId);
}
