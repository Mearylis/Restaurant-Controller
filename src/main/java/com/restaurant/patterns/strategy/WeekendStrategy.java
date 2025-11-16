package com.restaurant.patterns.strategy;
public class WeekendStrategy implements PricingStrategy {
    @Override
    public double calculatePrice(double originalPrice) {
        return originalPrice * 1.1;
    }

    @Override
    public String getDescription() {
        return "Weekend discount +10%";
    }
}