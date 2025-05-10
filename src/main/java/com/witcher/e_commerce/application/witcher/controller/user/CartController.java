package com.witcher.e_commerce.application.witcher.controller.user;

import com.razorpay.RazorpayClient;
import com.witcher.e_commerce.application.witcher.dto.PaymentFailureRequest;
import com.witcher.e_commerce.application.witcher.dto.PaymentSuccessRequest;
import com.witcher.e_commerce.application.witcher.entity.*;
import com.witcher.e_commerce.application.witcher.service.UserService;
import com.witcher.e_commerce.application.witcher.service.address.AddressService;
import com.witcher.e_commerce.application.witcher.service.cart.CartService;
import com.witcher.e_commerce.application.witcher.service.coupon.CouponService;
import com.witcher.e_commerce.application.witcher.service.order.OrderService;
import com.witcher.e_commerce.application.witcher.service.product.ProductService;
import com.witcher.e_commerce.application.witcher.service.transaction.TransactionService;
import com.witcher.e_commerce.application.witcher.service.wallet.WalletService;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


import java.util.*;

@Controller
@RequestMapping("/user/cart")
public class CartController {

    private final ProductService productService;
    private final CartService cartService;
    private final OrderService orderService;
    private final UserService userService;
    private final WalletService walletService;

    private final TransactionService transactionService;


    private final CouponService couponService;

    private final AddressService addressService;

    public CartController(ProductService productService, CartService cartService, OrderService orderService, UserService userService, WalletService walletService, TransactionService transactionService, CouponService couponService, AddressService addressService) {
        this.productService = productService;
        this.cartService = cartService;
        this.orderService = orderService;
        this.userService = userService;
        this.walletService = walletService;
        this.transactionService = transactionService;
        this.couponService = couponService;
        this.addressService = addressService;
    }


    @GetMapping("/addToCart/{id}")
    public String addToCart(@PathVariable Long id, Model model) {
        Optional<Product> optionalProduct = productService.getProductById(id);
        User user = userService.getCurrentUser();

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

        double total = cart.getCartItem().stream().mapToDouble(item -> {
            Product product = item.getProduct();
            double basePrice = product.getPrice();
            double finalPrice = basePrice;

            // Apply product offer - using only the first offer like in your template
            if (product.getProductOffers() != null && !product.getProductOffers().isEmpty()) {
                ProductOffer offer = product.getProductOffers().get(0);
                // Removed the isEnabled() and isActive() checks to match template
                double discountedPrice = basePrice - (basePrice * offer.getDiscountPercentage() / 100);
                finalPrice = discountedPrice;
            }
            // Apply category offer - making validation check match template
            else if (product.getCategoryOffer() != null && product.getCategoryOffer().isActive()
                    && product.getCategoryOffer().isActive()) {
                double categoryDiscountedPrice = basePrice - (basePrice * product.getCategoryOffer().getDiscountPercentage() / 100);
                finalPrice = categoryDiscountedPrice;
            }

            // Multiply by quantity
            return finalPrice * item.getQuantity();
        }).sum();

        // Format the total to two decimal places for consistency
        model.addAttribute("cartCount", cart.getCartItem().size());
        model.addAttribute("total", String.format("%.2f", total));
        model.addAttribute("cart", cart.getCartItem());

        return "cart";
    }


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
                cartService.saveCartItem(cartItem);
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
                productService.saveProduct(product);

                cartService.removeCartItem(cartItem);
            } catch (NumberFormatException e) {

            }
        }

        return "redirect:/productPage";
    }


