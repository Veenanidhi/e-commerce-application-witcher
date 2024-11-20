package com.witcher.e_commerce.application.witcher.entity;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "coupon")
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long couponId;

    private String couponName;

    private String description;

    private Double amount;

    private Double minimumPurchaseAmount;

    private boolean isActive= true;

    private Integer usageCount;

    @ManyToMany(mappedBy = "coupons" ,  cascade = CascadeType.ALL)
    private Set<User> user = new HashSet<>();

    @OneToMany(mappedBy="coupon")
    private List<PurchasedOrders> ordersList;

    public Coupon() {
    }

    public Coupon(Long couponId, String couponName, String description, Double amount, Double minimumPurchaseAmount, boolean isActive, Integer usageCount, Set<User> user) {
        this.couponId = couponId;
        this.couponName = couponName;
        this.description = description;
        this.amount = amount;
        this.minimumPurchaseAmount = minimumPurchaseAmount;
        this.isActive = isActive;
        this.usageCount = usageCount;
        this.user = user;
    }

    public Long getCouponId() {
        return couponId;
    }

    public void setCouponId(Long couponId) {
        this.couponId = couponId;
    }

    public String getCouponName() {
        return couponName;
    }

    public void setCouponName(String couponName) {
        this.couponName = couponName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Double getMinimumPurchaseAmount() {
        return minimumPurchaseAmount;
    }

    public void setMinimumPurchaseAmount(Double minimumPurchaseAmount) {
        this.minimumPurchaseAmount = minimumPurchaseAmount;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public Integer getUsageCount() {
        return usageCount;
    }

    public void setUsageCount(Integer usageCount) {
        this.usageCount = usageCount;
    }

    public Set<User> getUser() {
        return user;
    }

    public void setUser(Set<User> user) {
        this.user = user;
    }

    public void setIsActive(boolean active) {
    }
}
