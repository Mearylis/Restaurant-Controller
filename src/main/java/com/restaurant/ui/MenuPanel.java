package com.restaurant.ui;

import com.restaurant.patterns.facade.RestaurantFacade;
import com.restaurant.models.Dish;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import java.util.List;
import java.util.function.Consumer;

public class MenuPanel extends VBox {
    private RestaurantFacade restaurant;
    private TableView<DishTableItem> menuTable;
    private ObservableList<DishTableItem> menuData;
    private Consumer<String> notificationCallback;

    public MenuPanel(RestaurantFacade restaurant, Consumer<String> notificationCallback) {
        this.restaurant = restaurant;
        this.notificationCallback = notificationCallback;
        this.menuData = FXCollections.observableArrayList();

        setSpacing(15);
        setPadding(new Insets(20));

        createUI();
        loadMenuData();
    }

    private void createUI() {
        // Заголовок
        Label title = new Label("🍕 Menu Management");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        Label subtitle = new Label("Dishes created via Factory Pattern");
        subtitle.setStyle("-fx-text-fill: #7f8c8d;");

        // Кнопка добавления
        Button addBtn = new Button("➕ Add New Dish");
        addBtn.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 20;");
        addBtn.setOnAction(e -> showAddDishDialog());

        HBox headerBox = new HBox(10);
        headerBox.setAlignment(Pos.CENTER_LEFT);
        headerBox.getChildren().addAll(title, subtitle, addBtn);

        // Таблица меню
        menuTable = new TableView<>();
        menuTable.setPrefHeight(400);

        TableColumn<DishTableItem, String> nameCol = new TableColumn<>("Dish Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setPrefWidth(200);

        TableColumn<DishTableItem, String> categoryCol = new TableColumn<>("Category");
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));
        categoryCol.setPrefWidth(150);

        TableColumn<DishTableItem, String> priceCol = new TableColumn<>("Price ($)");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        priceCol.setPrefWidth(100);

        TableColumn<DishTableItem, String> factoryCol = new TableColumn<>("Factory");
        factoryCol.setCellValueFactory(new PropertyValueFactory<>("factory"));
        factoryCol.setPrefWidth(150);

        TableColumn<DishTableItem, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusCol.setPrefWidth(120);

        menuTable.getColumns().addAll(nameCol, categoryCol, priceCol, factoryCol, statusCol);
        menuTable.setItems(menuData);

        // Кнопки управления
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);

        Button toggleBtn = new Button("🔄 Toggle Status");
        toggleBtn.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white;");
        toggleBtn.setOnAction(e -> toggleDishStatus());

        Button deleteBtn = new Button("🗑️ Delete");
        deleteBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
        deleteBtn.setOnAction(e -> deleteDish());

        buttonBox.getChildren().addAll(toggleBtn, deleteBtn);

        getChildren().addAll(headerBox, menuTable, buttonBox);
    }

    private void loadMenuData() {
        menuData.clear();
        List<Dish> dishes = restaurant.getMenu();
        for (Dish dish : dishes) {
            String factory = "Unknown";
            if (dish.getCategory().contains("Appetizer")) factory = "AppetizerFactory";
            else if (dish.getCategory().contains("Main")) factory = "MainCourseFactory";
            else if (dish.getCategory().contains("Dessert")) factory = "DessertFactory";
            else if (dish.getCategory().contains("Beverage")) factory = "BeverageFactory";

            menuData.add(new DishTableItem(dish.getName(), dish.getCategory(),
                    dish.getPrice(), factory, "Available"));
        }
    }

    private void showAddDishDialog() {
        Dialog<Dish> dialog = new Dialog<>();
        dialog.setTitle("Add New Dish");
        dialog.setHeaderText("Create dish using Factory Pattern");

        // Кнопки
        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        // Поля формы
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField nameField = new TextField();
        nameField.setPromptText("Pizza Margherita");

        ComboBox<String> categoryCombo = new ComboBox<>();
        categoryCombo.getItems().addAll("Appetizer", "Main Course", "Dessert", "Beverage");
        categoryCombo.setValue("Main Course");

        TextField priceField = new TextField();
        priceField.setPromptText("12.99");

        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Category:"), 0, 1);
        grid.add(categoryCombo, 1, 1);
        grid.add(new Label("Price:"), 0, 2);
        grid.add(priceField, 1, 2);

        dialog.getDialogPane().setContent(grid);

        // Конвертация результата
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                try {
                    String name = nameField.getText();
                    String category = categoryCombo.getValue();
                    String priceText = priceField.getText();

                    // ✅ ИСПОЛЬЗОВАНИЕ UIUtils ДЛЯ ВАЛИДАЦИИ
                    if (!UIUtils.validateRequiredField(name, "Dish name")) return null;
                    if (!UIUtils.validatePrice(priceText, "Price")) return null;

                    double price = Double.parseDouble(priceText);

                    // Добавляем через фасад
                    String categoryKey = category.toLowerCase().replace(" ", "");
                    restaurant.addDishToMenu(categoryKey, name, price);

                    notificationCallback.accept("✅ Added via " + getFactoryName(category) + ": " + name);
                    return new Dish(name, price);
                } catch (NumberFormatException e) {
                    // ✅ ИСПОЛЬЗОВАНИЕ UIUtils ДЛЯ ОШИБОК
                    UIUtils.showError("Error", "Invalid price format!");
                } catch (Exception e) {
                    UIUtils.showError("Error", "Failed to add dish: " + e.getMessage());
                }
            }
            return null;
        });

        dialog.showAndWait();
        loadMenuData(); // Обновляем таблицу
    }

    private String getFactoryName(String category) {
        switch(category) {
            case "Appetizer": return "AppetizerFactory";
            case "Main Course": return "MainCourseFactory";
            case "Dessert": return "DessertFactory";
            case "Beverage": return "BeverageFactory";
            default: return "UnknownFactory";
        }
    }

    private void toggleDishStatus() {
        DishTableItem selected = menuTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            String newStatus = selected.getStatus().equals("Available") ? "Out of Stock" : "Available";
            selected.setStatus(newStatus);
            menuTable.refresh();
            notificationCallback.accept("🔄 " + selected.getName() + " is now " + newStatus);
        } else {
            // ✅ ИСПОЛЬЗОВАНИЕ UIUtils
            UIUtils.showWarning("No Selection", "Please select a dish first!");
        }
    }

    private void deleteDish() {
        DishTableItem selected = menuTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            // ✅ ИСПОЛЬЗОВАНИЕ UIUtils ДЛЯ ПОДТВЕРЖДЕНИЯ
            boolean confirmed = UIUtils.showConfirmation("Confirm Delete",
                    "Delete " + selected.getName() + "?\nThis action cannot be undone.");

            if (confirmed) {
                restaurant.removeDishFromMenu(selected.getName());
                menuData.remove(selected);
                notificationCallback.accept("🗑️ Deleted: " + selected.getName());
            }
        } else {
            UIUtils.showWarning("No Selection", "Please select a dish to delete!");
        }
    }

    // Класс для отображения в таблице
    public static class DishTableItem {
        private String name;
        private String category;
        private String price;
        private String factory;
        private String status;

        public DishTableItem(String name, String category, double price, String factory, String status) {
            this.name = name;
            this.category = category;
            this.price = UIUtils.formatPrice(price); // ✅ ИСПОЛЬЗОВАНИЕ UIUtils
            this.factory = factory;
            this.status = status;
        }

        public String getName() { return name; }
        public String getCategory() { return category; }
        public String getPrice() { return price; }
        public String getFactory() { return factory; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }
}