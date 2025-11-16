package com.restaurant.models;

import java.util.ArrayList;
import java.util.List;

public class Customer {
    private String name;
    private String phone;
    private String email;
    private int loyaltyPoints;
    private List<Order> orderHistory;
    private String preferences;

    public Customer(String name, String phone, String email) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.loyaltyPoints = 0;
        this.orderHistory = new ArrayList<>();
        this.preferences = "";
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public int getLoyaltyPoints() { return loyaltyPoints; }
    public void setLoyaltyPoints(int loyaltyPoints) {
        this.loyaltyPoints = loyaltyPoints;
    }

    public List<Order> getOrderHistory() { return new ArrayList<>(orderHistory); }

    public String getPreferences() { return preferences; }
    public void setPreferences(String preferences) {
        this.preferences = preferences;
    }

    public void addOrderToHistory(Order order) {
        this.orderHistory.add(order);
        this.loyaltyPoints += (int)(order.getTotalPrice() / 10);
    }

    public double getLoyaltyDiscount() {
        int discountTier = loyaltyPoints / 100;
        return Math.min(discountTier * 0.05, 0.15);
    }

    public boolean isVIP() {
        return loyaltyPoints >= 500;
    }

    public String getCustomerLevel() {
        if (loyaltyPoints >= 500) return "VIP";
        if (loyaltyPoints >= 200) return "Gold";
        if (loyaltyPoints >= 100) return "Silver";
        return "Regular";
    }

    public void receiveNotification(String message) {
        System.out.println("Notification to " + name + " (" + phone + "): " + message);
    }

    @Override
    public String toString() {
        return String.format("Customer{name='%s', phone='%s', points=%d, level=%s}",
                name, phone, loyaltyPoints, getCustomerLevel());
    }
}