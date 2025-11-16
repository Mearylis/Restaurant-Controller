package com.restaurant.patterns.strategy;

public class HappyHourStrategy implements PricingStrategy {
    @Override
    public double calculatePrice(double originalPrice) {
        return originalPrice * 0.8;
    }

    @Override
    public String getDescription() {
        return "Happy Hour -20%";
    }
}