package com.almina.ecommerce.controller;

import com.almina.ecommerce.dto.review.ReviewRequest;
import com.almina.ecommerce.dto.review.ReviewResponse;
import com.almina.ecommerce.service.ReviewService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/product/{productId}")
    public List<ReviewResponse> getProductReviews(@PathVariable Long productId) {
        return reviewService.getProductReviews(productId);
    }

    @PostMapping
    public ReviewResponse addReview(@RequestBody ReviewRequest request) {
        return reviewService.addReview(request);
    }
}
