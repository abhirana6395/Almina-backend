package com.almina.ecommerce.dto.payment;

import com.almina.ecommerce.dto.order.AddressRequest;
import java.math.BigDecimal;

public record PaymentOrderRequest(
        AddressRequest shippingAddress,
        BigDecimal amount
) {
}
