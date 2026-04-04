package com.almina.ecommerce.controller;

import com.almina.ecommerce.dto.auth.AdminLoginRequest;
import com.almina.ecommerce.dto.auth.AuthResponse;
import com.almina.ecommerce.dto.common.ApiResponse;
import com.almina.ecommerce.dto.product.CategoryDto;
import com.almina.ecommerce.dto.product.CouponDto;
import com.almina.ecommerce.dto.product.ProductRequest;
import com.almina.ecommerce.dto.product.ProductResponse;
import com.almina.ecommerce.dto.user.UserResponse;
import com.almina.ecommerce.entity.OrderStatus;
import com.almina.ecommerce.service.AdminService;
import com.almina.ecommerce.service.CategoryService;
import com.almina.ecommerce.service.CouponService;
import com.almina.ecommerce.service.OrderService;
import com.almina.ecommerce.service.ProductService;
import com.almina.ecommerce.service.UserService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private static final String ADMIN_OR_DEBUG = "@securityDebugMode.isDebugPermitAll() or hasRole('ADMIN')";

    private final AdminService adminService;
    private final ProductService productService;
    private final OrderService orderService;
    private final UserService userService;
    private final CategoryService categoryService;
    private final CouponService couponService;

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody AdminLoginRequest request) {
        return adminService.login(request);
    }

    @GetMapping({"/dashboard", "/analytics"})
    @PreAuthorize(ADMIN_OR_DEBUG)
    public ApiResponse<Map<String, Object>> getAnalytics() {
        return ApiResponse.success("Success", adminService.getDashboardAnalytics());
    }

    @GetMapping("/users")
    @PreAuthorize(ADMIN_OR_DEBUG)
    public ApiResponse<List<UserResponse>> getUsers() {
        return ApiResponse.success("Success", userService.getAllUsers());
    }

    @PutMapping("/users/{id}/toggle-status")
    @PreAuthorize(ADMIN_OR_DEBUG)
    public ApiResponse<UserResponse> toggleUserStatus(@PathVariable Long id) {
        return ApiResponse.success("Success", userService.toggleUserStatus(id));
    }

    @PostMapping("/products")
    @PreAuthorize(ADMIN_OR_DEBUG)
    public ApiResponse<ProductResponse> createProduct(@Valid @RequestBody ProductRequest request) {
        return ApiResponse.success("Success", productService.createProduct(request));
    }

    @PutMapping("/products/{id}")
    @PreAuthorize(ADMIN_OR_DEBUG)
    public ApiResponse<ProductResponse> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductRequest request) {
        return ApiResponse.success("Success", productService.updateProduct(id, request));
    }

    @DeleteMapping("/products/{id}")
    @PreAuthorize(ADMIN_OR_DEBUG)
    public ApiResponse<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ApiResponse.success("Success", null);
    }

    @GetMapping("/orders")
    @PreAuthorize(ADMIN_OR_DEBUG)
    public ApiResponse<List<com.almina.ecommerce.dto.order.OrderResponse>> getOrders() {
        return ApiResponse.success("Success", orderService.getAllOrders());
    }

    @PatchMapping("/orders/{orderId}")
    @PreAuthorize(ADMIN_OR_DEBUG)
    public ApiResponse<com.almina.ecommerce.dto.order.OrderResponse> updateOrderStatus(@PathVariable Long orderId, @RequestParam OrderStatus status) {
        return ApiResponse.success("Success", orderService.updateOrderStatus(orderId, status));
    }

    @PostMapping("/categories")
    @PreAuthorize(ADMIN_OR_DEBUG)
    public CategoryDto createCategory(@RequestBody CategoryDto request) {
        return categoryService.createCategory(request);
    }

    @PutMapping("/categories/{id}")
    @PreAuthorize(ADMIN_OR_DEBUG)
    public CategoryDto updateCategory(@PathVariable Long id, @RequestBody CategoryDto request) {
        return categoryService.updateCategory(id, request);
    }

    @DeleteMapping("/categories/{id}")
    @PreAuthorize(ADMIN_OR_DEBUG)
    public void deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
    }

    @GetMapping("/coupons")
    @PreAuthorize(ADMIN_OR_DEBUG)
    public List<CouponDto> getCoupons() {
        return couponService.getCoupons();
    }

    @PostMapping("/coupons")
    @PreAuthorize(ADMIN_OR_DEBUG)
    public CouponDto createCoupon(@RequestBody CouponDto request) {
        return couponService.createCoupon(request);
    }

    @PutMapping("/coupons/{id}")
    @PreAuthorize(ADMIN_OR_DEBUG)
    public CouponDto updateCoupon(@PathVariable Long id, @RequestBody CouponDto request) {
        return couponService.updateCoupon(id, request);
    }

    @DeleteMapping("/coupons/{id}")
    @PreAuthorize(ADMIN_OR_DEBUG)
    public void deleteCoupon(@PathVariable Long id) {
        couponService.deleteCoupon(id);
    }
}
