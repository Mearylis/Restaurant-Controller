package com.restaurant.services;

import com.restaurant.models.*;
import java.time.LocalDateTime;
import java.util.*;

public class OrderService {
    private List<Order> activeOrders;
    private List<Order> archivedOrders;

    public OrderService() {
        this.activeOrders = new ArrayList<>();
        this.archivedOrders = new ArrayList<>();
    }

    public void addOrder(Order order) {
        if (activeOrders.size() >= 1000) {
            archiveOldOrders();
        }
        activeOrders.add(order);
    }

    public Order getOrderById(int orderId) {
        for (Order order : activeOrders) {
            if (order.getOrderId() == orderId) {
                return order;
            }
        }

        for (Order order : archivedOrders) {
            if (order.getOrderId() == orderId) {
                return order;
            }
        }

        return null;
    }

    public void archiveOldOrders() {
        LocalDateTime cutoffDate = LocalDateTime.now().minusMonths(1);
        List<Order> toArchive = new ArrayList<>();

        for (Order order : activeOrders) {
            if (order.getCompletedAt() != null && order.getCompletedAt().isBefore(cutoffDate)) {
                toArchive.add(order);
            }
        }

        activeOrders.removeAll(toArchive);
        archivedOrders.addAll(toArchive);
    }

    public void archiveOrdersOlderThan(LocalDateTime cutoffDate) {
        List<Order> toArchive = new ArrayList<>();

        for (Order order : activeOrders) {
            if (order.getCompletedAt() != null && order.getCompletedAt().isBefore(cutoffDate)) {
                toArchive.add(order);
            }
        }

        activeOrders.removeAll(toArchive);
        archivedOrders.addAll(toArchive);
    }

    public List<Order> getAllOrders() {
        List<Order> allOrders = new ArrayList<>(activeOrders);
        allOrders.addAll(archivedOrders);
        return allOrders;
    }

    public List<Order> getActiveOrders() {
        return new ArrayList<>(activeOrders);
    }

    public List<Order> getArchivedOrders() {
        return new ArrayList<>(archivedOrders);
    }

    public int getOrderCount() {
        return activeOrders.size() + archivedOrders.size();
    }

    public double getTotalRevenue() {
        double total = 0;
        for (Order order : getAllOrders()) {
            if (order.getStatus() == OrderStatus.PAID) {
                total += order.getTotalPrice();
            }
        }
        return total;
    }

    public int getActiveOrderCount() {
        int count = 0;
        for (Order order : activeOrders) {
            if (order.getStatus() != OrderStatus.PAID) {
                count++;
            }
        }
        return count;
    }

    public Map<String, Object> getOrderStatistics() {
        Map<String, Object> stats = new HashMap<>();

        List<Order> allOrders = getAllOrders();
        stats.put("totalOrders", allOrders.size());
        stats.put("activeOrders", getActiveOrderCount());
        stats.put("archivedOrders", archivedOrders.size());
        stats.put("totalRevenue", getTotalRevenue());

        Map<OrderStatus, Integer> statusCount = new HashMap<>();
        for (Order order : allOrders) {
            OrderStatus status = order.getStatus();
            statusCount.put(status, statusCount.getOrDefault(status, 0) + 1);
        }
        stats.put("statusDistribution", statusCount);

        return stats;
    }
}