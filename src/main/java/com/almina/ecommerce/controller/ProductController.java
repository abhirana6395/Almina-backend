package com.almina.ecommerce.controller;

import com.almina.ecommerce.dto.product.ProductPageResponse;
import com.almina.ecommerce.dto.product.ProductResponse;
import com.almina.ecommerce.service.ProductService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ProductPageResponse getProducts(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String color,
            @RequestParam(required = false, name = "size") String selectedSize,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(defaultValue = "latest") String sortBy,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12", name = "pageSize") int pageSize
    ) {
        return productService.getProducts(search, category, color, selectedSize, minPrice, maxPrice, sortBy, page, pageSize);
    }

    @GetMapping("/featured")
    public List<ProductResponse> getFeaturedProducts() {
        return productService.getFeaturedProducts();
    }

    @GetMapping("/trending")
    public List<ProductResponse> getTrendingProducts() {
        return productService.getTrendingProducts();
    }

    @GetMapping("/{id}")
    public ProductResponse getProduct(@PathVariable Long id) {
        return productService.getProductById(id);
    }
}
