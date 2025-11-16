package com.restaurant.patterns.strategy;

public class LoyaltyDiscountStrategy implements PricingStrategy {
    private int loyaltyLevel;

    public LoyaltyDiscountStrategy(int level) {
        this.loyaltyLevel = Math.min(level, 3);
    }

    @Override
    public double calculatePrice(double originalPrice) {
        double discount = loyaltyLevel * 0.05;
        return originalPrice * (1 - discount);
    }

    @Override
    public String getDescription() {
        return "Loyalty Level " + loyaltyLevel + " (-" + (loyaltyLevel * 5) + "%)";
    }
}