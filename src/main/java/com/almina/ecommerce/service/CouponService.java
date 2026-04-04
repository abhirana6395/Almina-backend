package com.almina.ecommerce.service;

import com.almina.ecommerce.dto.product.CouponDto;
import java.util.List;

public interface CouponService {
    List<CouponDto> getCoupons();
    CouponDto createCoupon(CouponDto request);
    CouponDto updateCoupon(Long id, CouponDto request);
    void deleteCoupon(Long id);
}
