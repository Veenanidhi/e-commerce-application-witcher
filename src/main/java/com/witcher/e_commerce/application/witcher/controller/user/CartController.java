package com.witcher.e_commerce.application.witcher.controller.user;

import com.witcher.e_commerce.application.witcher.entity.*;
import com.witcher.e_commerce.application.witcher.service.UserService;
import com.witcher.e_commerce.application.witcher.service.address.AddressService;
import com.witcher.e_commerce.application.witcher.service.cart.CartService;
import com.witcher.e_commerce.application.witcher.service.coupon.CouponService;
import com.witcher.e_commerce.application.witcher.service.order.OrderService;
import com.witcher.e_commerce.application.witcher.service.product.ProductService;
import com.witcher.e_commerce.application.witcher.service.transaction.TransactionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/user/cart")
public class CartController {

    private final ProductService productService;
    private final CartService cartService;
    private final OrderService orderService;
    private final UserService userService;

    private final TransactionService transactionService;



    private final CouponService couponService;

    private final AddressService addressService;

    public CartController(ProductService productService, CartService cartService, OrderService orderService, UserService userService, TransactionService transactionService, CouponService couponService, AddressService addressService) {
        this.productService = productService;
        this.cartService = cartService;
        this.orderService = orderService;
        this.userService = userService;
        this.transactionService = transactionService;
        this.couponService = couponService;
        this.addressService = addressService;
    }

    @GetMapping("/addToCart/{id}")
    public String addToCart(@PathVariable Long id, Model model) {
        Optional<Product> optionalProduct = productService.getProductById(id);
        User user = userService.getCurrentUser(); // Assume you have a method to get the logged-in user

        if (optionalProduct.isPresent()) {
            Product product = optionalProduct.get();

            try {
                int stock = Integer.parseInt(product.getStock());

                if (stock > 0) {
                    Cart cart = cartService.getUserCart(user);

                    // Check if the product is already in the cart
                    Optional<CartItem> existingCartItem = cartService.getCartItemByProduct(cart, product);
                    if (existingCartItem.isPresent()) {
                        // Increase quantity if product already exists in the cart
                        CartItem cartItem = existingCartItem.get();
                        cartItem.setQuantity(cartItem.getQuantity() + 1);
                    } else {
                        // Create new cart item
                        CartItem cartItem = new CartItem();
                        cartItem.setCart(cart);
                        cartItem.setProduct(product);
                        cartItem.setQuantity(1);
                        cartItem.setPrice(product.getPrice());
                        cart.getCartItem().add(cartItem); // Add item to the cart
                    }

                    product.setStock(String.valueOf(stock - 1)); // Reduce stock
                    productService.saveProduct(product); // Save the updated product
                    cartService.saveCart(cart); // Save cart

                    model.addAttribute("message", "Product added to cart successfully!");
                } else {
                    model.addAttribute("error", "Product is out of stock.");
                }
            } catch (NumberFormatException e) {
                model.addAttribute("error", "Invalid stock value for product.");
            }
        } else {
            model.addAttribute("error", "Product not found.");
        }

        return "redirect:/user/cart/";
    }

    @GetMapping("/")
    public String getCart(Model model) {
        User user = userService.getCurrentUser();
        Cart cart = cartService.getUserCart(user);

        model.addAttribute("cartCount", cart.getCartItem().size());
        model.addAttribute("total", cart.getCartItem().stream().mapToDouble(item -> item.getPrice() * item.getQuantity()).sum());
        model.addAttribute("cart", cart.getCartItem());

        return "cart";
    }

//    @GetMapping("/increment/{id}")
//    public String incrementCartItem(@PathVariable Long id) {
//        cartService.incrementCartItem(id);
//        return "redirect:/cart"; // Redirects to cart view
//    }

    @GetMapping("/increment/{id}")
    public String incrementCartItem(@PathVariable Long id, Model model) {
        Optional<CartItem> optionalCartItem = cartService.getCartItemById(id);

        if (optionalCartItem.isPresent()) {
            CartItem cartItem = optionalCartItem.get();
            Product product = cartItem.getProduct();
            int currentStock = Integer.parseInt(product.getStock());

            if (cartItem.getQuantity() < currentStock) {
                cartItem.setQuantity(cartItem.getQuantity() + 1);
                cartService.saveCartItem(cartItem); // Save the updated cart item
            } else {
                model.addAttribute("error", "Product is out of stock.");
            }
        }

        return "redirect:/user/cart/";
    }


//    @GetMapping("/decrement/{id}")
//    public String decrementCartItem(@PathVariable Long id) {
//        cartService.decrementCartItem(id);
//        return "redirect:/cart"; // Redirects to cart view
//    }

    @GetMapping("/decrement/{id}")
    public String decrementCartItem(@PathVariable Long id) {
        Optional<CartItem> optionalCartItem = cartService.getCartItemById(id);

        if (optionalCartItem.isPresent()) {
            CartItem cartItem = optionalCartItem.get();
            if (cartItem.getQuantity() > 1) {
                cartItem.setQuantity(cartItem.getQuantity() - 1);
                cartService.saveCartItem(cartItem); // Save the updated cart item
            }
        }

        return "redirect:/user/cart/";
    }




