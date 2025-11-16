package com.restaurant.ui;

import com.restaurant.patterns.facade.RestaurantFacade;
import com.restaurant.models.*;
import com.restaurant.patterns.decorator.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;


public class OrderPanel extends VBox {
    private RestaurantFacade restaurant;
    private Consumer<String> notificationCallback;

    private ComboBox<String> tableCombo;
    private ListView<String> dishesList;
    private CheckBox cheeseCheck, baconCheck, spicyCheck, doubleCheck;
    private ComboBox<String> strategyCombo;
    private TextArea instructionsArea;

    public OrderPanel(RestaurantFacade restaurant, Consumer<String> notificationCallback) {
        this.restaurant = restaurant;
        this.notificationCallback = notificationCallback;

        setSpacing(20);
        setPadding(new Insets(25));
        setStyle("-fx-background-color: white;");

        createUI();
    }

    private void createUI() {
        // Заголовок
        Label title = new Label("📝 Create New Order");
        title.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        Label subtitle = new Label("Complete demonstration of all 6 Design Patterns");
        subtitle.setStyle("-fx-font-size: 16px; -fx-text-fill: #7f8c8d;");

        // Секция 1: Выбор стола
        VBox tableSection = createTableSection();

        // Секция 2: Выбор блюд
        VBox dishSection = createDishSection();

        // Секция 3: Декораторы
        VBox decoratorSection = createDecoratorSection();

        // Секция 4: Стратегия ценообразования
        VBox strategySection = createStrategySection();

        // Секция 5: Специальные инструкции
        VBox instructionsSection = createInstructionsSection();

        // Кнопки
        HBox buttonBox = createButtons();

        // Информационная панель
        VBox infoPanel = createInfoPanel();

        // Компоновка
        ScrollPane scrollPane = new ScrollPane();
        VBox content = new VBox(15);
        content.getChildren().addAll(
                title, subtitle,
                new Separator(),
                tableSection,
                dishSection,
                decoratorSection,
                strategySection,
                instructionsSection,
                new Separator(),
                buttonBox,
                infoPanel
        );

        scrollPane.setContent(content);
        scrollPane.setFitToWidth(true);
        getChildren().add(scrollPane);
    }

    private VBox createTableSection() {
        VBox section = new VBox(10);
        section.setStyle("-fx-background-color: #ecf0f1; -fx-padding: 15; -fx-background-radius: 10;");

        Label label = new Label("1️⃣ Select Table");
        label.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        tableCombo = new ComboBox<>();
        tableCombo.setPrefWidth(300);
        tableCombo.setPromptText("Choose an occupied table...");

        refreshTableList();

        Button refreshBtn = new Button("🔄 Refresh");
        refreshBtn.setOnAction(e -> refreshTableList());

        HBox box = new HBox(15);
        box.setAlignment(Pos.CENTER_LEFT);
        box.getChildren().addAll(tableCombo, refreshBtn);

        section.getChildren().addAll(label, box);
        return section;
    }

    private void refreshTableList() {
        tableCombo.getItems().clear();
        restaurant.getAllTables().stream()
                .filter(Table::isOccupied)
                .forEach(table -> {
                    String customerName = table.getCurrentCustomer() != null ?
                            table.getCurrentCustomer().getName() : "Guest";
                    tableCombo.getItems().add("Table " + table.getTableNumber() + " - " + customerName);
                });

        if (tableCombo.getItems().isEmpty()) {
            notificationCallback.accept("⚠️ No occupied tables! Go to Tables panel to occupy one.");
        }
    }

