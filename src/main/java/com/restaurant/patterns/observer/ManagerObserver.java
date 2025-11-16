package com.restaurant.patterns.observer;

import com.restaurant.models.Order;
import com.restaurant.models.OrderStatus;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ManagerObserver implements Observer {
    private static ManagerObserver instance;
    private Map<OrderStatus, Integer> statusCounts;
    private double totalRevenue;
    private LocalDateTime lastNotificationTime;
    private ManagerObserver() {
        this.statusCounts = new ConcurrentHashMap<>();
        this.totalRevenue = 0.0;

        for (OrderStatus status : OrderStatus.values()) {
            statusCounts.put(status, 0);
        }

        System.out.println(" ManagerObserver singleton initialized");
    }

    public static ManagerObserver getInstance() {
        if (instance == null) {
            instance = new ManagerObserver();
        }
        return instance;
    }

    @Override
    public void update(Order order) {
        lastNotificationTime = LocalDateTime.now();

        OrderStatus newStatus = order.getStatus();
        statusCounts.merge(newStatus, 1, Integer::sum);

        if (newStatus == OrderStatus.PAID) {
            totalRevenue += order.getTotalPrice();
            System.out.println("MANAGER Revenue +$" +
                    String.format("%.2f", order.getTotalPrice()) +
                    " (Total: $" + String.format("%.2f", totalRevenue) + ")");
        }

        System.out.println("MANAGER Order #" + order.getOrderId() +
                " → " + newStatus + " | Total: $" +
                String.format("%.2f", order.getTotalPrice()));
    }

    public Map<String, Object> getAnalytics() {
        Map<String, Object> analytics = new HashMap<>();
        analytics.put("totalRevenue", totalRevenue);
        analytics.put("statusDistribution", new HashMap<>(statusCounts));
        analytics.put("lastNotification", lastNotificationTime);
        return analytics;
    }
    public void printAnalyticsReport() {
        System.out.println("MANAGER ANALYTICS REPORT");
        System.out.println("Total Revenue: $" + String.format("%.2f", totalRevenue));
        System.out.println("Status Distribution:");
        for (Map.Entry<OrderStatus, Integer> entry : statusCounts.entrySet()) {
            System.out.println("  " + entry.getKey() + ": " + entry.getValue());
        }
        System.out.println("Last Notification: " +
                (lastNotificationTime != null ? lastNotificationTime : "Never"));
    }
}