package com.witcher.e_commerce.application.witcher.service.order;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.witcher.e_commerce.application.witcher.dao.CategoryOfferRepository;
import com.witcher.e_commerce.application.witcher.dao.OrderRepository;
import com.witcher.e_commerce.application.witcher.dao.PurchasedOrderRepository;
import com.witcher.e_commerce.application.witcher.dao.TransactionRepository;
import com.witcher.e_commerce.application.witcher.entity.*;
import com.witcher.e_commerce.application.witcher.service.WalletService;
import jakarta.persistence.EntityNotFoundException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService{

    private static final int PAGE_SIZE = 500;

    @Autowired
    private WalletService walletService;

    private final OrderRepository orderRepository;

    private final RazorpayClient razorpayClient;

    private final CategoryOfferRepository categoryOfferRepository;

    private  final TransactionRepository transactionRepository;

    private final PurchasedOrderRepository purchasedOrderRepository;

    public OrderServiceImpl(OrderRepository orderRepository, RazorpayClient razorpayClient, CategoryOfferRepository categoryOfferRepository, TransactionRepository transactionRepository, PurchasedOrderRepository purchasedOrderRepository) {
        this.orderRepository = orderRepository;
        this.razorpayClient = razorpayClient;
        this.categoryOfferRepository = categoryOfferRepository;
        this.transactionRepository = transactionRepository;
        this.purchasedOrderRepository = purchasedOrderRepository;
    }


    @Override
    public PurchasedOrders savePurchaseOrder(PurchasedOrders purchaseOrder) {
        return purchasedOrderRepository.save(purchaseOrder);
    }

    @Override
    public Orders saveOrder(Orders order) {
        return orderRepository.save(order);
    }

    @Override
    public Orders findOrderByOrderNumber(String orderNumber) {
        return orderRepository.findTopByOrderByOrderItemIdDesc(); // Retrieve the most recent order
    }

    @Override
    public Orders getLastOrder() {
        return orderRepository.findTopByOrderByOrderItemIdDesc(); // Retrieve the most recent order

    }

    @Override
    public List<Orders> getAllOrders() {
        // You can apply business logic here if needed
        return orderRepository.findAllByOrderByOrderDateDesc();
    }

    @Override
    public void updateOrderStatus(Long orderId, String newStatus) {
        Orders order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));
        order.setOrderStatus(newStatus);  // Update the order status
        orderRepository.save(order);  // Save the updated order
    }

    @Override
    public List<Orders> getOrdersByUser(String username, int page) {
        Pageable pageable = PageRequest.of(page - 1, PAGE_SIZE, Sort.by("orderDate").descending());
        Page<Orders> ordersPage = orderRepository.findByUser_Username(username, pageable);
        return ordersPage.getContent();
    }

    @Override
    public int getTotalPagesByUser(String username) {
        int totalOrders = (int) orderRepository.countByUser_Username(username);
        return (int) Math.ceil((double) totalOrders / PAGE_SIZE);
    }

    @Override
    public void cancelOrder(Long id) {
        Orders order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid order ID"));

        // Update the order status to 'Cancelled'
        order.setOrderStatus("Cancelled");
        orderRepository.save(order); // Save the updated order status
    }

    @Override
    public Optional<Orders> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    @Override
    public String generateOrderNumber() {
        return "ORD-" + UUID.randomUUID().toString();
    }

    @Override
    public Orders findById(Long orderItemId) {
        Optional<Orders> optionalOrder = orderRepository.findById(orderItemId); // Use orderItemId here
        return optionalOrder.orElse(null); // Return null if not found
    }

    @Override
    public void returnOrder(Long id) {

        Optional<Orders> ordersOptional= orderRepository.findById(id);
        if (!ordersOptional.isPresent()){
            throw new IllegalArgumentException("Order not found for id" +id);
        }

        Orders order = ordersOptional.get();

        // Ensure that the order is delivered before processing a return
        if (!order.getOrderStatus().equals("Delivered")) {
            throw new IllegalStateException("Cannot return an order that is not delivered.");
        }

        // Update the order status to 'Returned'
        order.setOrderStatus("Returned");

        // Optionally, implement business logic such as restocking items, issuing refunds, etc.
        // For example, restocking the product:
        order.getProduct().setStock(order.getProduct().getStock() + order.getItemCount());

        User user = order.getUser();
        Wallet wallet = walletService.getWalletByUserId(user.getId());

        if (wallet == null){

            wallet = new Wallet();
            wallet.setUser(user);
            wallet.setBalance(0.0);
        }

        wallet.setBalance(wallet.getBalance() + order.getTotalAmount());
        walletService.save(wallet);

        // Save the updated order back to the database
        orderRepository.save(order);
    }

    @Override
    public void savePurchasedOrder(PurchasedOrders purchasedOrder) {
        purchasedOrderRepository.save(purchasedOrder);
    }

    @Override
    public void saveTransaction(Transaction transaction) {
        transactionRepository.save(transaction);
    }

    @Override
    @Transactional
    public void deleteCategoryOfferById(Long id) {
        // Check if the offer exists before attempting to delete
        if (categoryOfferRepository.existsById(id)) {
            categoryOfferRepository.deleteById(id);
        } else {
            throw new EntityNotFoundException("Category offer not found with id: " + id);
        }
    }



    public Order createOrder(double amount, String currency) {
        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", amount * 100); // amount in paise
        orderRequest.put("currency", currency);
        orderRequest.put("payment_capture", 1); // Auto capture

        try {
            return razorpayClient.orders.create(orderRequest);
        } catch (RazorpayException e) {
            e.printStackTrace();
            return null;
        }
    }




}
