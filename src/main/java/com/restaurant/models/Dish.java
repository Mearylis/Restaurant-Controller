package com.restaurant.models;

public class Dish {
    protected String name;
    protected double price;
    protected String description;
    protected String category;
    public Dish(String name, double price) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Dish name cannot be null or empty");
        }
        if (price < 0) {
            throw new IllegalArgumentException("Dish price cannot be negative: " + price);
        }

        this.name = name;
        this.price = price;
        this.description = name + " ($" + String.format("%.2f", price) + ")";
        this.category = "Unknown";
    }

    public String getName() { return name; }
    public double getPrice() { return price; }
    public String getDescription() {
        return description;
    }

    public void setCategory(String category) {
        if (category == null || category.trim().isEmpty()) {
            throw new IllegalArgumentException("Category cannot be null or empty");
        }
        this.category = category;
    }

    public String getCategory() { return category; }
}