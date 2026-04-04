package com.almina.ecommerce.service;

import com.almina.ecommerce.dto.cart.CartItemRequest;
import com.almina.ecommerce.dto.cart.CartResponse;

public interface CartService {
    CartResponse getCart();
    CartResponse addToCart(CartItemRequest request);
    CartResponse updateCartItem(Long itemId, CartItemRequest request);
    CartResponse removeCartItem(Long itemId);
}
