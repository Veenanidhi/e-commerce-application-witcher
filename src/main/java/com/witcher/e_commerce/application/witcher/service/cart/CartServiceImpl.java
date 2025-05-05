package com.witcher.e_commerce.application.witcher.service.cart;

import com.witcher.e_commerce.application.witcher.dao.CartItemRepository;
import com.witcher.e_commerce.application.witcher.dao.CartRepository;
import com.witcher.e_commerce.application.witcher.dao.ProductRepository;
import com.witcher.e_commerce.application.witcher.entity.*;
import com.witcher.e_commerce.application.witcher.service.coupon.CouponService;
import com.witcher.e_commerce.application.witcher.service.product.ProductService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;

    private final CartItemRepository cartItemRepository;

    private final ProductService productService;

    private final ProductRepository productRepository;

    private final CouponService couponService;

    public CartServiceImpl(CartRepository cartRepository, CartItemRepository cartItemRepository, ProductService productService, ProductRepository productRepository, CouponService couponService) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productService = productService;
        this.productRepository = productRepository;
        this.couponService = couponService;

    }


    @Override
    public Cart getUserCart(User user) {
        Optional<Cart> optionalCart = cartRepository.findByUser(user);
        if (optionalCart.isPresent()) {
            return optionalCart.get();
        } else {

            Cart newCart = new Cart();
            newCart.setUser(user);
            return cartRepository.save(newCart);
        }
    }

    @Override
    public Optional<CartItem> getCartItemByProduct(Cart cart, Product product) {
        return cartItemRepository.findByCartAndProduct(cart, product);
    }

    @Override
    public Cart saveCart(Cart cart) {
        return cartRepository.save(cart);
    }

    @Override
    public Optional<CartItem> getCartItemById(Long cartItemId) {
        return cartItemRepository.findById(cartItemId);
    }

    @Override
    public void removeCartItem(CartItem cartItem) {
        cartItemRepository.delete(cartItem);

    }

    @Override
    public void clearCart(Cart cart) {
        cart.getCartItem().clear();
        cartRepository.save(cart);
    }

    @Override
    public void incrementCartItem(Long cartItemId) {
        Optional<CartItem> optionalCartItem = cartItemRepository.findById(cartItemId);
        if (optionalCartItem.isPresent()) {
            CartItem cartItem = optionalCartItem.get();
            Product product = cartItem.getProduct();
            int currentQuantity = cartItem.getQuantity();
            int productStock = Integer.parseInt(product.getStock());


            if (productStock > 0) {
                cartItem.setQuantity(currentQuantity + 1);
                cartItemRepository.save(cartItem);

                // Reduce product stock by 1
                product.setStock(String.valueOf(productStock - 1));
                productService.saveProduct(product);
            } else {
                throw new IllegalArgumentException("Insufficient stock for product: " + product.getName());
            }
        } else {
            throw new IllegalArgumentException("CartItem not found for id: " + cartItemId);
        }
    }



    @Override
    public void decrementCartItem(Long cartItemId) {
        Optional<CartItem> optionalCartItem = cartItemRepository.findById(cartItemId);
        if (optionalCartItem.isPresent()) {
            CartItem cartItem = optionalCartItem.get();
            Product product = cartItem.getProduct();
            int currentQuantity = cartItem.getQuantity();

            // Decrement quantity if more than 1
            if (currentQuantity > 1) {
                cartItem.setQuantity(currentQuantity - 1);
                cartItemRepository.save(cartItem);

                // Update product stock (increment stock back by 1)
                int productStock = Integer.parseInt(product.getStock());
                product.setStock(String.valueOf(productStock + 1));
                productService.saveProduct(product);

            } else {
                throw new IllegalArgumentException("Cannot decrement, product quantity is already at the minimum.");
            }
        } else {
            throw new IllegalArgumentException("CartItem not found for id: " + cartItemId);
        }
    }

    @Override
    public void saveCartItem(CartItem cartItem) {
        cartItemRepository.save(cartItem);

    }

    @Override
    public boolean applyCouponToCart(Long userId, Long couponId) {
        // Fetch the user's cart
        Cart cart = cartRepository.findByUserId(userId);
        if (cart == null) {
            return false; // User cart not found
        }

        // Fetch the coupon
        Optional<Coupon> optionalCoupon = couponService.getCouponById(couponId);
        if (optionalCoupon.isEmpty() || !optionalCoupon.get().isActive()) {
            return false; // Invalid or inactive coupon
        }

        Coupon coupon = optionalCoupon.get();

        // Check if the cart's total meets the minimum purchase amount
        double totalCartAmount = cart.getTotalAmount(); // Assuming you have a method to calculate total
        if (totalCartAmount < coupon.getMinimumPurchaseAmount()) {
            return false;
        }

        // Check if the coupon has reached its usage limit
        if (coupon.getUsageCount() <= 0) {
            return false; // Coupon usage limit reached
        }

        // Apply the coupon logic, e.g., discount calculation
        double discountAmount = coupon.getAmount();
        double newTotalAmount = totalCartAmount - discountAmount;

        // Prevent total from becoming negative
        if (newTotalAmount < 0) {
            newTotalAmount = 0;
        }

        // Update the cart's total amount and applied coupon
        cart.setTotalAmount(newTotalAmount);
        cart.setAppliedCoupon(coupon);
        cartRepository.save(cart);

        // Decrease coupon usage count
        coupon.setUsageCount(coupon.getUsageCount() - 1);
        couponService.saveCoupon(coupon); // Save the updated coupon if you have a method for this

        return true;
    }

    @Override
    public double calculateTotalAmount(Long userId) {

        Cart cart = cartRepository.findByUserId(userId);
        if (cart == null) {
            return 0.0;
        }

        // Calculate total amount of items in the cart
        double totalAmount = 0.0;
        for (CartItem item : cart.getCartItem()) {
            Product product = productRepository.findById(item.getCartItemId())
                    .orElseThrow(() -> new IllegalArgumentException("Product not found"));
            totalAmount += product.getPrice() * item.getQuantity();
        }


        if (cart.getAppliedCoupon() != null) {
            totalAmount -= cart.getAppliedCoupon().getAmount();
        }

        return totalAmount;
    }



    }







