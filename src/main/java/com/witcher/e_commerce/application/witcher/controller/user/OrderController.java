package com.witcher.e_commerce.application.witcher.controller.user;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.witcher.e_commerce.application.witcher.dao.CartRepository;
import com.witcher.e_commerce.application.witcher.dao.UserRepository;
import com.witcher.e_commerce.application.witcher.entity.*;
import com.witcher.e_commerce.application.witcher.service.PaymentMethod;
import com.witcher.e_commerce.application.witcher.service.UserService;
import com.witcher.e_commerce.application.witcher.service.cart.CartService;
import com.witcher.e_commerce.application.witcher.service.coupon.CouponService;
import com.witcher.e_commerce.application.witcher.service.order.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Date;

@Controller
@Slf4j
public class OrderController {

    private final OrderService orderService;

    private final UserService userService;

    private final CartService cartService;

    private final CartRepository cartRepository;

    private final CouponService couponService;

    private final RazorpayClient razorpayClient;

    public OrderController(OrderService orderService, UserService userService, UserRepository userRepository, CartService cartService, CartRepository cartRepository, CouponService couponService, RazorpayClient razorpayClient) {
        this.orderService = orderService;
        this.userService = userService;
        this.cartService = cartService;
        this.cartRepository = cartRepository;
        this.couponService = couponService;
        this.razorpayClient = razorpayClient;
    }

    @PostMapping("/placeOrder")
    public String placeOrder(@RequestParam("productId") Long productId,
                             @RequestParam("itemCount") Integer itemCount,
                             @RequestParam("totalAmount") Double totalAmount,
                             @RequestParam(value = "paymentMethod") PaymentMethod paymentMethod,
                             @RequestParam(value = "couponId", required = false) Long couponId,
                             Model model, Principal principal) throws RazorpayException{


        User user= userService.findByUsername(principal.getName());

        Product product= new Product();
        product.setId(productId);

        Orders order= new Orders();
        order.setOrderNumber(generateOrderNumber());
        order.setProduct(product);
        order.setItemCount(itemCount);
        order.setOrderStatus("processing");
        order.setTotalAmount(totalAmount);
        order.setOrderDate(new Date());
        order.setUser(user);

        //create razorpay order
        try{
            Order razorpayOrder = createRazorpayOrder(totalAmount);

            model.addAttribute("razorpayOrderId", razorpayOrder.get("id"));
            model.addAttribute("totalAmount", totalAmount);

        } catch (RazorpayException e){
            log.error("Error Creating Razorpay order", e);
            model.addAttribute("errorMessage", "Unable to process payment. Please try again!");
            return "order-confirmation";
        }

        Orders savedOrder = orderService.saveOrder(order);

        // Create the PurchasedOrders object
        PurchasedOrders purchasedOrder = new PurchasedOrders();
        purchasedOrder.setUser(user);
        purchasedOrder.getOrderDate(LocalDateTime.now());
        purchasedOrder.setOrderAmount(totalAmount);
        purchasedOrder.setPaymentMethod(paymentMethod);
        purchasedOrder.setOrderItems(Collections.singletonList(savedOrder));
        purchasedOrder.setPaymentStatus("Pending");

        if (couponId !=null){
            Coupon coupon = couponService.findCouponById(couponId);
            if (coupon != null && coupon.isActive()){
                purchasedOrder.setCoupon(coupon);
                coupon.setUsageCount(coupon.getUsageCount() +1);
                couponService.saveCoupon(coupon);
            }
        }

        Transaction transaction = new Transaction();
        transaction.setAmount(totalAmount);
        transaction.setTransactionType("purchase");
        transaction.setDescription("Order transaction for " + purchasedOrder.getOrderId());

        orderService.savePurchasedOrder(purchasedOrder);
        orderService.saveTransaction(transaction);

        log.info(savedOrder.toString());

        model.addAttribute("orderNumber", savedOrder.getOrderNumber());

        return "order-confirmation";


    }

    private Order createRazorpayOrder(Double totalAmount) throws RazorpayException {
        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", totalAmount * 100); // Amount in paise
        orderRequest.put("currency", "INR"); // Currency code
        orderRequest.put("payment_capture", 1); // Auto capture

        return razorpayClient.orders.create(orderRequest);
    }


    private String generateOrderNumber() {
        // You can implement a proper order number generation logic
        return "ORD-" + System.currentTimeMillis();
    }

    @GetMapping("/trackOrder")
    public String trackOrder(@RequestParam("orderNumber") String orderNumber, Model model) {
        Orders order = orderService.findOrderByOrderNumber(orderNumber);
        model.addAttribute("order", order);
        return "order-tracking";
    }

    @GetMapping("/order-confirmation")
    public String confirmOrder(){
        return "order-confirmation";
    }







}
