package com.witcher.e_commerce.application.witcher.entity;

import com.witcher.e_commerce.application.witcher.service.PaymentMethod;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Entity
public class PurchasedOrders {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    @OneToOne
    private Address address;

    @OneToMany(mappedBy = "order" ,cascade = CascadeType.ALL)
    private List<Orders> orderItems = new ArrayList<>();

    @ManyToOne
    private User user;

    @Enumerated(EnumType.STRING) // Store the enum as a string in the database
    private PaymentMethod paymentMethod;

    private LocalDateTime orderDate;

    private Double orderAmount;

    private String paymentStatus;

    private boolean refund_used= false;

    @ManyToOne
    private Coupon coupon;

    public PurchasedOrders() {
    }

    public PurchasedOrders(Long orderId, Address address, List<Orders> orderItems, User user, PaymentMethod paymentMethod, LocalDateTime orderDate, Double orderAmount, String paymentStatus, boolean refund_used, Coupon coupon) {
        this.orderId = orderId;
        this.address = address;
        this.orderItems = orderItems;
        this.user = user;
        this.paymentMethod = paymentMethod;
        this.orderDate = orderDate;
        this.orderAmount = orderAmount;
        this.paymentStatus = paymentStatus;
        this.refund_used = refund_used;
        this.coupon = coupon;
    }



    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public List<Orders> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<Orders> orderItems) {
        this.orderItems = orderItems;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getOrderDate(LocalDateTime now) {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public Double getOrderAmount() {
        return orderAmount;
    }

    public void setOrderAmount(Double orderAmount) {
        this.orderAmount = orderAmount;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public boolean isRefund_used() {
        return refund_used;
    }

    public void setRefund_used(boolean refund_used) {
        this.refund_used = refund_used;
    }

    public Coupon getCoupon() {
        return coupon;
    }

    public void setCoupon(Coupon coupon) {
        this.coupon = coupon;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }
}
