package com.almina.ecommerce.service.impl;

import com.almina.ecommerce.dto.cart.CartItemRequest;
import com.almina.ecommerce.dto.cart.CartResponse;
import com.almina.ecommerce.entity.Cart;
import com.almina.ecommerce.entity.CartItem;
import com.almina.ecommerce.exception.ResourceNotFoundException;
import com.almina.ecommerce.mapper.EntityMapper;
import com.almina.ecommerce.repository.CartRepository;
import com.almina.ecommerce.repository.ProductRepository;
import com.almina.ecommerce.service.CartService;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CurrentUserService currentUserService;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final EntityMapper entityMapper;

    @Override
    public CartResponse getCart() {
        return entityMapper.toCartResponse(getCurrentCart());
    }

    @Override
    @Transactional
    public CartResponse addToCart(CartItemRequest request) {

        Cart cart = getCurrentCart();

        var product = productRepository.findByIdAndIsDeletedFalse(request.productId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        // 🔍 check if product already exists in cart
        var existingItem = cart.getItems().stream()
                .filter(i -> i.getProduct().getId().equals(product.getId()))
                .findFirst();

        if (existingItem.isPresent()) {
            existingItem.get().setQuantity(
                    existingItem.get().getQuantity() + request.quantity()
            );
        } else {
            CartItem item = new CartItem();

            // 🔥 PEHLE data set karo
            item.setProduct(product);
            item.setQuantity(request.quantity());
            item.setSelectedSize(request.selectedSize());
            item.setSelectedColor(request.selectedColor());

            // 🔥 LAST me cart me add karo
            cart.addItem(item);
        }

        recalculate(cart);

        return entityMapper.toCartResponse(cartRepository.save(cart));
    }

    @Override
    @Transactional
    public CartResponse updateCartItem(Long itemId, CartItemRequest request) {
        Cart cart = getCurrentCart();

        CartItem item = cart.getItems().stream()
                .filter(cartItem -> cartItem.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

        item.setQuantity(request.quantity());
        item.setSelectedSize(request.selectedSize());
        item.setSelectedColor(request.selectedColor());

        recalculate(cart);

        return entityMapper.toCartResponse(cartRepository.save(cart));
    }

    @Override
    @Transactional
    public CartResponse removeCartItem(Long itemId) {
        Cart cart = getCurrentCart();

        cart.getItems().removeIf(item -> item.getId().equals(itemId));

        recalculate(cart);

        return entityMapper.toCartResponse(cartRepository.save(cart));
    }

    private Cart getCurrentCart() {
        var user = currentUserService.getCurrentUser();
        return cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart cart = new Cart();
                    cart.setUser(user);
                    cart.setSubtotal(BigDecimal.ZERO);
                    return cartRepository.save(cart);
                });
    }

    private void recalculate(Cart cart) {
        BigDecimal subtotal = cart.getItems().stream()
                .map(item -> entityMapper.resolvedPrice(item.getProduct())
                        .multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        cart.setSubtotal(subtotal);
    }
}