//    @GetMapping("/checkout")
//    public String checkout(Model model, @RequestParam(value = "couponId", required = false) Long couponId) {
//        User user = userService.getCurrentUser();
//        Cart cart = cartService.getUserCart(user);
//
//        if (cart == null || cart.getCartItem() == null || cart.getCartItem().isEmpty()) {
//            return "redirect:/productPage";
//        }
//
//        double totalAmount = cart.getCartItem().stream().mapToDouble(item -> {
//            Product product = item.getProduct();
//            double basePrice = product.getPrice();
//            double finalPrice = basePrice;
//
//            // Apply product offer - same logic as in getCart
//            if (product.getProductOffers() != null && !product.getProductOffers().isEmpty()) {
//                ProductOffer offer = product.getProductOffers().get(0);
//                // Removed isActive check to match template logic
//                double discountedPrice = basePrice - (basePrice * offer.getDiscountPercentage() / 100);
//                finalPrice = discountedPrice;
//            }
//            // Apply category offer - same logic as in getCart
//            else if (product.getCategoryOffer() != null && product.getCategoryOffer().isActive()
//                    && product.getCategoryOffer().isActive()) {
//                double categoryDiscountedPrice = basePrice - (basePrice * product.getCategoryOffer().getDiscountPercentage() / 100);
//                finalPrice = categoryDiscountedPrice;
//            }
//
//            // Multiply by quantity
//            return finalPrice * item.getQuantity();
//        }).sum();
//
//        // Apply coupon if provided
//        if (couponId != null) {
//            Optional<Coupon> optionalCoupon = couponService.getCouponById(couponId);
//
//            if (optionalCoupon.isPresent()) {
//                Coupon coupon = optionalCoupon.get();
//
//                if (coupon.isActive() && totalAmount >= coupon.getMinimumPurchaseAmount() && coupon.getUsageCount() > 0) {
//                    totalAmount -= coupon.getAmount();
//                    cart.setTotalAmount(totalAmount);
//                    cartService.saveCart(cart);
//
//                    coupon.setUsageCount(coupon.getUsageCount() - 1);
//                    couponService.saveCoupon(coupon);
//                    model.addAttribute("success", "Coupon applied successfully!");
//                } else {
//                    model.addAttribute("error", "Coupon is invalid or cannot be applied.");
//                }
//            } else {
//                model.addAttribute("error", "Coupon not found.");
//            }
//        }
//
//        List<Address> addresses = addressService.getAllAddressesByUser(user);
//        List<Coupon> activeCoupons = couponService.getAllCoupons();
//
//        model.addAttribute("coupons", activeCoupons);
//        model.addAttribute("user", user);
//        model.addAttribute("cartCount", cart.getCartItem().size());
//        model.addAttribute("paymentAmount", String.format("%.2f", totalAmount));
//        model.addAttribute("addresses", addresses);
//        model.addAttribute("coupons", couponService.getAllCoupons());
//
//        return "checkout";
//    }

    @GetMapping("/checkout")
    public String checkout(Model model, @RequestParam(value = "couponId", required = false) Long couponId) {
        User user = userService.getCurrentUser();
        Cart cart = cartService.getUserCart(user);

        if (cart == null || cart.getCartItem() == null || cart.getCartItem().isEmpty()) {
            return "redirect:/productPage";
        }

        // ✅ Always calculate subtotal (based on cart items)
        double subtotalAmount = cart.getCartItem().stream().mapToDouble(item -> {
            Product product = item.getProduct();
            double basePrice = product.getPrice();
            double finalPrice = basePrice;

            // Apply product offer
            if (product.getProductOffers() != null && !product.getProductOffers().isEmpty()) {
                ProductOffer offer = product.getProductOffers().get(0);
                double discountedPrice = basePrice - (basePrice * offer.getDiscountPercentage() / 100);
                finalPrice = discountedPrice;
            }
            // Apply category offer
            else if (product.getCategoryOffer() != null && product.getCategoryOffer().isActive()) {
                double categoryDiscountedPrice = basePrice - (basePrice * product.getCategoryOffer().getDiscountPercentage() / 100);
                finalPrice = categoryDiscountedPrice;
            }

            return finalPrice * item.getQuantity();
        }).sum();

        double discountAmount = 0;

        // ✅ Apply coupon only if couponId is given
        if (couponId != null) {
            Optional<Coupon> optionalCoupon = couponService.getCouponById(couponId);

            if (optionalCoupon.isPresent()) {
                Coupon coupon = optionalCoupon.get();

                if (coupon.isActive() && subtotalAmount >= coupon.getMinimumPurchaseAmount() && coupon.getUsageCount() > 0) {
                    discountAmount = coupon.getAmount();
                    coupon.setUsageCount(coupon.getUsageCount() - 1);
                    couponService.saveCoupon(coupon);
                    model.addAttribute("success", "Coupon applied successfully!");
                } else {
                    model.addAttribute("error", "Coupon is invalid or cannot be applied.");
                }
            } else {
                model.addAttribute("error", "Coupon not found.");
            }
        }

        // ✅ Total amount = subtotal - discount
        double paymentAmount = subtotalAmount - discountAmount;
        if (paymentAmount < 0) paymentAmount = 0;

        cart.setTotalAmount(paymentAmount);
        cartService.saveCart(cart);

        List<Address> addresses = addressService.getAllAddressesByUser(user);
        List<Coupon> activeCoupons = couponService.getAllCoupons();

        model.addAttribute("user", user);
        model.addAttribute("cartCount", cart.getCartItem().size());
        model.addAttribute("subtotalAmount", String.format("%.2f", subtotalAmount));
        model.addAttribute("discountAmount", String.format("%.2f", discountAmount));
        model.addAttribute("paymentAmount", String.format("%.2f", paymentAmount));
        model.addAttribute("addresses", addresses);
        model.addAttribute("coupons", activeCoupons);

        return "checkout";
    }




    @GetMapping("/applyCoupon")
    public String applyCoupon(@RequestParam(value = "couponId") Long couponId, Model model) {
        User user = userService.getCurrentUser();
        Cart cart = cartService.getUserCart(user);
        List<Address> addresses = addressService.getAllAddressesByUser(user);


        if (cart == null || cart.getCartItem() == null || cart.getCartItem().isEmpty()) {
            model.addAttribute("error", "Your cart is empty.");
            model.addAttribute("coupons", couponService.getAllCoupons());
            return "checkout";
        }

        Optional<Coupon> optionalCoupon = couponService.getCouponById(couponId);

        if (optionalCoupon.isPresent() && optionalCoupon.get().isActive()) {
            Coupon coupon = optionalCoupon.get();
            double totalAmount = cart.getCartItem().stream()
                    .mapToDouble(item -> item.getPrice() * item.getQuantity())
                    .sum();


            if (totalAmount >= coupon.getMinimumPurchaseAmount()) {
                double discountedAmount = totalAmount - coupon.getAmount();
                cart.setAppliedCoupon(coupon);
                cart.setTotalAmount(discountedAmount);
                cartService.saveCart(cart);
                model.addAttribute("success", "Coupon applied successfully!");
                model.addAttribute("paymentAmount", discountedAmount);
                System.out.println("DISSSSSSSSSSSSSSSSSSSSSSCOUNNNNNNNNNNNNNNNNNNNT" + discountedAmount);
            } else {
                model.addAttribute("error", "Coupon cannot be applied. Minimum purchase amount is required.");
            }
        } else {
            model.addAttribute("error", "Invalid or inactive coupon.");
        }

        List<Coupon> activeCoupons = couponService.getAllCoupons();
        model.addAttribute("paymentAmount", cart.getTotalAmount());
        model.addAttribute("coupons", activeCoupons);
        model.addAttribute("addresses", addresses);
        model.addAttribute("cartCount", cart.getCartItem().size());
        return "checkout";
    }

    @GetMapping("/removeCoupon")
    public String removeCoupon(Model model) {
        User user = userService.getCurrentUser();
        Cart cart = cartService.getUserCart(user);
        List<Address> addresses = addressService.getAllAddressesByUser(user);

        if (cart == null || cart.getCartItem() == null || cart.getCartItem().isEmpty()) {
            model.addAttribute("error", "Your cart is empty.");
            model.addAttribute("coupons", couponService.getAllCoupons());
            return "checkout";
        }

        // Reset total amount without coupon
        double totalAmount = cart.getCartItem().stream()
                .mapToDouble(item -> {
                    double price = item.getPrice();

                    // Check if there's an offer
                    if (item.getProduct().getProductOffers() != null && !item.getProduct().getProductOffers().isEmpty()) {
                        ProductOffer offer = item.getProduct().getProductOffers().get(0);
                        if (offer.isEnabled()) {
                            double discount = offer.getDiscountPercentage();
                            price = price - (price * discount / 100);
                        }
                    }

                    return price * item.getQuantity();
                }).sum();

        // Remove coupon from cart
        cart.setAppliedCoupon(null);
        cart.setTotalAmount(totalAmount);
        cartService.saveCart(cart);

        model.addAttribute("success", "Coupon removed successfully.");
        model.addAttribute("paymentAmount", totalAmount);
        model.addAttribute("addresses", addresses);
        model.addAttribute("coupons", couponService.getAllCoupons());
        model.addAttribute("cartCount", cart.getCartItem().size());

        return "redirect:/user/cart/checkout";
    }






    @PostMapping("/payNow")
    public String payNow(@RequestParam(value = "orderId", required = false) Long orderId, Model model) {
        User user = userService.getCurrentUser();
        Cart cart = cartService.getUserCart(user);

        double totalAmount = 0.0;
        double totalDiscount = 0.0;

        for (CartItem cartItem : cart.getCartItem()) {
            Product product = cartItem.getProduct();
            double basePrice = product.getPrice();
            double finalPrice = basePrice;
            double discountAmount = 0.0;


            if (orderId != null) {

                Orders pendingOrder = orderService.findById(orderId);

                if (pendingOrder == null || !"Payment Pending".equals(pendingOrder.getOrderStatus())) {
                    model.addAttribute("errorMessage", "Invalid or non-pending order selected.");
                    return "error";
                }

                totalAmount = pendingOrder.getTotalAmount();
                model.addAttribute("paymentAmount", totalAmount);
                model.addAttribute("totalDiscount", 0.0);
                model.addAttribute("paymentMethod", "razorpay");
                model.addAttribute("user", user);
                model.addAttribute("orderId", orderId);

                return "payment";
            }



            double originalItemTotal = basePrice * cartItem.getQuantity();


            if (product.getProductOffers() != null && !product.getProductOffers().isEmpty()) {
                ProductOffer offer = product.getProductOffers().get(0);
                double discountedPrice = basePrice - (basePrice * offer.getDiscountPercentage() / 100);
                finalPrice = discountedPrice;
            }

            else if (product.getCategoryOffer() != null && product.getCategoryOffer().isActive()
                    && product.getCategoryOffer().isActive()) {
                double categoryDiscountedPrice = basePrice - (basePrice * product.getCategoryOffer().getDiscountPercentage() / 100);
                finalPrice = categoryDiscountedPrice;
            }


            double finalItemTotal = finalPrice * cartItem.getQuantity();


            discountAmount = originalItemTotal - finalItemTotal;




            totalDiscount += discountAmount;
            totalAmount += finalItemTotal;

            //to save order
            Orders order = new Orders();
            order.setItemCount(cartItem.getQuantity());
            order.setProduct(product);
            order.setOrderStatus("Payment Pending");
            order.setPaymentStatus("Pending");
            order.setOrderDate(new Date());
            order.setTotalAmount(finalItemTotal);
            order.setUser(user);
            order.setPaymentMethod(order.getPaymentMethod());
            orderService.saveOrder(order);
        }

        // apply coupon discount
        Coupon couponDiscount = cart.getAppliedCoupon();

        if (couponDiscount != null) {
            double couponDiscountAmount = couponDiscount.getAmount();
            totalDiscount += couponDiscountAmount;
            totalAmount -= couponDiscountAmount;
        }

        boolean codAvailable = totalAmount <= 1000;

        model.addAttribute("codAvailable", codAvailable);
        model.addAttribute("paymentAmount", totalAmount);
        model.addAttribute("totalDiscount", totalDiscount);
        model.addAttribute("paymentMethod", "razorpay");
        model.addAttribute("user", user);

        return "payment";
    }








    @PostMapping("/payment/success")
    public ResponseEntity<String> handlePaymentSuccess(@RequestBody PaymentSuccessRequest request) {
        try {

            Orders order = orderService.findById(request.getOrderId());
            if (order == null) {
                return ResponseEntity.badRequest().body("Invalid order ID");
            }

            // Update order status
            order.setPaymentStatus("Success");
            order.setOrderStatus("Processing");
            order.setRazorpayOrderId(order.getRazorpayOrderId());
            order.setPaymentMethod(order.getPaymentMethod());
            order.getRazorpayPaymentId();
            orderService.saveOrder(order);

            return ResponseEntity.ok("Payment success processed");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing success");
        }
    }



    @PostMapping("/payment/failure")
    public ResponseEntity<String> handlePaymentFailure(@RequestBody PaymentFailureRequest request) {
        try {

            Orders order = orderService.findById(request.getOrderId());
            if (order == null) {
                return ResponseEntity.badRequest().body("Invalid order ID");
            }

            order.setPaymentStatus("failure");
            order.setOrderStatus("Payment Pending");
            order.setRazorpayOrderId(order.getRazorpayOrderId());
            order.setPaymentMethod(order.getPaymentMethod());
            order.getRazorpayPaymentId();
            orderService.saveOrder(order);

            return ResponseEntity.ok("Payment failure processed");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing failure");
        }
    }


    @PostMapping("/create-razorpay-order")
    @ResponseBody
    public Map<String, Object> createRazorpayOrder(@RequestBody Map<String, Object> payload) {
        try {
            double amount = Double.parseDouble(payload.get("amount").toString());
            Long orderId = payload.get("orderId") != null ? Long.parseLong(payload.get("orderId").toString()) : null;

            RazorpayClient razorpay = new RazorpayClient("rzp_test_GCgY3o3OlXx0HN", "q4GkXW0SRLZ7MBzK1sRO2Pcc");

            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", (int) (amount * 100)); // amount in paise
            orderRequest.put("currency", "INR");
            orderRequest.put("receipt", "order_rcptid_" + System.currentTimeMillis());

            com.razorpay.Order order = razorpay.orders.create(orderRequest);

            Map<String, Object> response = new HashMap<>();
            response.put("orderId", order.get("id"));
            response.put("amount", amount);
            response.put("applicationOrderId", orderId);
            return response;
        } catch (Exception e) {
            throw new RuntimeException("Razorpay order creation failed: " + e.getMessage());
        }
    }








}



























