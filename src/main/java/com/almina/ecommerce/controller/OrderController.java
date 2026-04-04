package com.almina.ecommerce.controller;

import com.almina.ecommerce.dto.order.BuyNowOrderRequest;
import com.almina.ecommerce.dto.order.OrderCreateRequest;
import com.almina.ecommerce.dto.order.OrderResponse;
import com.almina.ecommerce.dto.payment.RazorpayOrderResponse;
import com.almina.ecommerce.service.OrderService;
import com.almina.ecommerce.service.PaymentService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final PaymentService paymentService;

    @PostMapping
    public OrderResponse createOrder(@RequestBody OrderCreateRequest request) {
        return orderService.placeOrder(request);
    }

    @PostMapping("/buy-now")
    public RazorpayOrderResponse buyNow(@Valid @RequestBody BuyNowOrderRequest request) {
        return paymentService.createBuyNowOrder(request);
    }

    @GetMapping
    public List<OrderResponse> getUserOrders() {
        return orderService.getUserOrders();
    }
}
