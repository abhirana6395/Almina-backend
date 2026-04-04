package com.almina.ecommerce.service.impl;

import com.almina.ecommerce.dto.product.ProductPageResponse;
import com.almina.ecommerce.dto.product.ProductRequest;
import com.almina.ecommerce.dto.product.ProductResponse;
import com.almina.ecommerce.entity.Product;
import com.almina.ecommerce.exception.BadRequestException;
import com.almina.ecommerce.exception.ResourceNotFoundException;
import com.almina.ecommerce.mapper.EntityMapper;
import com.almina.ecommerce.repository.CategoryRepository;
import com.almina.ecommerce.repository.ProductRepository;
import com.almina.ecommerce.service.ProductService;
import jakarta.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final EntityMapper entityMapper;

    @Override
    public ProductResponse createProduct(ProductRequest request) {
        Product product = new Product();
        apply(product, request);
        return entityMapper.toProductResponse(productRepository.save(product));
    }

    @Override
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        apply(product, request);
        return entityMapper.toProductResponse(productRepository.save(product));
    }

    @Override
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        product.setIsDeleted(true);
        productRepository.save(product);
    }

    @Override
    public ProductPageResponse getProducts(String search, String category, String color, String selectedSize, Double minPrice, Double maxPrice, String sortBy, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize, resolveSort(sortBy));
        Specification<Product> specification = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.isFalse(root.get("isDeleted")));
            if (search != null && !search.isBlank()) {
                String pattern = "%" + search.toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("name")), pattern),
                        cb.like(cb.lower(root.get("description")), pattern),
                        cb.like(cb.lower(root.get("sku")), pattern)
                ));
            }
            if (category != null && !category.isBlank()) {
                predicates.add(cb.equal(cb.lower(root.join("category").get("slug")), category.toLowerCase()));
            }
            if (minPrice != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("price"), BigDecimal.valueOf(minPrice)));
            }
            if (maxPrice != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("price"), BigDecimal.valueOf(maxPrice)));
            }
            if (color != null && !color.isBlank()) {
                predicates.add(cb.isMember(color, root.get("colors")));
            }
            if (selectedSize != null && !selectedSize.isBlank()) {
                predicates.add(cb.isMember(selectedSize, root.get("sizes")));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        return entityMapper.toProductPageResponse(productRepository.findAll(specification, pageable));
    }

    @Override
    public ProductResponse getProductById(Long id) {
        return entityMapper.toProductResponse(productRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found")));
    }

    @Override
    public List<ProductResponse> getFeaturedProducts() {
        return productRepository.findByFeaturedTrueAndIsDeletedFalse(PageRequest.of(0, 8)).stream()
                .map(entityMapper::toProductResponse)
                .toList();
    }

    @Override
    public List<ProductResponse> getTrendingProducts() {
        return productRepository.findByTrendingTrueAndIsDeletedFalse(PageRequest.of(0, 8)).stream()
                .map(entityMapper::toProductResponse)
                .toList();
    }

    private void apply(Product product, ProductRequest request) {
        if (request.discountPrice() != null && request.discountPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequestException("Discount price cannot be negative");
        }

        product.setName(request.name().trim());
        product.setSku(request.sku().trim());
        product.setDescription(request.description().trim());
        product.setPrice(request.price());
        product.setDiscountPrice(request.discountPrice() != null && request.discountPrice().compareTo(BigDecimal.ZERO) > 0 ? request.discountPrice() : null);
        product.setStockQuantity(request.stockQuantity());
        product.setGender(request.gender() == null ? null : request.gender().trim());
        product.setFeatured(Boolean.TRUE.equals(request.featured()));
        product.setTrending(Boolean.TRUE.equals(request.trending()));
        product.setImages(request.images() == null ? List.of() : request.images());
        product.setSizes(request.sizes() == null ? List.of() : request.sizes());
        product.setColors(request.colors() == null ? List.of() : request.colors());
        product.setCategory(categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found")));
    }

    private Sort resolveSort(String sortBy) {
        if ("priceAsc".equalsIgnoreCase(sortBy)) {
            return Sort.by("price").ascending();
        }
        if ("priceDesc".equalsIgnoreCase(sortBy)) {
            return Sort.by("price").descending();
        }
        return Sort.by("createdAt").descending();
    }
}
