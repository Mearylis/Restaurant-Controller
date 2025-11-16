package com.restaurant.models;

import com.restaurant.patterns.observer.Observer;
import com.restaurant.patterns.strategy.PricingStrategy;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Order {
    private int orderId;
    private int tableNumber;
    private List<Dish> dishes;
    private OrderStatus status;
    private double totalPrice;
    private List<Observer> observers;
    private Customer customer;
    private String specialInstructions;
    private PricingStrategy pricingStrategy;
    private String assignedWaiter;
    private String assignedChef;

    private List<StatusChange> statusHistory;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;

    public static class StatusChange {
        private final OrderStatus fromStatus;
        private final OrderStatus toStatus;
        private final LocalDateTime timestamp;
        private final String changedBy;

        public StatusChange(OrderStatus fromStatus, OrderStatus toStatus, LocalDateTime timestamp) {
            this.fromStatus = fromStatus;
            this.toStatus = toStatus;
            this.timestamp = timestamp;
            this.changedBy = "System";
        }

        public OrderStatus getFromStatus() { return fromStatus; }
        public OrderStatus getToStatus() { return toStatus; }
        public LocalDateTime getTimestamp() { return timestamp; }
        public String getChangedBy() { return changedBy; }

        @Override
        public String toString() {
            return fromStatus + " → " + toStatus + " at " + timestamp;
        }
    }

    public Order(int orderId, int tableNumber, Customer customer) {
        this.orderId = orderId;
        this.tableNumber = tableNumber;
        this.customer = customer;
        this.dishes = new ArrayList<>();
        this.status = OrderStatus.PENDING;
        this.observers = new ArrayList<>();
        this.specialInstructions = "";
        this.pricingStrategy = null;
        this.assignedWaiter = null;
        this.assignedChef = null;

        this.statusHistory = new ArrayList<>();
        this.createdAt = LocalDateTime.now();
        this.statusHistory.add(new StatusChange(null, OrderStatus.PENDING, this.createdAt));
    }

    public Order(int tableNumber, Customer customer) {
        this(0, tableNumber, customer);
    }

    public void setStatus(OrderStatus newStatus) {
        if (this.status == newStatus) {
            return;
        }

        StatusChange change = new StatusChange(this.status, newStatus, LocalDateTime.now());
        statusHistory.add(change);

        this.status = newStatus;

        if (newStatus == OrderStatus.PAID) {
            this.completedAt = LocalDateTime.now();
        }

        notifyObservers();
    }

    public List<StatusChange> getStatusHistory() {
        return new ArrayList<>(statusHistory);
    }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getCompletedAt() { return completedAt; }

    public void detach(Observer observer) {
        observers.remove(observer);
    }

    public void detachAll() {
        observers.clear();
    }

    public void setAssignedWaiter(String waiterName) {
        this.assignedWaiter = waiterName;
    }

    public void setAssignedChef(String chefName) {
        this.assignedChef = chefName;
    }

    public String getAssignedWaiter() { return assignedWaiter; }
    public String getAssignedChef() { return assignedChef; }

    public void calculateTotal() {
        totalPrice = 0;
        for (Dish dish : dishes) {
            totalPrice += dish.getPrice();
        }

        if (pricingStrategy != null) {
            totalPrice = pricingStrategy.calculatePrice(totalPrice);
        }

        if (customer != null) {
            double loyaltyDiscount = customer.getLoyaltyDiscount();
            if (loyaltyDiscount > 0) {
                double discountAmount = totalPrice * loyaltyDiscount;
                totalPrice -= discountAmount;
            }
        }
    }

    public void setPricingStrategy(PricingStrategy strategy) {
        this.pricingStrategy = strategy;
    }

    public void addDish(Dish dish) {
        dishes.add(dish);
    }

    public void attach(Observer observer) {
        observers.add(observer);
    }

    private void notifyObservers() {
        for (Observer observer : observers) {
            observer.update(this);
        }
    }

    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }
    public int getTableNumber() { return tableNumber; }
    public List<Dish> getDishes() { return new ArrayList<>(dishes); }
    public OrderStatus getStatus() { return status; }
    public double getTotalPrice() { return totalPrice; }
    public Customer getCustomer() { return customer; }
    public String getSpecialInstructions() { return specialInstructions; }
    public void setSpecialInstructions(String instructions) {
        this.specialInstructions = instructions;
    }
}