package com.witcher.e_commerce.application.witcher.service.cart;

import com.witcher.e_commerce.application.witcher.entity.*;

import java.util.Optional;

public interface CartService {
    Cart getUserCart(User user);

    Optional<CartItem> getCartItemByProduct(Cart cart, Product product);

    Cart saveCart(Cart cart);

    Optional<CartItem> getCartItemById(Long cartItemId);

    void removeCartItem(CartItem cartItem);

    void clearCart(Cart cart);

    void incrementCartItem(Long cartItemId);

    void decrementCartItem(Long cartItemId);

    void saveCartItem(CartItem cartItem);


    boolean applyCouponToCart(Long userId, Long couponId);

    double calculateTotalAmount(Long userId);
}