    private VBox createDishSection() {
        VBox section = new VBox(10);
        section.setStyle("-fx-background-color: #e8f5e9; -fx-padding: 15; -fx-background-radius: 10;");

        Label label = new Label("2️⃣ Select Dishes (🏭 Factory Pattern)");
        label.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Label hint = new Label("Hold Ctrl/Cmd to select multiple dishes");
        hint.setStyle("-fx-font-size: 12px; -fx-text-fill: #7f8c8d;");

        dishesList = new ListView<>();
        dishesList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        dishesList.setPrefHeight(200);

        restaurant.getMenu().forEach(dish ->
                dishesList.getItems().add(dish.getName() + " - $" + String.format("%.2f", dish.getPrice()) +
                        " [" + dish.getCategory() + "]"));

        section.getChildren().addAll(label, hint, dishesList);
        return section;
    }

    private VBox createDecoratorSection() {
        VBox section = new VBox(10);
        section.setStyle("-fx-background-color: #fff3e0; -fx-padding: 15; -fx-background-radius: 10;");

        Label label = new Label("3️⃣ Customize Dishes (🎨 Decorator Pattern)");
        label.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        cheeseCheck = new CheckBox("Extra Cheese (+$2.00)");
        baconCheck = new CheckBox("Bacon (+$3.50)");
        spicyCheck = new CheckBox("Spicy Sauce 🌶️ (+$1.00)");
        doubleCheck = new CheckBox("Double Portion (+80%)");

        cheeseCheck.setStyle("-fx-font-size: 14px;");
        baconCheck.setStyle("-fx-font-size: 14px;");
        spicyCheck.setStyle("-fx-font-size: 14px;");
        doubleCheck.setStyle("-fx-font-size: 14px;");

        section.getChildren().addAll(label, cheeseCheck, baconCheck, spicyCheck, doubleCheck);
        return section;
    }

    private VBox createStrategySection() {
        VBox section = new VBox(10);
        section.setStyle("-fx-background-color: #e1f5fe; -fx-padding: 15; -fx-background-radius: 10;");

        Label label = new Label("4️⃣ Pricing Strategy (💵 Strategy Pattern)");
        label.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        strategyCombo = new ComboBox<>();
        strategyCombo.getItems().addAll(
                "Regular - No discount",
                "Happy Hour - 20% off",
                "Weekend - +10% surcharge",
                "Loyalty - Discount based on points"
        );
        strategyCombo.setValue("Regular - No discount");
        strategyCombo.setPrefWidth(300);

        section.getChildren().addAll(label, strategyCombo);
        return section;
    }

    private VBox createInstructionsSection() {
        VBox section = new VBox(10);
        section.setStyle("-fx-background-color: #f3e5f5; -fx-padding: 15; -fx-background-radius: 10;");

        Label label = new Label("5️⃣ Special Instructions");
        label.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        instructionsArea = new TextArea();
        instructionsArea.setPrefHeight(80);
        instructionsArea.setPromptText("E.g., No onions, extra sauce, cook well-done, allergies...");
        instructionsArea.setWrapText(true);

        section.getChildren().addAll(label, instructionsArea);
        return section;
    }

    private HBox createButtons() {
        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(20, 0, 0, 0));

