package com.restaurant.patterns.strategy;

public class RegularPricingStrategy implements PricingStrategy {
    @Override
    public double calculatePrice(double originalPrice) {
        return originalPrice;
    }

    @Override
    public String getDescription() {
        return "Regular pricing";
    }
}