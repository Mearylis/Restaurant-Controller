package com.restaurant.patterns.observer;

import com.restaurant.models.Order;
import com.restaurant.models.OrderStatus;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class WaiterObserver implements Observer {
    private static WaiterObserver instance;
    private Map<String, List<String>> waiterNotifications;
    private WaiterObserver() {
        this.waiterNotifications = new ConcurrentHashMap<>();
        System.out.println("WaiterObserver singleton initialized");
    }

    public static WaiterObserver getInstance() {
        if (instance == null) {
            instance = new WaiterObserver();
        }
        return instance;
    }

    @Override
    public void update(Order order) {
        String waiterName = order.getAssignedWaiter();
        if (waiterName == null || "Unknown".equals(waiterName)) {
            return;
        }

        waiterNotifications.putIfAbsent(waiterName, new ArrayList<>());

        String message = "Order #" + order.getOrderId() + " is " + order.getStatus();

        List<String> notifications = waiterNotifications.get(waiterName);
        notifications.add(0, message);
        if (notifications.size() > 5) {
            notifications.remove(notifications.size() - 1);
        }

        if (order.getStatus() == OrderStatus.READY) {
            System.out.println("WAITER " + waiterName + " URGENT: Order #" +
                    order.getOrderId() + " READY for serving!");
        } else {
            System.out.println("WAITER " + waiterName + message);
        }
    }
    public Map<String, Integer> getAllWaiterStats() {
        Map<String, Integer> stats = new HashMap<>();
        for (Map.Entry<String, List<String>> entry : waiterNotifications.entrySet()) {
            stats.put(entry.getKey(), entry.getValue().size());
        }
        return stats;
    }
}