package com.witcher.e_commerce.application.witcher.service.coupon;

import com.witcher.e_commerce.application.witcher.entity.Coupon;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface CouponService {


    List<Coupon> getAllCoupons();

    Coupon createCoupon(Coupon coupon);

    Optional<Coupon> getCouponById(Long couponId);

    Coupon updateCoupon(Long id, Coupon coupon);

    void deleteCoupon(Long id);

    Coupon saveCoupon(Coupon coupon);


    Coupon findCouponById(Long couponId);
}
