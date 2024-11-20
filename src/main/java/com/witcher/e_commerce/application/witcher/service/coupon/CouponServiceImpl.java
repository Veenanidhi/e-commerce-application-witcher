package com.witcher.e_commerce.application.witcher.service.coupon;

import com.witcher.e_commerce.application.witcher.dao.CouponRepository;
import com.witcher.e_commerce.application.witcher.entity.Coupon;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CouponServiceImpl implements CouponService{

    private final CouponRepository couponRepository;

    public CouponServiceImpl(CouponRepository couponRepository) {
        this.couponRepository = couponRepository;
    }

    public List<Coupon> getAllCoupons() {
        return couponRepository.findAll();
    }

    public Optional<Coupon> getCouponById(Long couponId) {
        return couponRepository.findById(couponId); // Assuming you're using a JPA repository
    }


    public Coupon createCoupon(Coupon coupon) {
        return couponRepository.save(coupon);
    }

    public Coupon updateCoupon(Long couponId, Coupon updatedCoupon) {
        return couponRepository.findById(couponId)
                .map(coupon -> {
                    coupon.setCouponName(updatedCoupon.getCouponName());
                    coupon.setDescription(updatedCoupon.getDescription());
                    coupon.setAmount(updatedCoupon.getAmount());
                    coupon.setUsageCount(updatedCoupon.getUsageCount());
                    coupon.setMinimumPurchaseAmount(updatedCoupon.getMinimumPurchaseAmount());
                    coupon.setIsActive(updatedCoupon.isActive());
                    return couponRepository.save(coupon);
                })
                .orElse(null);
    }

    public void deleteCoupon(Long couponId) {
        couponRepository.deleteById(couponId);
    }

    @Override
    public Coupon saveCoupon(Coupon coupon) {
        return couponRepository.save(coupon);
    }

    @Override
    public Coupon findCouponById(Long couponId) {
        Optional<Coupon> optionalCoupon = couponRepository.findById(couponId);
        if (optionalCoupon.isPresent()) {
            return optionalCoupon.get();
        } else {
            throw new IllegalArgumentException("Coupon not found with ID: " + couponId);
        }
    }


}
