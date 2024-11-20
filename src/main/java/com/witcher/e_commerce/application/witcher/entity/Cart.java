package com.witcher.e_commerce.application.witcher.entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.util.List;

@Getter
@Entity
@Table(name = "cart")
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_id")
    private Long cartId;

    @ManyToOne
    @JoinColumn(name = "id")
    private User user;

    @OneToMany(mappedBy = "cart",cascade = CascadeType.ALL,orphanRemoval = true)
    private List <CartItem> cartItem;

    private double totalAmount;

    @ManyToOne // Assuming a Cart can apply one Coupon
    @JoinColumn(name = "coupon_id") // Specify the column name for the foreign key
    private Coupon appliedCoupon; // Add this field to store the applied coupo

    public Cart() {
    }

    public Cart(Long cartId, User user, List<CartItem> cartItem, double totalAmount, Coupon appliedCoupon) {
        this.cartId = cartId;
        this.user = user;
        this.cartItem = cartItem;
        this.totalAmount = totalAmount;
        this.appliedCoupon = appliedCoupon;
    }

    public double getTotalAmount() {
        double total = 0.0;
        for (CartItem item : cartItem) {
            total += item.getProduct().getPrice() * item.getQuantity(); // Assuming getPrice() returns the product price
        }
        return total;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public void setCartId(Long cartId) {
        this.cartId = cartId;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setCartItem(List<CartItem> cartItem) {
        this.cartItem = cartItem;
    }

    public Long getCartId() {
        return cartId;
    }

    public User getUser() {
        return user;
    }

    public List<CartItem> getCartItem() {
        return cartItem;
    }

    public Coupon getAppliedCoupon() {
        return appliedCoupon;
    }

    public void setAppliedCoupon(Coupon appliedCoupon) {
        this.appliedCoupon = appliedCoupon;
    }
}
