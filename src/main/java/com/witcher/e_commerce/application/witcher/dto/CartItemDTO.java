package com.witcher.e_commerce.application.witcher.dto;

import com.witcher.e_commerce.application.witcher.entity.CartItem;

public class CartItemDTO {
    private CartItem cartItem;
    private double finalPrice;
    private String appliedOfferType;

    public CartItemDTO() {
    }

    public CartItemDTO(CartItem cartItem, double finalPrice, String appliedOfferType) {
        this.cartItem = cartItem;
        this.finalPrice = finalPrice;
        this.appliedOfferType = appliedOfferType;
    }

    public CartItem getCartItem() {
        return cartItem;
    }

    public void setCartItem(CartItem cartItem) {
        this.cartItem = cartItem;
    }

    public double getFinalPrice() {
        return finalPrice;
    }

    public void setFinalPrice(double finalPrice) {
        this.finalPrice = finalPrice;
    }

    public String getAppliedOfferType() {
        return appliedOfferType;
    }

    public void setAppliedOfferType(String appliedOfferType) {
        this.appliedOfferType = appliedOfferType;
    }
}
