package com.restaurant.patterns.builder;
import com.restaurant.models.*;
import java.util.ArrayList;
import java.util.List;

public class OrderBuilder {
    private int tableNumber;
    private List<Dish> dishes;
    private String specialInstructions;
    private Customer customer;

    public OrderBuilder() {
        this.dishes = new ArrayList<>();
        this.specialInstructions = "";
    }

    public OrderBuilder setTableNumber(int tableNumber) {
        this.tableNumber = tableNumber;
        return this;
    }

    public OrderBuilder addDish(Dish dish) {
        this.dishes.add(dish);
        return this;
    }

    public OrderBuilder setSpecialInstructions(String instructions) {
        this.specialInstructions = instructions;
        return this;
    }

    public OrderBuilder setCustomer(Customer customer) {
        this.customer = customer;
        return this;
    }

    public Order build() {
        if (tableNumber <= 0) {
            throw new IllegalStateException("Table number must be set!");
        }
        if (dishes.isEmpty()) {
            throw new IllegalStateException("Order must have at least one dish!");
        }

        Order order;
        if (customer != null) {
            order = new Order(tableNumber, customer);
        } else {
            order = new Order(tableNumber,
                    new Customer("Guest", "000-0000", "guest@email.com"));
        }

        for (Dish dish : dishes) {
            order.addDish(dish);
        }

        if (!specialInstructions.isEmpty()) {
            order.setSpecialInstructions(specialInstructions);
        }

        return order;
    }
}