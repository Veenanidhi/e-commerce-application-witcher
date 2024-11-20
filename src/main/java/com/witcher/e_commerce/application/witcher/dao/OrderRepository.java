package com.witcher.e_commerce.application.witcher.dao;

import com.witcher.e_commerce.application.witcher.entity.Orders;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Orders, Long> {

    Orders findTopByOrderByOrderItemIdDesc(); // Fetch the last order

    // Find all orders for a specific user with pagination
    Page<Orders> findByUser_Username(String username, Pageable pageable);

    // Count the total number of orders for a specific user
    long countByUser_Username(String username);

    Optional<Orders> findById(Long id);

    //for admin dash
    @Query("SELECT SUM(o.ItemCount) FROM Orders o WHERE DATE(o.orderDate) = DATE(CURRENT_DATE)")
    Integer findTodaySalesCount();

    @Query("SELECT SUM(o.totalAmount) FROM Orders o WHERE DATE(o.orderDate) = DATE(CURRENT_DATE)")
    Double findTodayRevenue();

    @Query("SELECT SUM(o.totalAmount) FROM Orders o")
    Double findTotalRevenue();

    @Query("SELECT o.product.name, SUM(o.ItemCount) as totalSold FROM Orders o GROUP BY o.product.name ORDER BY totalSold DESC")
    List<Object[]> findMostSoldItems();

    @Query("SELECT MONTH(o.orderDate) AS month, SUM(o.totalAmount) AS monthlyRevenue FROM Orders o GROUP BY MONTH(o.orderDate) ORDER BY MONTH(o.orderDate)")
    List<Object[]> findMonthlyRevenue();


    List<Orders> findAllByOrderByOrderDateDesc();
}
