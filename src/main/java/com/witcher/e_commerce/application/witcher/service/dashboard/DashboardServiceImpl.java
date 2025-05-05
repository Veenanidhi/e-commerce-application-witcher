package com.witcher.e_commerce.application.witcher.service.dashboard;

import com.witcher.e_commerce.application.witcher.dao.CategoryRepository;
import com.witcher.e_commerce.application.witcher.dao.OrderRepository;
import com.witcher.e_commerce.application.witcher.dao.UserRepository;
import com.witcher.e_commerce.application.witcher.entity.Orders;
import com.witcher.e_commerce.application.witcher.entity.Product;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

@Service
public class DashboardServiceImpl implements DashboardService{

    private final UserRepository userRepository;

    private final OrderRepository orderRepository;

    private final CategoryRepository categoryRepository;

    public DashboardServiceImpl(UserRepository userRepository, OrderRepository orderRepository, CategoryRepository categoryRepository) {
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.categoryRepository = categoryRepository;
    }


    @Override
    public Object getTotalUsers() {
        return userRepository.countTotalUsers();
    }

    @Override
    public Object getTodaySalesCount() {
        return orderRepository.findTodaySalesCount() != null ? orderRepository.findTodaySalesCount() : 0;
    }

    @Override
    public Double getTodayRevenue() {
        return orderRepository.findTodayRevenue() != null ? orderRepository.findTodayRevenue() : 0.0;
    }

    @Override
    public Object getTotalRevenue() {
        return orderRepository.findTotalRevenue() != null ? orderRepository.findTotalRevenue() : 0.0;
    }

    @Override
    public Object getMostSoldItems() {
        return orderRepository.findMostSoldItems();
    }

    @Override
    public Object getMonthlyRevenue() {
        List<Object[]> results = orderRepository.findMonthlyRevenue();
        List<Double> monthlyRevenue = new ArrayList<>(12);

        // Initialize the list with zeroes for each month
        for (int i = 0; i < 12; i++) {
            monthlyRevenue.add(0.0);
        }

        // Map the revenue data to the correct month index (0 = Jan, 11 = Dec)
        for (Object[] result : results) {
            if (result[0] != null && result[1] != null) { // Null check!
                int monthIndex = ((Number) result[0]).intValue() - 1;  // Safer cast using Number
                Double revenue = ((Number) result[1]).doubleValue();  // Safe convert
                if (monthIndex >= 0 && monthIndex < 12) { // Extra safety
                    monthlyRevenue.set(monthIndex, revenue);
                }
            }
        }

        return monthlyRevenue;
    }


    public List<Object[]> getTopSellingCategories() {
        return categoryRepository.findTopSellingCategories(PageRequest.of(0, 10));
    }






}
