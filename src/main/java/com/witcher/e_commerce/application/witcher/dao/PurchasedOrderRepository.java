package com.witcher.e_commerce.application.witcher.dao;

import com.witcher.e_commerce.application.witcher.entity.Orders;
import com.witcher.e_commerce.application.witcher.entity.PurchasedOrders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PurchasedOrderRepository extends JpaRepository<PurchasedOrders, Long> {

    Optional<PurchasedOrders> findByOrderItemsContaining(Orders order);

    @Query("SELECT po FROM PurchasedOrders po JOIN po.orderItems o WHERE o.orderNumber = :orderNumber")
    PurchasedOrders findByOrderItemOrderNumber(@Param("orderNumber") String orderNumber);

}
