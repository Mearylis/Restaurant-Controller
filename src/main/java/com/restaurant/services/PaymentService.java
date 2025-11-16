package com.restaurant.services;

public class PaymentService {
    public boolean processPayment(double amount, String method) {
        System.out.println("Processing payment: $" +
                String.format("%.2f", amount) + " via " + method);

        // Симуляция обработки платежа
        try {
            Thread.sleep(500); // Имитируем задержку
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // В реальности здесь была бы интеграция с payment gateway
        return true;
    }
}
