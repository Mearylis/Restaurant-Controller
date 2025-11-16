package com.restaurant.ui;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.logging.Level;

// ✅ УТИЛИТНЫЙ КЛАСС ДЛЯ ПЕРЕИСПОЛЬЗОВАНИЯ UI КОДА
public class UIUtils {
    private static final Logger logger = Logger.getLogger(UIUtils.class.getName());

    public static void showInfo(String title, String message) {
        logger.log(Level.INFO, title + ": " + message);
        showAlert(Alert.AlertType.INFORMATION, title, null, message);
    }

    public static void showError(String title, String message) {
        logger.log(Level.SEVERE, title + ": " + message);
        showAlert(Alert.AlertType.ERROR, title, null, message);
    }

    public static void showWarning(String title, String message) {
        logger.log(Level.WARNING, title + ": " + message);
        showAlert(Alert.AlertType.WARNING, title, null, message);
    }

    public static boolean showConfirmation(String title, String message) {
        logger.log(Level.INFO, "Confirmation dialog: " + title + " - " + message);

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    private static void showAlert(Alert.AlertType alertType, String title, String header, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // ✅ ВАЛИДАЦИЯ ПОЛЕЙ
    public static boolean validateRequiredField(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            showError("Validation Error", fieldName + " is required");
            return false;
        }
        return true;
    }

    public static boolean validatePrice(String priceStr, String fieldName) {
        try {
            double price = Double.parseDouble(priceStr);
            if (price < 0) {
                showError("Validation Error", fieldName + " cannot be negative");
                return false;
            }
            return true;
        } catch (NumberFormatException e) {
            showError("Validation Error", fieldName + " must be a valid number");
            return false;
        }
    }

    public static boolean validateInteger(String intStr, String fieldName) {
        try {
            Integer.parseInt(intStr);
            return true;
        } catch (NumberFormatException e) {
            showError("Validation Error", fieldName + " must be a valid integer");
            return false;
        }
    }

    // ✅ ФОРМАТИРОВАНИЕ
    public static String formatPrice(double price) {
        return String.format("$%.2f", price);
    }

    public static String formatDateTime(java.time.LocalDateTime dateTime) {
        if (dateTime == null) return "N/A";
        return dateTime.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }
}