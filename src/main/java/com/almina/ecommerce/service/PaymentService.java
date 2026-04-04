package com.almina.ecommerce.service;

import com.almina.ecommerce.dto.order.BuyNowOrderRequest;
import com.almina.ecommerce.dto.order.OrderResponse;
import com.almina.ecommerce.dto.payment.PaymentOrderRequest;
import com.almina.ecommerce.dto.payment.RazorpayOrderResponse;
import com.almina.ecommerce.dto.payment.RazorpayVerifyRequest;

public interface PaymentService {
    RazorpayOrderResponse createRazorpayOrder(PaymentOrderRequest request);
    RazorpayOrderResponse createBuyNowOrder(BuyNowOrderRequest request);
    OrderResponse verifyRazorpayPayment(RazorpayVerifyRequest request);
}
