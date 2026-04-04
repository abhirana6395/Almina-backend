package com.almina.ecommerce.controller;

import com.almina.ecommerce.dto.cart.CartItemRequest;
import com.almina.ecommerce.dto.cart.CartResponse;
import com.almina.ecommerce.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public CartResponse getCart() {
        return cartService.getCart();
    }

    @PostMapping
    public CartResponse addToCart(@Valid @RequestBody CartItemRequest request) {
        return cartService.addToCart(request);
    }

    @PutMapping("/{itemId}")
    public CartResponse updateCartItem(@PathVariable Long itemId, @Valid @RequestBody CartItemRequest request) {
        return cartService.updateCartItem(itemId, request);
    }

    @DeleteMapping("/{itemId}")
    public CartResponse removeCartItem(@PathVariable Long itemId) {
        return cartService.removeCartItem(itemId);
    }
}