        Button createBtn = new Button("✅ Create Order");
        createBtn.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; " +
                "-fx-font-size: 18px; -fx-padding: 15 40; -fx-font-weight: bold;");
        createBtn.setOnAction(e -> createOrder());

        Button clearBtn = new Button("🗑️ Clear Form");
        clearBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; " +
                "-fx-font-size: 16px; -fx-padding: 15 30;");
        clearBtn.setOnAction(e -> clearForm());

        Button demoBtn = new Button("🎬 Run Pattern Demo");
        demoBtn.setStyle("-fx-background-color: #9b59b6; -fx-text-fill: white; " +
                "-fx-font-size: 16px; -fx-padding: 15 30;");
        demoBtn.setOnAction(e -> runPatternDemo());

        buttonBox.getChildren().addAll(createBtn, clearBtn, demoBtn);
        return buttonBox;
    }

    private VBox createInfoPanel() {
        VBox panel = new VBox(10);
        panel.setStyle("-fx-background-color: #fafafa; -fx-padding: 20; " +
                "-fx-border-color: #bdc3c7; -fx-border-width: 2; -fx-border-radius: 10;");

        Label title = new Label("ℹ️ How This Demonstrates 6 Patterns:");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        Label info = new Label(
                "🔨 BUILDER - Step-by-step order construction\n" +
                        "🏭 FACTORY - Dishes created by category factories\n" +
                        "💵 STRATEGY - Dynamic pricing algorithms\n" +
                        "🎨 DECORATOR - Adding toppings/modifications\n" +
                        "👁️ OBSERVER - Staff gets notified automatically\n" +
                        "🎭 FACADE - Single interface for complex operations"
        );
        info.setStyle("-fx-font-size: 14px; -fx-text-fill: #2c3e50;");
        info.setWrapText(true);

        panel.getChildren().addAll(title, info);
        return panel;
    }

    private void createOrder() {
        // ✅ ВАЛИДАЦИЯ: Проверяем входные данные
        if (tableCombo.getValue() == null) {
            showAlert("⚠️ No Table Selected", "Please select an occupied table!");
            return;
        }

        if (dishesList.getSelectionModel().isEmpty()) {
            showAlert("⚠️ No Dishes Selected", "Please select at least one dish!");
            return;
        }

        try {
            notificationCallback.accept("🎭 Starting order creation using FACADE pattern...");

            // Извлекаем номер стола
            String tableStr = tableCombo.getValue().split(" ")[1];
            int tableNumber = Integer.parseInt(tableStr);

            // Получаем клиента
            Table table = restaurant.getAllTables().stream()
                    .filter(t -> t.getTableNumber() == tableNumber)
                    .findFirst()
                    .orElse(null);

            if (table == null || table.getCurrentCustomer() == null) {
                showAlert("❌ Error", "Table not found or has no customer!");
                return;
            }

            Customer customer = table.getCurrentCustomer();

            // Устанавливаем стратегию
            String strategy = strategyCombo.getValue().split(" ")[0].toLowerCase();
            restaurant.setPricingStrategy(strategy);
            notificationCallback.accept("💵 STRATEGY pattern: Applied " + strategy + " pricing");

            // ✅ ИСПРАВЛЕНИЕ: Собираем блюда с ПРИМЕНЕНИЕМ ДЕКОРАТОРОВ
            List<Dish> customizedDishes = new ArrayList<>();
            for (String item : dishesList.getSelectionModel().getSelectedItems()) {
                String dishName = item.split(" - ")[0];
                Dish dish = restaurant.getMenu().stream()
                        .filter(d -> d.getName().equals(dishName))
                        .findFirst()
                        .orElse(null);

                if (dish != null) {
                    // ✅ ПРИМЕНЯЕМ ДЕКОРАТОРЫ К КАЖДОМУ БЛЮДУ
                    if (cheeseCheck.isSelected()) {
                        dish = new ExtraCheeseDecorator(dish);
                        notificationCallback.accept("🎨 DECORATOR: Added extra cheese to " + dishName);
                    }
                    if (baconCheck.isSelected()) {
                        dish = new BaconDecorator(dish);
                        notificationCallback.accept("🎨 DECORATOR: Added bacon to " + dishName);
                    }
                    if (spicyCheck.isSelected()) {
                        dish = new SpicyDecorator(dish);
                        notificationCallback.accept("🎨 DECORATOR: Added spicy sauce to " + dishName);
                    }
                    if (doubleCheck.isSelected()) {
                        dish = new DoublePortionDecorator(dish);
                        notificationCallback.accept("🎨 DECORATOR: Double portion for " + dishName);
                    }
                    customizedDishes.add(dish);
                }
            }

            // ✅ ВАЛИДАЦИЯ: Проверяем что есть блюда после кастомизации
            if (customizedDishes.isEmpty()) {
                showAlert("❌ Error", "No valid dishes found after customization!");
                return;
            }

            notificationCallback.accept("🏭 FACTORY pattern: Created " + customizedDishes.size() + " customized dishes");

            // Создаем заказ через FACADE с уже настроенными блюдами
            notificationCallback.accept("🔨 BUILDER pattern: Constructing order step-by-step");

            // Используем фасад для создания заказа с кастомизированными блюдами
            Order order = restaurant.placeOrderWithCustomDishes(tableNumber, customer, customizedDishes);

            if (order != null) {
                // Специальные инструкции
                if (!instructionsArea.getText().trim().isEmpty()) {
                    order.setSpecialInstructions(instructionsArea.getText());
                    notificationCallback.accept("📋 Special instructions added to order");
                }

                notificationCallback.accept("👁️ OBSERVER pattern: Staff notified automatically");
                notificationCallback.accept("✅ Order #" + order.getOrderId() + " created successfully!");

                // Показываем успех
                showSuccessDialog(order);

                // Очищаем форму
                clearForm();
                refreshTableList();
            } else {
                showAlert("❌ Error", "Failed to create order!");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            showAlert("❌ Error", "Error creating order: " + ex.getMessage());
        }
    }

    private void showSuccessDialog(Order order) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("✅ Order Created Successfully!");
        alert.setHeaderText("Order #" + order.getOrderId());

        StringBuilder content = new StringBuilder();
        content.append("Table: ").append(order.getTableNumber()).append("\n");
        content.append("Customer: ").append(order.getCustomer().getName()).append("\n");
        content.append("Dishes: ").append(order.getDishes().size()).append(" items\n");
        content.append("Total: $").append(String.format("%.2f", order.getTotalPrice())).append("\n");
        content.append("Status: ").append(order.getStatus()).append("\n\n");

        if (order.getAssignedWaiter() != null) {
            content.append("👨‍💼 Waiter: ").append(order.getAssignedWaiter()).append("\n");
        }
        if (order.getAssignedChef() != null) {
            content.append("👨‍🍳 Chef: ").append(order.getAssignedChef()).append("\n");
        }

        content.append("\n🎉 All 6 Design Patterns Demonstrated!\n");
        content.append("🔨 Builder | 🏭 Factory | 💵 Strategy\n");
        content.append("🎨 Decorator | 👁️ Observer | 🎭 Facade");

        alert.setContentText(content.toString());
        alert.showAndWait();
    }

    private void clearForm() {
        tableCombo.setValue(null);
        dishesList.getSelectionModel().clearSelection();
        cheeseCheck.setSelected(false);
        baconCheck.setSelected(false);
        spicyCheck.setSelected(false);
        doubleCheck.setSelected(false);
        strategyCombo.setValue("Regular - No discount");
        instructionsArea.clear();
        notificationCallback.accept("🗑️ Form cleared");
    }

    private void runPatternDemo() {
        notificationCallback.accept("🎬 Running Design Pattern Demonstration...");
        notificationCallback.accept("==================================================");

        // Демонстрация каждого паттерна
        notificationCallback.accept("🔨 BUILDER: OrderBuilder creates orders step-by-step");
        notificationCallback.accept("🏭 FACTORY: Different factories for Appetizer/Main/Dessert/Beverage");
        notificationCallback.accept("💵 STRATEGY: Regular/HappyHour/Weekend/Loyalty pricing strategies");
        notificationCallback.accept("🎨 DECORATOR: ExtraCheese/Bacon/Spicy/Double decorators");
        notificationCallback.accept("👁️ OBSERVER: Kitchen/Waiter/Customer/Manager observers");
        notificationCallback.accept("🎭 FACADE: RestaurantFacade simplifies complex operations");
        notificationCallback.accept("==================================================");

        showAlert("🎬 Pattern Demo",
                "Check the notification panel for details!\n\n" +
                        "All 6 patterns are working together to create a seamless restaurant experience.");
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}