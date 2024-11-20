package com.witcher.e_commerce.application.witcher.service.dashboard;

public interface DashboardService {

    Object getTotalUsers();

    Object getTodaySalesCount();

    Object getTodayRevenue();

    Object getTotalRevenue();

    Object getMostSoldItems();

    Object getMonthlyRevenue();
}
