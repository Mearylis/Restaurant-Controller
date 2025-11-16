package com.restaurant.services;
public class PaymentService {
    public boolean processPayment(double amount, String method) {
        System.out.println("Processing payment: $" + String.format("%.2f", amount) + " via " + method);

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return true;
    }
}
