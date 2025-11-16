package com.restaurant.models;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Employee {
    private String name;
    private String role;
    private boolean isOnDuty;
    private List<Order> assignedOrders;
    private int maxOrders;
    private int completedOrdersToday;

    public static class Config {
        public static final int WAITER_MAX_ORDERS = 6;
        public static final int CHEF_MAX_ORDERS = 8;
        public static final int MANAGER_MAX_ORDERS = 10;
    }

    public Employee(String name, String role) {
        this.name = name;
        this.role = role;
        this.isOnDuty = true;
        this.assignedOrders = new CopyOnWriteArrayList<>();
        this.completedOrdersToday = 0;

        if (role.equalsIgnoreCase("waiter")) {
            this.maxOrders = Config.WAITER_MAX_ORDERS;
        } else if (role.equalsIgnoreCase("chef")) {
            this.maxOrders = Config.CHEF_MAX_ORDERS;
        } else {
            this.maxOrders = Config.MANAGER_MAX_ORDERS;
        }
    }

    public boolean canTakeMoreOrders() {
        return isOnDuty && assignedOrders.size() < maxOrders;
    }

    public synchronized boolean assignOrder(Order order) {
        if (!canTakeMoreOrders()) {
            return false;
        }

        assignedOrders.add(order);

        if (role.equalsIgnoreCase("waiter")) {
            order.setAssignedWaiter(name);
        } else if (role.equalsIgnoreCase("chef")) {
            order.setAssignedChef(name);
        }

        return true;
    }

    public synchronized void completeOrder(Order order) {
        if (assignedOrders.remove(order)) {
            completedOrdersToday++;
        }
    }

    public String getWorkloadStatus() {
        if (!isOnDuty) return "Off Duty";

        int workloadPercent = (assignedOrders.size() * 100) / maxOrders;
        if (workloadPercent < 40) return "Light (" + assignedOrders.size() + "/" + maxOrders + ")";
        if (workloadPercent < 75) return "Moderate (" + assignedOrders.size() + "/" + maxOrders + ")";
        return "Heavy (" + assignedOrders.size() + "/" + maxOrders + ")";
    }

    public double getEfficiency() {
        return completedOrdersToday > 0 ?
                (completedOrdersToday * 100.0) / (assignedOrders.size() + completedOrdersToday) : 0;
    }

    public synchronized List<Order> getAssignedOrders() {
        return new ArrayList<>(assignedOrders);
    }

    public String getName() { return name; }
    public String getRole() { return role; }
    public boolean isOnDuty() { return isOnDuty; }
    public int getCurrentWorkload() { return assignedOrders.size(); }
    public int getCompletedOrdersToday() { return completedOrdersToday; }
    public int getMaxOrders() { return maxOrders; }

    public void startShift() {
        this.isOnDuty = true;
    }

    public void endShift() {
        this.isOnDuty = false;
    }
}