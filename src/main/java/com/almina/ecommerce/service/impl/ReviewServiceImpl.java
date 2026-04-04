package com.almina.ecommerce.service.impl;

import com.almina.ecommerce.dto.review.ReviewRequest;
import com.almina.ecommerce.dto.review.ReviewResponse;
import com.almina.ecommerce.entity.Review;
import com.almina.ecommerce.exception.ResourceNotFoundException;
import com.almina.ecommerce.mapper.EntityMapper;
import com.almina.ecommerce.repository.ProductRepository;
import com.almina.ecommerce.repository.ReviewRepository;
import com.almina.ecommerce.service.ReviewService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final CurrentUserService currentUserService;
    private final ProductRepository productRepository;
    private final ReviewRepository reviewRepository;
    private final EntityMapper entityMapper;

    @Override
    public ReviewResponse addReview(ReviewRequest request) {
        Review review = new Review();
        review.setProduct(productRepository.findByIdAndIsDeletedFalse(request.productId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found")));
        review.setUser(currentUserService.getCurrentUser());
        review.setRating(request.rating());
        review.setComment(request.comment());
        Review saved = reviewRepository.save(review);
        updateRating(saved.getProduct().getId());
        return entityMapper.toReviewResponse(saved);
    }

    @Override
    public List<ReviewResponse> getProductReviews(Long productId) {
        var product = productRepository.findByIdAndIsDeletedFalse(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        return reviewRepository.findByProductOrderByCreatedAtDesc(product).stream().map(entityMapper::toReviewResponse).toList();
    }

    private void updateRating(Long productId) {
        var product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        var reviews = reviewRepository.findByProductOrderByCreatedAtDesc(product);
        product.setAverageRating(reviews.stream().mapToInt(Review::getRating).average().orElse(0));
        productRepository.save(product);
    }
}
