package com.witcher.e_commerce.application.witcher.service.dashboard;

import com.witcher.e_commerce.application.witcher.dao.OrderRepository;
import com.witcher.e_commerce.application.witcher.dao.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DashboardServiceImpl implements DashboardService{

    private final UserRepository userRepository;

    private final OrderRepository orderRepository;

    public DashboardServiceImpl(UserRepository userRepository, OrderRepository orderRepository) {
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
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
    public Object getTodayRevenue() {
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
            int monthIndex = ((Integer) result[0]) - 1; // Adjust to zero-based index
            Double revenue = (Double) result[1];
            monthlyRevenue.set(monthIndex, revenue);
        }

        return monthlyRevenue;
    }
}
