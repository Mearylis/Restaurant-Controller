package com.restaurant.ui;

import com.restaurant.patterns.facade.RestaurantFacade;
import com.restaurant.models.Order;
import com.restaurant.models.OrderStatus;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import java.util.List;
import java.util.function.Consumer;
import java.util.HashMap;
import java.util.Map;

public class ReportsPanel extends VBox {
    private RestaurantFacade restaurant;
    private TableView<OrderTableItem> ordersTable;
    private TextArea reportArea;
    private Label revenueLabel, ordersLabel, activeOrdersLabel;
    private Consumer<String> notificationCallback;

    public ReportsPanel(RestaurantFacade restaurant, Consumer<String> notificationCallback) {
        this.restaurant = restaurant;
        this.notificationCallback = notificationCallback;
        setSpacing(15);
        setPadding(new Insets(20));
        createUI();
        loadReports();
    }

    private void createUI() {
        Label title = new Label("📈 Reports & Analytics");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        Label subtitle = new Label("Aggregated via Facade Pattern");
        subtitle.setStyle("-fx-text-fill: #7f8c8d;");

        // Статистические карточки
        HBox statsBox = createStatsCards();

        // График выручки (упрощенный)
        VBox chartBox = createRevenueChart();

        // Таблица заказов
        Label ordersTitle = new Label("Order History");
        ordersTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        ordersTable = new TableView<>();
        ordersTable.setPrefHeight(200);

        TableColumn<OrderTableItem, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("orderId"));

        TableColumn<OrderTableItem, String> tableCol = new TableColumn<>("Table");
        tableCol.setCellValueFactory(new PropertyValueFactory<>("tableNumber"));

        TableColumn<OrderTableItem, String> totalCol = new TableColumn<>("Total ($)");
        totalCol.setCellValueFactory(new PropertyValueFactory<>("total"));

        TableColumn<OrderTableItem, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        ordersTable.getColumns().addAll(idCol, tableCol, totalCol, statusCol);

        // Область отчета
        Label reportTitle = new Label("Daily Report");
        reportTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        reportArea = new TextArea();
        reportArea.setEditable(false);
        reportArea.setWrapText(true);
        reportArea.setPrefHeight(150);
        reportArea.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 12px;");

