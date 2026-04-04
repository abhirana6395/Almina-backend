package com.almina.ecommerce.mapper;

import com.almina.ecommerce.dto.cart.CartItemResponse;
import com.almina.ecommerce.dto.cart.CartResponse;
import com.almina.ecommerce.dto.order.OrderItemResponse;
import com.almina.ecommerce.dto.order.OrderResponse;
import com.almina.ecommerce.dto.product.CategoryDto;
import com.almina.ecommerce.dto.product.ProductPageResponse;
import com.almina.ecommerce.dto.product.ProductResponse;
import com.almina.ecommerce.dto.review.ReviewResponse;
import com.almina.ecommerce.dto.user.UserResponse;
import com.almina.ecommerce.entity.Cart;
import com.almina.ecommerce.entity.CartItem;
import com.almina.ecommerce.entity.Order;
import com.almina.ecommerce.entity.OrderItem;
import com.almina.ecommerce.entity.Product;
import com.almina.ecommerce.entity.Review;
import com.almina.ecommerce.entity.User;
import java.math.BigDecimal;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class EntityMapper {

    public UserResponse toUserResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getAvatarUrl(),
                user.getAddressLine1(),
                user.getAddressLine2(),
                user.getCity(),
                user.getState(),
                user.getPostalCode(),
                user.getCountry(),
                user.isActive(),
                user.getRole().name()
        );
    }

    public CategoryDto toCategoryDto(com.almina.ecommerce.entity.Category category) {
        if (category == null) {
            return null;
        }
        return new CategoryDto(category.getId(), category.getName(), category.getSlug(), category.getImageUrl(), category.getDescription());
    }

    public ProductResponse toProductResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getSku(),
                product.getDescription(),
                product.getPrice(),
                product.getDiscountPrice(),
                product.getStockQuantity(),
                product.getGender(),
                product.getFeatured(),
                product.getTrending(),
                product.getAverageRating(),
                toCategoryDto(product.getCategory()),
                product.getImages(),
                product.getSizes(),
                product.getColors(),
                product.getCreatedAt()
        );
    }

    public ProductPageResponse toProductPageResponse(Page<Product> page) {
        return new ProductPageResponse(
                page.getContent().stream().map(this::toProductResponse).toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast()
        );
    }

    public CartItemResponse toCartItemResponse(CartItem item) {
        BigDecimal lineTotal = resolvedPrice(item.getProduct()).multiply(BigDecimal.valueOf(item.getQuantity()));
        return new CartItemResponse(
                item.getId(),
                toProductResponse(item.getProduct()),
                item.getQuantity(),
                item.getSelectedSize(),
                item.getSelectedColor(),
                lineTotal
        );
    }

    public CartResponse toCartResponse(Cart cart) {
        return new CartResponse(
                cart.getId(),
                cart.getItems().stream().map(this::toCartItemResponse).toList(),
                cart.getSubtotal()
        );
    }

    public OrderResponse toOrderResponse(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getOrderNumber(),
                order.getStatus(),
                order.getPaymentMethod(),
                order.getPaymentStatus(),
                order.getSubtotal(),
                order.getShippingFee(),
                order.getTax(),
                order.getTotalAmount(),
                order.getCreatedAt(),
                order.getItems().stream().map(this::toOrderItemResponse).toList()
        );
    }

    public OrderItemResponse toOrderItemResponse(OrderItem item) {
        return new OrderItemResponse(
                item.getId(),
                item.getProduct().getId(),
                item.getProduct().getName(),
                item.getProduct().getImages().isEmpty() ? null : item.getProduct().getImages().get(0),
                item.getQuantity(),
                item.getSelectedSize(),
                item.getSelectedColor(),
                item.getUnitPrice(),
                item.getTotalPrice()
        );
    }

    public ReviewResponse toReviewResponse(Review review) {
        return new ReviewResponse(
                review.getId(),
                review.getUser().getId(),
                review.getUser().getFullName(),
                review.getRating(),
                review.getComment(),
                review.getCreatedAt()
        );
    }

    public BigDecimal resolvedPrice(Product product) {
        return product.getDiscountPrice() != null ? product.getDiscountPrice() : product.getPrice();
    }
}
