package com.almina.ecommerce.controller;

import com.almina.ecommerce.dto.order.OrderResponse;
import com.almina.ecommerce.dto.payment.PaymentOrderRequest;
import com.almina.ecommerce.dto.payment.RazorpayOrderResponse;
import com.almina.ecommerce.dto.payment.RazorpayVerifyRequest;
import com.almina.ecommerce.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/api/order", "/api/payment"})
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/create-order")
    public RazorpayOrderResponse createOrder(@RequestBody PaymentOrderRequest request) {
        return paymentService.createRazorpayOrder(request);
    }

    @PostMapping("/verify")
    public OrderResponse verifyPayment(@RequestBody RazorpayVerifyRequest request) {
        return paymentService.verifyRazorpayPayment(request);
    }
}
