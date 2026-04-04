package com.almina.ecommerce.service;

import com.almina.ecommerce.dto.review.ReviewRequest;
import com.almina.ecommerce.dto.review.ReviewResponse;
import java.util.List;

public interface ReviewService {
    ReviewResponse addReview(ReviewRequest request);
    List<ReviewResponse> getProductReviews(Long productId);
}
