package com.almina.ecommerce.service.impl;

import com.almina.ecommerce.dto.auth.AdminLoginRequest;
import com.almina.ecommerce.dto.auth.AuthResponse;
import com.almina.ecommerce.entity.Role;
import com.almina.ecommerce.exception.BadRequestException;
import com.almina.ecommerce.mapper.EntityMapper;
import com.almina.ecommerce.entity.OrderStatus;
import com.almina.ecommerce.repository.OrderRepository;
import com.almina.ecommerce.repository.ProductRepository;
import com.almina.ecommerce.repository.UserRepository;
import com.almina.ecommerce.security.JwtService;
import com.almina.ecommerce.service.AdminService;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final EntityMapper entityMapper;

    @Override
    public AuthResponse login(AdminLoginRequest request) {
        var user = userRepository.findByEmail(request.email().toLowerCase())
                .orElseThrow(() -> new BadRequestException("Invalid admin credentials"));

        if (user.getRole() != Role.ADMIN || !passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new BadRequestException("Invalid admin credentials");
        }

        return new AuthResponse(jwtService.generateToken(user), entityMapper.toUserResponse(user));
    }

    @Override
    public Map<String, Object> getDashboardAnalytics() {
        var orders = orderRepository.findAll();
        BigDecimal revenue = orders.stream()
                .filter(order -> order.getStatus() == OrderStatus.CONFIRMED
                        || order.getStatus() == OrderStatus.SHIPPED
                        || order.getStatus() == OrderStatus.DELIVERED)
                .map(order -> order.getTotalAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return Map.of(
                "totalOrders", orders.size(),
                "totalUsers", userRepository.count(),
                "totalRevenue", revenue,
                "totalProducts", productRepository.count(),
                "orderStatusBreakdown", List.of(
                        Map.of("label", "Pending", "value", orders.stream().filter(o -> o.getStatus() == OrderStatus.PENDING).count()),
                        Map.of("label", "Confirmed", "value", orders.stream().filter(o -> o.getStatus() == OrderStatus.CONFIRMED).count()),
                        Map.of("label", "Shipped", "value", orders.stream().filter(o -> o.getStatus() == OrderStatus.SHIPPED).count()),
                        Map.of("label", "Delivered", "value", orders.stream().filter(o -> o.getStatus() == OrderStatus.DELIVERED).count()),
                        Map.of("label", "Cancelled", "value", orders.stream().filter(o -> o.getStatus() == OrderStatus.CANCELLED).count())
                )
        );
    }
}
