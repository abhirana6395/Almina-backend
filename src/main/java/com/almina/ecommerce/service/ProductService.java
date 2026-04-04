package com.almina.ecommerce.service;

import com.almina.ecommerce.dto.product.ProductPageResponse;
import com.almina.ecommerce.dto.product.ProductRequest;
import com.almina.ecommerce.dto.product.ProductResponse;
import java.util.List;

public interface ProductService {
    ProductResponse createProduct(ProductRequest request);
    ProductResponse updateProduct(Long id, ProductRequest request);
    void deleteProduct(Long id);
    ProductPageResponse getProducts(String search, String category, String color, String selectedSize, Double minPrice, Double maxPrice, String sortBy, int page, int pageSize);
    ProductResponse getProductById(Long id);
    List<ProductResponse> getFeaturedProducts();
    List<ProductResponse> getTrendingProducts();
}
