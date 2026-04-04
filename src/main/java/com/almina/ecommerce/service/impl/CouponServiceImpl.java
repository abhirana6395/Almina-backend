package com.almina.ecommerce.service.impl;

import com.almina.ecommerce.dto.product.CouponDto;
import com.almina.ecommerce.entity.Coupon;
import com.almina.ecommerce.exception.ResourceNotFoundException;
import com.almina.ecommerce.repository.CouponRepository;
import com.almina.ecommerce.service.CouponService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {

    private final CouponRepository couponRepository;

    @Override
    public List<CouponDto> getCoupons() {
        return couponRepository.findAll().stream().map(this::toDto).toList();
    }

    @Override
    public CouponDto createCoupon(CouponDto request) {
        Coupon coupon = new Coupon();
        apply(coupon, request);
        return toDto(couponRepository.save(coupon));
    }

    @Override
    public CouponDto updateCoupon(Long id, CouponDto request) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon not found"));
        apply(coupon, request);
        return toDto(couponRepository.save(coupon));
    }

    @Override
    public void deleteCoupon(Long id) {
        couponRepository.deleteById(id);
    }

    private CouponDto toDto(Coupon coupon) {
        return new CouponDto(
                coupon.getId(),
                coupon.getCode(),
                coupon.getDiscountPercentage(),
                coupon.getMaxDiscountAmount(),
                coupon.getValidUntil(),
                coupon.getActive()
        );
    }

    private void apply(Coupon coupon, CouponDto request) {
        coupon.setCode(request.code());
        coupon.setDiscountPercentage(request.discountPercentage());
        coupon.setMaxDiscountAmount(request.maxDiscountAmount());
        coupon.setValidUntil(request.validUntil());
        coupon.setActive(request.active());
    }
}
