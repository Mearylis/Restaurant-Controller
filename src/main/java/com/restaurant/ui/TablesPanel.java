package com.restaurant.ui;

import com.restaurant.patterns.facade.RestaurantFacade;
import com.restaurant.models.Table;
import com.restaurant.models.Customer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.util.List;
import java.util.function.Consumer;

public class TablesPanel extends VBox {
    private RestaurantFacade restaurant;
    private GridPane tablesGrid;
    private Label statsLabel;
    private Consumer<String> notificationCallback;

    public TablesPanel(RestaurantFacade restaurant, Consumer<String> notificationCallback) {
        this.restaurant = restaurant;
        this.notificationCallback = notificationCallback;
        setSpacing(15);
        setPadding(new Insets(20));
        createUI();
        loadTables();
    }

    private void createUI() {
        Label title = new Label("🪑 Table Management");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        statsLabel = new Label();
        statsLabel.setStyle("-fx-font-size: 16px;");
        updateStats();

        tablesGrid = new GridPane();
        tablesGrid.setHgap(15);
        tablesGrid.setVgap(15);
        tablesGrid.setPadding(new Insets(20));
        tablesGrid.setAlignment(Pos.CENTER);

        ScrollPane scrollPane = new ScrollPane(tablesGrid);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        getChildren().addAll(title, statsLabel, scrollPane);
    }

    private void loadTables() {
        tablesGrid.getChildren().clear();

        List<Table> allTables = restaurant.getAllTables();
        int cols = 5;
        int row = 0, col = 0;

        for (Table table : allTables) {
            VBox tableCard = createTableCard(table);
            tablesGrid.add(tableCard, col, row);

            col++;
            if (col >= cols) {
                col = 0;
                row++;
            }
        }

        updateStats();
    }

    private VBox createTableCard(Table table) {
        VBox card = new VBox(10);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(15));
        card.setPrefSize(150, 120);

        boolean isAvailable = !table.isOccupied();
        String bgColor = isAvailable ? "#2ecc71" : "#e74c3c";
        String textColor = "white";

        card.setStyle("-fx-background-color: " + bgColor + "; -fx-background-radius: 15;");

        Label tableIcon = new Label("🪑");
        tableIcon.setStyle("-fx-font-size: 40px;");

        Label numberLabel = new Label("Table #" + table.getTableNumber());
        numberLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: " + textColor + ";");

        Label capacityLabel = new Label("Seats: " + table.getCapacity());
        capacityLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: " + textColor + ";");

        Label statusLabel = new Label(table.getStatus());
        statusLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + textColor + ";");

        card.getChildren().addAll(tableIcon, numberLabel, capacityLabel, statusLabel);

        // Обработчик клика
        card.setOnMouseClicked(e -> handleTableClick(table, isAvailable));

        return card;
    }

    private void handleTableClick(Table table, boolean isAvailable) {
        if (isAvailable) {
            // Занимаем стол
            Customer customer = new Customer("Walk-in Customer", "000-0000", "walkin@restaurant.com");
            restaurant.occupyTable(table.getTableNumber(), customer);
            notificationCallback.accept("🟢 Table #" + table.getTableNumber() + " occupied");
        } else {
            // Освобождаем стол
            restaurant.freeTable(table.getTableNumber());
            notificationCallback.accept("🔴 Table #" + table.getTableNumber() + " freed");
        }
        loadTables();
    }

    private void updateStats() {
        List<Table> allTables = restaurant.getAllTables();
        long available = allTables.stream().filter(t -> !t.isOccupied()).count();
        long occupied = allTables.size() - available;

        statsLabel.setText(String.format("📊 Statistics: %d available | %d occupied | Total: %d",
                available, occupied, allTables.size()));
    }
}