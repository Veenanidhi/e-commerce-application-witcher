package com.witcher.e_commerce.application.witcher.entity;


import com.witcher.e_commerce.application.witcher.service.PaymentMethod;
import jakarta.persistence.*;
import lombok.ToString;

import java.util.Date;
import java.util.UUID;

@Entity
@ToString
public class Orders {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderItemId;

    private Integer ItemCount;

    private String orderNumber;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "id")
    private User user;

    private String razorpayOrderId;

    private String RazorpayPaymentId;


    private String orderStatus;   // 'Processing', 'Shipped', 'Delivered', etc.

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    private Date orderDate;

    private Double totalAmount;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private PurchasedOrders order;

    @ManyToOne
    private Coupon coupon;

    @Column(name = "payment_status")
    private String paymentStatus;






    public Orders() {
        this.orderNumber = "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    public Orders(Long orderItemId, Integer itemCount, String orderNumber, Product product, User user, String razorpayOrderId, String orderStatus, PaymentMethod paymentMethod, Date orderDate, Double totalAmount, PurchasedOrders order, Coupon coupon, String paymentStatus) {
        this.orderItemId = orderItemId;
        ItemCount = itemCount;
        this.orderNumber = orderNumber;
        this.product = product;
        this.user = user;
        this.razorpayOrderId = razorpayOrderId;
        this.orderStatus = orderStatus;
        this.paymentMethod = paymentMethod;
        this.orderDate = orderDate;
        this.totalAmount = totalAmount;
        this.order = order;
        this.coupon = coupon;
        this.paymentStatus = paymentStatus;
    }

    public String getRazorpayPaymentId() {
        return RazorpayPaymentId;
    }

    public void setRazorpayPaymentId(String razorpayPaymentId) {
        RazorpayPaymentId = razorpayPaymentId;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getRazorpayOrderId() {
        return razorpayOrderId;
    }

    public void setRazorpayOrderId(String razorpayOrderId) {
        this.razorpayOrderId = razorpayOrderId;
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

    public Long getOrderItemId() {
        return orderItemId;
    }

    public void setOrderItemId(Long orderItemId) {
        this.orderItemId = orderItemId;
    }

    public Integer getItemCount() {
        return ItemCount;
    }

    public void setItemCount(Integer itemCount) {
        ItemCount = itemCount;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public PurchasedOrders getOrder() {
        return order;
    }

    public void setOrder(PurchasedOrders order) {
        this.order = order;
    }

}
