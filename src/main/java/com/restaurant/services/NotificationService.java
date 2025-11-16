package com.restaurant.services;

import com.restaurant.models.*;
import java.util.logging.Logger;
import java.util.logging.Level;

public class NotificationService {
    private static final Logger logger = Logger.getLogger(NotificationService.class.getName());

    public NotificationService() {
        logger.info("🔔 NotificationService initialized");
    }

    public void notifyNewOrder(Order order) {
        String message = "📧 New order #" + order.getOrderId() + " for table " + order.getTableNumber();

        // Логируем
        logger.log(Level.INFO, message);


        System.out.println(message);
        simulateEmailNotification(order.getCustomer().getEmail(), "New Order Confirmation", message);
        simulateSMSNotification(order.getCustomer().getPhone(), message);
    }

    public void notifyOrderReady(Order order) {
        String message = "🔔 Order #" + order.getOrderId() + " is READY for serving!";

        logger.log(Level.INFO, message);
        System.out.println(message);

        // Уведомление официанта
        if (order.getAssignedWaiter() != null) {
            simulatePushNotification(order.getAssignedWaiter(), message);
        }

        // Уведомление клиента
        simulateSMSNotification(order.getCustomer().getPhone(),
                "Your order #" + order.getOrderId() + " is ready! Please proceed to the counter.");
    }

    // ✅ ЗАГЛУШКИ ДЛЯ РЕАЛЬНЫХ СЕРВИСОВ
    private void simulateEmailNotification(String email, String subject, String body) {
        // В реальности: JavaMail API или SendGrid
        System.out.println("📧 [EMAIL to " + email + "] Subject: " + subject);
        System.out.println("   Body: " + body);
        logger.log(Level.INFO, "Simulated email to " + email + ": " + subject);
    }

    private void simulateSMSNotification(String phone, String message) {
        // В реальности: Twilio или другой SMS gateway
        System.out.println("📱 [SMS to " + phone + "]: " + message);
        logger.log(Level.INFO, "Simulated SMS to " + phone + ": " + message);
    }

    private void simulatePushNotification(String recipient, String message) {
        // В реальности: Firebase Cloud Messaging или аналоги
        System.out.println("📲 [PUSH to " + recipient + "]: " + message);
        logger.log(Level.INFO, "Simulated push notification to " + recipient + ": " + message);
    }

    // ✅ УВЕДОМЛЕНИЕ О СТАТУСЕ
    public void notifyStatusChange(Order order, OrderStatus oldStatus, OrderStatus newStatus) {
        String message = "🔄 Order #" + order.getOrderId() + " status changed: " +
                oldStatus + " → " + newStatus;

        logger.log(Level.INFO, message);

        if (newStatus == OrderStatus.READY) {
            notifyOrderReady(order);
        }
    }

    // ✅ УВЕДОМЛЕНИЕ О НАЗНАЧЕНИИ СОТРУДНИКА
    public void notifyStaffAssignment(Order order, String staffName, String role) {
        String message = "👥 " + staffName + " assigned as " + role + " for order #" + order.getOrderId();
        logger.log(Level.INFO, message);
        System.out.println(message);
    }
}