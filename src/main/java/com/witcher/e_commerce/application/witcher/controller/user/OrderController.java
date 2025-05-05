package com.witcher.e_commerce.application.witcher.controller.user;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.witcher.e_commerce.application.witcher.dao.CartRepository;
import com.witcher.e_commerce.application.witcher.dao.UserRepository;
import com.witcher.e_commerce.application.witcher.entity.*;
import com.witcher.e_commerce.application.witcher.service.UserService;
import com.witcher.e_commerce.application.witcher.service.address.AddressService;
import com.witcher.e_commerce.application.witcher.service.cart.CartService;
import com.witcher.e_commerce.application.witcher.service.coupon.CouponService;
import com.witcher.e_commerce.application.witcher.service.order.OrderService;
import com.witcher.e_commerce.application.witcher.service.product.ProductService;
import com.witcher.e_commerce.application.witcher.service.transaction.TransactionService;
import com.witcher.e_commerce.application.witcher.service.wallet.WalletService;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@Slf4j
public class OrderController {

    private final OrderService orderService;

    private final RazorpayClient razorpayClient;

    public OrderController(OrderService orderService, RazorpayClient razorpayClient) {
        this.orderService = orderService;
        this.razorpayClient = razorpayClient;
    }


    private Order createRazorpayOrder(Double totalAmount) throws RazorpayException {
        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", totalAmount * 100);
        orderRequest.put("currency", "INR");
        orderRequest.put("payment_capture", 1);

        return razorpayClient.orders.create(orderRequest);
    }


    private String generateOrderNumber() {
        return "ORD-" + System.currentTimeMillis();
    }


    @GetMapping("/order-confirmation")
    public String showOrderConfirmation(@RequestParam(value = "orderNumbers", required = false) String orderNumbersStr, Model model) {
        if (orderNumbersStr != null && !orderNumbersStr.isEmpty()) {
            String[] orderNumbers = orderNumbersStr.split(",");
            List<Orders> orders = new ArrayList<>();

            for (String orderNumber : orderNumbers) {
                Orders order = orderService.findOrderByOrderNumber(orderNumber);
                if (order != null) {
                    orders.add(order);
                }
            }

            model.addAttribute("orders", orders);
        }

        return "order-confirmation";
    }





}










