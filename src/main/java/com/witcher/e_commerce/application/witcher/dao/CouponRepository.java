package com.witcher.e_commerce.application.witcher.dao;

import com.witcher.e_commerce.application.witcher.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {



}