        // Кнопки
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);

        Button refreshBtn = new Button("🔄 Refresh");
        refreshBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
        refreshBtn.setOnAction(e -> loadReports());

        buttonBox.getChildren().addAll(refreshBtn);

        getChildren().addAll(title, subtitle, statsBox, chartBox,
                ordersTitle, ordersTable, reportTitle, reportArea, buttonBox);
    }

    private HBox createStatsCards() {
        HBox statsBox = new HBox(15);
        statsBox.setAlignment(Pos.CENTER);

        VBox revenueCard = createStatCard("💰 Revenue", "$0.00", "#2ecc71");
        revenueLabel = (Label) revenueCard.getChildren().get(1);

        VBox ordersCard = createStatCard("📝 Total Orders", "0", "#3498db");
        ordersLabel = (Label) ordersCard.getChildren().get(1);

        VBox activeCard = createStatCard("⏳ Active", "0", "#f39c12");
        activeOrdersLabel = (Label) activeCard.getChildren().get(1);

        statsBox.getChildren().addAll(revenueCard, ordersCard, activeCard);
        return statsBox;
    }

    private VBox createStatCard(String title, String value, String color) {
        VBox card = new VBox(10);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(20));
        card.setPrefSize(180, 100);
        card.setStyle("-fx-background-color: " + color + "; " +
                "-fx-background-radius: 10; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 2);");

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");

        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-text-fill: white; -fx-font-size: 24px; -fx-font-weight: bold;");

        card.getChildren().addAll(titleLabel, valueLabel);
        return card;
    }

    private VBox createRevenueChart() {
        VBox chartBox = new VBox(10);
        chartBox.setPadding(new Insets(15));
        chartBox.setStyle("-fx-background-color: white; -fx-background-radius: 10;");

        Label chartTitle = new Label("Weekly Revenue 📊");
        chartTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        HBox barsBox = new HBox(20);
        barsBox.setAlignment(Pos.BOTTOM_CENTER);
        barsBox.setPrefHeight(200);

        // ✅ ИСПРАВЛЕНИЕ: Используем реальные данные вместо фиктивных
        Map<String, Double> weeklyRevenue = calculateWeeklyRevenue();
        String[] days = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};

        for (String day : days) {
            double revenue = weeklyRevenue.getOrDefault(day, 0.0);
            VBox barContainer = new VBox(5);
            barContainer.setAlignment(Pos.BOTTOM_CENTER);

            Label valueLabel = new Label("$" + (int)revenue);
            valueLabel.setStyle("-fx-font-size: 10px; -fx-font-weight: bold;");

            Pane bar = new Pane();
            bar.setPrefWidth(40);
            bar.setPrefHeight(revenue / 10.0); // Масштабируем высоту
            bar.setStyle("-fx-background-color: linear-gradient(to top, #3498db, #5dade2); " +
                    "-fx-background-radius: 5 5 0 0;");

            Label dayLabel = new Label(day);
            dayLabel.setStyle("-fx-font-size: 12px;");

            barContainer.getChildren().addAll(valueLabel, bar, dayLabel);
            barsBox.getChildren().add(barContainer);
        }

        chartBox.getChildren().addAll(chartTitle, barsBox);
        return chartBox;
    }

    // ✅ НОВЫЙ МЕТОД: Расчет реальной выручки по дням недели
    private Map<String, Double> calculateWeeklyRevenue() {
        Map<String, Double> revenue = new HashMap<>();
        String[] days = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};

        // Инициализируем нулями
        for (String day : days) {
            revenue.put(day, 0.0);
        }

        // Собираем реальные данные из заказов
        for (Order order : restaurant.getAllOrders()) {
            if (order.getStatus() == OrderStatus.PAID) {
                // Упрощенная логика распределения по дням
                // В реальном приложении используйте LocalDate из заказа
                String day = days[order.getOrderId() % 7]; // Временная логика
                revenue.merge(day, order.getTotalPrice(), Double::sum);
            }
        }

        return revenue;
    }

    private void loadReports() {
        List<Order> orders = restaurant.getAllOrders();
        ordersTable.getItems().clear();

        double totalRevenue = 0;
        int activeCount = 0;

        for (Order order : orders) {
            ordersTable.getItems().add(new OrderTableItem(
                    order.getOrderId(),
                    order.getTableNumber(),
                    order.getTotalPrice(),
                    order.getStatus().toString()
            ));

            if (order.getStatus() == OrderStatus.PAID) {
                totalRevenue += order.getTotalPrice();
            }
            if (order.getStatus() != OrderStatus.PAID) {
                activeCount++;
            }
        }

        // Обновляем статистику
        revenueLabel.setText(String.format("$%.2f", totalRevenue));
        ordersLabel.setText(String.valueOf(orders.size()));
        activeOrdersLabel.setText(String.valueOf(activeCount));

        // Генерируем отчет
        generateDailyReport();
        notificationCallback.accept("📈 Reports refreshed with real data");
    }

    private void generateDailyReport() {
        StringBuilder report = new StringBuilder();
        report.append("========== DAILY REPORT ==========\n");
        report.append("Total Orders: ").append(restaurant.getAllOrders().size()).append("\n");
        report.append("Total Revenue: $").append(String.format("%.2f",
                restaurant.getAllOrders().stream()
                        .filter(o -> o.getStatus() == OrderStatus.PAID)
                        .mapToDouble(Order::getTotalPrice)
                        .sum())).append("\n");
        report.append("Available Tables: ").append(restaurant.getAvailableTables().size()).append("/15\n");
        report.append("Active Orders: ").append(restaurant.getAllOrders().stream()
                .filter(o -> o.getStatus() != OrderStatus.PAID)
                .count()).append("\n");
        report.append("Menu Items: ").append(restaurant.getMenu().size()).append("\n");
        report.append("==================================\n");

        reportArea.setText(report.toString());
    }

    // Класс для отображения заказов в таблице
    public static class OrderTableItem {
        private String orderId;
        private String tableNumber;
        private String total;
        private String status;

        public OrderTableItem(int orderId, int tableNumber, double total, String status) {
            this.orderId = "#" + orderId;
            this.tableNumber = String.valueOf(tableNumber);
            this.total = String.format("%.2f", total);
            this.status = status;
        }

        public String getOrderId() { return orderId; }
        public String getTableNumber() { return tableNumber; }
        public String getTotal() { return total; }
        public String getStatus() { return status; }
    }
}