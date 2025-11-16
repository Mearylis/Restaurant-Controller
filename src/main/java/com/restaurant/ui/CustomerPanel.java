package com.restaurant.ui;

import com.restaurant.models.Customer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import java.util.function.Consumer;

public class CustomerPanel extends VBox {
    private TableView<Customer> customerTable;
    private ObservableList<Customer> customerData;
    private Consumer<String> notificationCallback;

    public CustomerPanel(Consumer<String> notificationCallback) {
        this.notificationCallback = notificationCallback;
        this.customerData = FXCollections.observableArrayList();

        setSpacing(15);
        setPadding(new Insets(20));
        createUI();
        loadSampleCustomers();
    }

    private void createUI() {
        Label title = new Label("👥 Customer Management");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        // Таблица клиентов
        customerTable = new TableView<>();

        TableColumn<Customer, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Customer, String> phoneCol = new TableColumn<>("Phone");
        phoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));

        TableColumn<Customer, String> levelCol = new TableColumn<>("Level");
        // Исправлено: используем лямбду вместо PropertyValueFactory для вычисляемого поля
        levelCol.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getCustomerLevel()));

        TableColumn<Customer, Integer> pointsCol = new TableColumn<>("Points");
        pointsCol.setCellValueFactory(new PropertyValueFactory<>("loyaltyPoints"));

        customerTable.getColumns().addAll(nameCol, phoneCol, levelCol, pointsCol);
        customerTable.setItems(customerData);

        getChildren().addAll(title, customerTable);
    }

    private void loadSampleCustomers() {
        customerData.addAll(
                new Customer("Alice Brown", "+1234567801", "alice@email.com"),
                new Customer("Bob Wilson", "+1234567802", "bob@email.com"),
                new Customer("Carol Davis", "+1234567803", "carol@email.com")
        );

        // Добавляем заказы для тестирования лояльности
        customerData.get(0).setLoyaltyPoints(350); // Gold
        customerData.get(1).setLoyaltyPoints(600); // VIP
        customerData.get(2).setLoyaltyPoints(120); // Silver
    }
}
