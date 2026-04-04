package com.almina.ecommerce.dto.order;

import com.almina.ecommerce.entity.PaymentMethod;

public record OrderCreateRequest(
        AddressRequest shippingAddress,
        PaymentMethod paymentMethod
) {
}
