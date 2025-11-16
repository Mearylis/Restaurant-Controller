package com.restaurant.patterns.strategy;

public class SeasonalStrategy implements PricingStrategy {
    private String seasonName;
    private double discountPercent;

    public SeasonalStrategy(String season, double discount) {
        this.seasonName = season;
        this.discountPercent = discount;
    }

    @Override
    public double calculatePrice(double originalPrice) {
        return originalPrice * (1 - discountPercent / 100);
    }

    @Override
    public String getDescription() {
        return seasonName + " special -" + discountPercent + "%";
    }
}
