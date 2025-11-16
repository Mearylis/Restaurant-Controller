package com.restaurant.patterns.observer;

import com.restaurant.models.Order;
import com.restaurant.models.OrderStatus;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class KitchenObserver implements Observer {
    private static KitchenObserver instance;
    private int notificationCount;
    private List<String> recentNotifications;
    private int ordersInProgress;

    private KitchenObserver() {
        this.notificationCount = 0;
        this.recentNotifications = new CopyOnWriteArrayList<>();
        this.ordersInProgress = 0;
        System.out.println("KitchenObserver singleton initialized");
    }

    public static KitchenObserver getInstance() {
        if (instance == null) {
            instance = new KitchenObserver();
        }
        return instance;
    }

    @Override
    public void update(Order order) {
        notificationCount++;
        String message = "Order #" + order.getOrderId() + " status: " + order.getStatus();

        recentNotifications.add(0, message);
        if (recentNotifications.size() > 10) {
            recentNotifications.remove(recentNotifications.size() - 1);
        }

        if (order.getStatus() == OrderStatus.PREPARING) {
            ordersInProgress++;
        } else if (order.getStatus() == OrderStatus.READY || order.getStatus() == OrderStatus.PAID) {
            ordersInProgress = Math.max(0, ordersInProgress - 1);
        }

        System.out.println("KITCHEN " + message + " | In progress: " + ordersInProgress);
    }

    public int getNotificationCount() { return notificationCount; }
    public List<String> getRecentNotifications() { return new ArrayList<>(recentNotifications); }
    public int getOrdersInProgress() { return ordersInProgress; }
}