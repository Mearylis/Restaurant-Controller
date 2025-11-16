package com.restaurant.ui;

import com.restaurant.patterns.facade.RestaurantFacade;
import com.restaurant.models.Order;
import com.restaurant.models.OrderStatus;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.beans.property.SimpleStringProperty;
import java.util.function.Consumer;

/**
 * ✅ CORRECTED: Order Management Panel with proper generics
 */
public class OrderManagementPanel extends VBox {
    private RestaurantFacade restaurant;
    private TableView<Order> ordersTable;
    private Consumer<String> notificationCallback;

    public OrderManagementPanel(RestaurantFacade restaurant, Consumer<String> notificationCallback) {
        this.restaurant = restaurant;
        this.notificationCallback = notificationCallback;

        setSpacing(15);
        setPadding(new Insets(25));
        setStyle("-fx-background-color: white;");

        createUI();
        loadOrders();
    }

    private void createUI() {
        Label title = new Label("📋 Order Management");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        Label subtitle = new Label("Real-time order tracking with Observer Pattern");
        subtitle.setStyle("-fx-font-size: 14px; -fx-text-fill: #7f8c8d;");

        // ✅ CORRECTED: Properly typed TableView
        ordersTable = new TableView<>();
        ordersTable.setPrefHeight(400);

        // ✅ CORRECTED: Proper column definitions with generics
        TableColumn<Order, String> idCol = new TableColumn<>("Order ID");
        idCol.setCellValueFactory(cellData ->
                new SimpleStringProperty("#" + cellData.getValue().getOrderId()));

        TableColumn<Order, String> tableCol = new TableColumn<>("Table");
        tableCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(String.valueOf(cellData.getValue().getTableNumber())));

        TableColumn<Order, String> customerCol = new TableColumn<>("Customer");
        customerCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getCustomer().getName()));

        TableColumn<Order, String> dishesCol = new TableColumn<>("Dishes");
        dishesCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getDishes().size() + " items"));

        TableColumn<Order, String> totalCol = new TableColumn<>("Total");
        totalCol.setCellValueFactory(cellData ->
                new SimpleStringProperty("$" + String.format("%.2f", cellData.getValue().getTotalPrice())));

        TableColumn<Order, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getStatus().toString()));

        TableColumn<Order, String> waiterCol = new TableColumn<>("Waiter");
        waiterCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getAssignedWaiter() != null ?
                        cellData.getValue().getAssignedWaiter() : "N/A"));

        TableColumn<Order, String> chefCol = new TableColumn<>("Chef");
        chefCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getAssignedChef() != null ?
                        cellData.getValue().getAssignedChef() : "N/A"));

        // ✅ CORRECTED: Add all columns to the table
        ordersTable.getColumns().add(idCol);
        ordersTable.getColumns().add(tableCol);
        ordersTable.getColumns().add(customerCol);
        ordersTable.getColumns().add(dishesCol);
        ordersTable.getColumns().add(totalCol);
        ordersTable.getColumns().add(statusCol);
        ordersTable.getColumns().add(waiterCol);
        ordersTable.getColumns().add(chefCol);

        // Кнопки управления
        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);

        Button readyBtn = new Button("✅ Mark Ready");
        readyBtn.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white;");
        readyBtn.setOnAction(e -> markOrderReady());

        Button servedBtn = new Button("🍽️ Mark Served");
        servedBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
        servedBtn.setOnAction(e -> markOrderServed());

        Button completeBtn = new Button("💰 Complete & Pay");
        completeBtn.setStyle("-fx-background-color: #9b59b6; -fx-text-fill: white;");
        completeBtn.setOnAction(e -> completeOrder());

        Button refreshBtn = new Button("🔄 Refresh");
        refreshBtn.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white;");
        refreshBtn.setOnAction(e -> loadOrders());

        buttonBox.getChildren().addAll(readyBtn, servedBtn, completeBtn, refreshBtn);

        getChildren().addAll(title, subtitle, ordersTable, buttonBox);
    }

    private void loadOrders() {
        ordersTable.getItems().clear();
        ordersTable.getItems().addAll(restaurant.getAllOrders());
        notificationCallback.accept("📋 Orders refreshed - " + restaurant.getAllOrders().size() + " total");
    }

    private void markOrderReady() {
        Order selected = ordersTable.getSelectionModel().getSelectedItem();
        if (selected != null && selected.getStatus() == OrderStatus.PREPARING) {
            restaurant.markOrderReady(selected.getOrderId());
            loadOrders();
            notificationCallback.accept("✅ Order #" + selected.getOrderId() + " marked READY");
        } else {
            showAlert("Please select an order in PREPARING status!");
        }
    }

    private void markOrderServed() {
        Order selected = ordersTable.getSelectionModel().getSelectedItem();
        if (selected != null && selected.getStatus() == OrderStatus.READY) {
            restaurant.markOrderServed(selected.getOrderId());
            loadOrders();
            notificationCallback.accept("🍽️ Order #" + selected.getOrderId() + " marked SERVED");
        } else {
            showAlert("Please select an order in READY status!");
        }
    }

    private void completeOrder() {
        Order selected = ordersTable.getSelectionModel().getSelectedItem();
        if (selected != null && selected.getStatus() == OrderStatus.SERVED) {
            boolean success = restaurant.completeOrder(selected.getOrderId(), "Cash");
            if (success) {
                loadOrders();
                notificationCallback.accept("💰 Order #" + selected.getOrderId() + " completed!");
            }
        } else {
            showAlert("Please select an order in SERVED status!");
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Order Management");
        alert.setContentText(message);
        alert.showAndWait();
    }
}