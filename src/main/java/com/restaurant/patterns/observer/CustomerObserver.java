package com.restaurant.patterns.observer;

import com.restaurant.models.Order;
import com.restaurant.models.OrderStatus;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class CustomerObserver implements Observer {
    private static CustomerObserver instance;
    private Map<String, List<String>> customerNotifications;
    private Set<String> vipCustomers;
    private CustomerObserver() {
        this.customerNotifications = new ConcurrentHashMap<>();
        this.vipCustomers = ConcurrentHashMap.newKeySet();
        System.out.println("CustomerObserver singleton initialized");
    }
    public static CustomerObserver getInstance() {
        if (instance == null) {
            instance = new CustomerObserver();
        }
        return instance;
    }

    @Override
    public void update(Order order) {
        String customerPhone = order.getCustomer().getPhone();
        String customerName = order.getCustomer().getName();
        customerNotifications.putIfAbsent(customerPhone, new ArrayList<>());
        if (order.getCustomer().isVIP() && !vipCustomers.contains(customerPhone)) {
            vipCustomers.add(customerPhone);
            System.out.println("New VIP customer: " + customerName);
        }

        String message = "Order #" + order.getOrderId() + " status: " + order.getStatus();
        if (order.getStatus() == OrderStatus.READY) {
            message += " - Your order is ready for pickup!";
        }
        List<String> notifications = customerNotifications.get(customerPhone);
        notifications.add(0, message);
        if (notifications.size() > 3) {
            notifications.remove(notifications.size() - 1);
        }
        if (vipCustomers.contains(customerPhone)) {
            System.out.println("VIP SMS to:" + customerPhone  + message);
        } else {
            System.out.println("SMS to" + customerPhone + ":" + message);
        }
    }
    public Set<String> getVIPCustomers() {
        return new HashSet<>(vipCustomers);
    }

}