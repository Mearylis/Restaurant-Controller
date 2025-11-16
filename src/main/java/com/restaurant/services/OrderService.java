package com.restaurant.services;

import com.restaurant.models.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class OrderService {
    private List<Order> activeOrders;
    private List<Order> archivedOrders; // ✅ АРХИВ СТАРЫХ ЗАКАЗОВ
    private static final int MAX_ACTIVE_ORDERS = 1000; // ✅ ЛИМИТ ДЛЯ ПАМЯТИ

    public OrderService() {
        this.activeOrders = new ArrayList<>();
        this.archivedOrders = new ArrayList<>();
    }

    public void addOrder(Order order) {
        // ✅ АВТОМАТИЧЕСКАЯ АРХИВАЦИЯ ПРИ ПРЕВЫШЕНИИ ЛИМИТА
        if (activeOrders.size() >= MAX_ACTIVE_ORDERS) {
            archiveOldOrders();
        }
        activeOrders.add(order);
    }

    public Order getOrderById(int orderId) {
        // Ищем в активных заказах
        Order order = activeOrders.stream()
                .filter(o -> o.getOrderId() == orderId)
                .findFirst()
                .orElse(null);

        // Если не нашли, ищем в архиве
        if (order == null) {
            order = archivedOrders.stream()
                    .filter(o -> o.getOrderId() == orderId)
                    .findFirst()
                    .orElse(null);
        }

        return order;
    }

    // ✅ МЕТОД АРХИВАЦИИ СТАРЫХ ЗАКАЗОВ
    public void archiveOldOrders() {
        LocalDateTime cutoffDate = LocalDateTime.now().minusMonths(1); // Архивируем заказы старше 1 месяца

        List<Order> toArchive = activeOrders.stream()
                .filter(o -> o.getCompletedAt() != null && o.getCompletedAt().isBefore(cutoffDate))
                .collect(Collectors.toList());

        activeOrders.removeAll(toArchive);
        archivedOrders.addAll(toArchive);

        System.out.println("📦 Archived " + toArchive.size() + " old orders");
    }

    // ✅ РУЧНАЯ АРХИВАЦИЯ
    public void archiveOrdersOlderThan(LocalDateTime cutoffDate) {
        List<Order> toArchive = activeOrders.stream()
                .filter(o -> o.getCompletedAt() != null && o.getCompletedAt().isBefore(cutoffDate))
                .collect(Collectors.toList());

        activeOrders.removeAll(toArchive);
        archivedOrders.addAll(toArchive);

        System.out.println("📦 Manually archived " + toArchive.size() + " orders older than " + cutoffDate);
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
        return getAllOrders().stream()
                .filter(o -> o.getStatus() == OrderStatus.PAID)
                .mapToDouble(Order::getTotalPrice)
                .sum();
    }

    public int getActiveOrderCount() {
        return (int) activeOrders.stream()
                .filter(o -> o.getStatus() != OrderStatus.PAID)
                .count();
    }


    public Map<String, Object> getOrderStatistics() {
        Map<String, Object> stats = new HashMap<>();

        List<Order> allOrders = getAllOrders();
        stats.put("totalOrders", allOrders.size());
        stats.put("activeOrders", getActiveOrderCount());
        stats.put("archivedOrders", archivedOrders.size());
        stats.put("totalRevenue", getTotalRevenue());

        Map<OrderStatus, Long> statusCount = allOrders.stream()
                .collect(Collectors.groupingBy(Order::getStatus, Collectors.counting()));
        stats.put("statusDistribution", statusCount);

        return stats;
    }
}