package com.restaurant.patterns.strategy;

public interface PricingStrategy {
    double calculatePrice(double originalPrice);
    String getDescription();
}