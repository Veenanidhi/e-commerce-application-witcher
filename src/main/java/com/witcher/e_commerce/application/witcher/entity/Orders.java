package com.witcher.e_commerce.application.witcher.entity;


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

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "id")
    private User user;


    private String orderStatus;   // 'Processing', 'Shipped', 'Delivered', etc.


    private Date orderDate;

    private Double totalAmount;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private PurchasedOrders order;


    public Orders() {
        this.orderNumber = "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    public Orders(Long orderItemId, Integer itemCount, String orderNumber, Product product, User user, String orderStatus, Date orderDate, Double totalAmount ) {
        this.orderItemId = orderItemId;
        ItemCount = itemCount;
        this.orderNumber = orderNumber;
        this.product = product;
        this.user = user;
        this.orderStatus = orderStatus;
        this.orderDate = orderDate;
        this.totalAmount = totalAmount;

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
}