    @GetMapping("/removeProduct/{cartItemId}")
    public String cartProductRemove(@PathVariable Long cartItemId) {
        Optional<CartItem> optionalCartItem = cartService.getCartItemById(cartItemId);

        if (optionalCartItem.isPresent()) {
            CartItem cartItem = optionalCartItem.get();
            Product product = cartItem.getProduct();

            try {
                int stock = Integer.parseInt(product.getStock());
                product.setStock(String.valueOf(stock + cartItem.getQuantity())); // Increase stock
                productService.saveProduct(product); // Save updated product

                cartService.removeCartItem(cartItem); // Remove item from cart
            } catch (NumberFormatException e) {
                // Handle error if stock is not a valid number
            }
        }

        return "redirect:/user/cart/";
    }

    @GetMapping("/checkout")
    public String checkout(Model model, @RequestParam(value = "couponId", required = false) Long couponId) {
        User user = userService.getCurrentUser();
        Cart cart = cartService.getUserCart(user);
        if (cart == null || cart.getCartItem() == null || cart.getCartItem().isEmpty()){
            return "redirect:/productPage";
        }

        double totalAmount = cart.getCartItem().stream().mapToDouble(item -> item.getPrice() * item.getQuantity()).sum();

        // Check if a coupon is applied
        if (couponId != null) {
            Optional<Coupon> optionalCoupon = couponService.getCouponById(couponId);

            if (optionalCoupon.isPresent() && optionalCoupon.get().isActive()) {
                Coupon coupon = optionalCoupon.get();

                // Check if total is above the coupon's minimum purchase amount
                if (totalAmount >= coupon.getMinimumPurchaseAmount()) {
                    totalAmount -= coupon.getAmount(); // Subtract coupon amount
                }
            }
        }


        // Fetch all addresses for the user
        List<Address> addresses = addressService.getAllAddressesByUser(user);
        List<Coupon> activeCoupons = couponService.getAllCoupons();



        model.addAttribute("coupons", activeCoupons);
        model.addAttribute("user", user);
        model.addAttribute("cartCount", cart.getCartItem().size());
        model.addAttribute("paymentAmount", totalAmount);
        model.addAttribute("addresses", addresses);

        return "checkout";
    }

    @GetMapping("/applyCoupon")
    public String applyCoupon(@RequestParam(value = "couponId") Long couponId, Model model) {
        User user = userService.getCurrentUser();
        Cart cart = cartService.getUserCart(user);

        // Check if the cart is empty
        if (cart == null || cart.getCartItem() == null || cart.getCartItem().isEmpty()) {
            model.addAttribute("error", "Your cart is empty.");
            model.addAttribute("coupons",couponService.getAllCoupons());
            return "checkout"; // Redirect to checkout or cart page
        }

        Optional<Coupon> optionalCoupon = couponService.getCouponById(couponId);

        // Apply the coupon if it's present and active
        if (optionalCoupon.isPresent() && optionalCoupon.get().isActive()) {
            Coupon coupon = optionalCoupon.get();
            double totalAmount = cart.getCartItem().stream()
                    .mapToDouble(item -> item.getPrice() * item.getQuantity())
                    .sum();

            // Check minimum purchase amount
            if (totalAmount >= coupon.getMinimumPurchaseAmount()) {
                double discountedAmount = totalAmount - coupon.getAmount();
                model.addAttribute("success", "Coupon applied successfully!");
                model.addAttribute("discountedAmount", discountedAmount);
            } else {
                model.addAttribute("error", "Coupon cannot be applied. Minimum purchase amount is required.");
            }
        } else {
            model.addAttribute("error", "Invalid or inactive coupon.");
        }

        // Return to checkout page with model attributes
        model.addAttribute("coupons", couponService.getAllCoupons());
        return "checkout";
    }

    @GetMapping("/removeCoupon")
    public String removeCoupon(Model model) {
        User user = userService.getCurrentUser();
        Cart cart = cartService.getUserCart(user);

        // Check if the cart is empty
        if (cart == null || cart.getCartItem() == null || cart.getCartItem().isEmpty()) {
            model.addAttribute("error", "Your cart is empty.");
            model.addAttribute("coupons", couponService.getAllCoupons()); // Ensure coupons are displayed
            return "redirect:/checkout"; // Redirect to checkout or cart page
        }

        // Restore original total amount
        double totalAmount = cart.getCartItem().stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();

        model.addAttribute("success", "Coupon removed successfully.");
        model.addAttribute("paymentAmount", totalAmount); // Reset to the original amount
        model.addAttribute("coupons", couponService.getAllCoupons()); // Ensure coupons are displayed

        return "checkout"; // Return to checkout page
    }

    @PostMapping("/payNow")
    public String payNow (Model model) {
        User user = userService.getCurrentUser();
        Cart cart = cartService.getUserCart(user);


        double totalAmount = cart.getCartItem().stream().mapToDouble(item -> item.getPrice() * item.getQuantity()).sum();


        for (CartItem cartItem : cart.getCartItem()) {
            Orders order = new Orders();
            order.setItemCount(cartItem.getQuantity());
            order.setProduct(cartItem.getProduct());
            order.setOrderStatus("Processing");
            order.setOrderDate(new Date());
            order.setTotalAmount(cartItem.getPrice() * cartItem.getQuantity());
            order.setUser(user);


            orderService.saveOrder(order);
        }

        model.addAttribute("paymentAmount", totalAmount);
        model.addAttribute("paymentMethod", "razorpay");
        model.addAttribute("user", user);

        return "payment";
    }












    }










