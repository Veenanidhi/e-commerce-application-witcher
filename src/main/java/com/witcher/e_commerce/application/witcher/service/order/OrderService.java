package com.witcher.e_commerce.application.witcher.service.order;

import com.witcher.e_commerce.application.witcher.entity.Orders;
import com.witcher.e_commerce.application.witcher.entity.PurchasedOrders;
import com.witcher.e_commerce.application.witcher.entity.Transaction;

import java.util.List;
import java.util.Optional;

public interface OrderService {


    PurchasedOrders savePurchaseOrder(PurchasedOrders purchaseOrder);

    Orders saveOrder(Orders order);

    Orders findOrderByOrderNumber(String orderNumber);

    Orders getLastOrder();

    List<Orders> getAllOrders();


    void updateOrderStatus(Long orderId, String newStatus);

    List<Orders> getOrdersByUser(String username, int page);

    int getTotalPagesByUser(String username);

     

    void cancelOrder(Long id);


    Optional<Orders> getOrderById(Long id);

    String generateOrderNumber();

    Orders findById(Long orderItemId);

    void returnOrder(Long id);

    void savePurchasedOrder(PurchasedOrders purchasedOrder);

    void saveTransaction(Transaction transaction);

    void deleteCategoryOfferById(Long id);
}
