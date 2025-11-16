package com.restaurant.models;

public class Table {
    private int tableNumber;
    private int capacity;
    private boolean isOccupied;
    private Customer currentCustomer;
    private Order currentOrder;

    public Table(int tableNumber, int capacity) {
        this.tableNumber = tableNumber;
        this.capacity = capacity;
        this.isOccupied = false;
        this.currentCustomer = null;
        this.currentOrder = null;
    }

    public int getTableNumber() { return tableNumber; }
    public int getCapacity() { return capacity; }
    public boolean isOccupied() { return isOccupied; }
    public Customer getCurrentCustomer() { return currentCustomer; }
    public Order getCurrentOrder() { return currentOrder; }

    public void occupyTable(Customer customer) {
        this.isOccupied = true;
        this.currentCustomer = customer;
        this.currentOrder = null;
    }

    public void assignOrder(Order order) {
        this.currentOrder = order;
    }

    public void freeTable() {
        this.isOccupied = false;
        this.currentCustomer = null;
        this.currentOrder = null;
    }

    public String getStatus() {
        if (!isOccupied) return "Available";
        if (currentOrder == null) return "Seated (No order)";
        return "Dining (Order #" + currentOrder.getOrderId() + ")";
    }

    @Override
    public String toString() {
        return String.format("Table #%d (%d seats) - %s",
                tableNumber, capacity, getStatus());
    }
}