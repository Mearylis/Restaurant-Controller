package com.restaurant.ui;

import com.restaurant.patterns.facade.RestaurantFacade;
import com.restaurant.models.Employee;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleIntegerProperty;
import java.util.Map;
import java.util.function.Consumer;

/**
 * ПАНЕЛЬ УПРАВЛЕНИЯ ПЕРСОНАЛОМ
 * Показывает реальную нагрузку сотрудников (OBSERVER PATTERN)
 */
public class StaffPanel extends VBox {
    private RestaurantFacade restaurant;
    private TableView<Employee> staffTable;
    private ObservableList<Employee> staffData;
    private Consumer<String> notificationCallback;
    private Label statsLabel;

    public StaffPanel(RestaurantFacade restaurant, Consumer<String> notificationCallback) {
        this.restaurant = restaurant;
        this.notificationCallback = notificationCallback;
        this.staffData = FXCollections.observableArrayList();

        setSpacing(15);
        setPadding(new Insets(25));
        setStyle("-fx-background-color: white;");

        createUI();
        loadStaffData();
    }

    private void createUI() {
        // Заголовок
        Label title = new Label("👥 Staff Management");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        Label subtitle = new Label("Real-time workload monitoring using OBSERVER PATTERN");
        subtitle.setStyle("-fx-font-size: 14px; -fx-text-fill: #7f8c8d;");

        // Статистика
        statsLabel = new Label();
        statsLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #2c3e50; " +
                "-fx-padding: 15 0; -fx-font-weight: bold;");

        // Кнопки управления
        HBox controlBox = new HBox(15);
        controlBox.setAlignment(Pos.CENTER_LEFT);
        controlBox.setPadding(new Insets(10, 0, 10, 0));

        Button startShiftBtn = new Button("🟢 Start All Shifts");
        Button endShiftBtn = new Button("🔴 End All Shifts");
        Button refreshBtn = new Button("🔄 Refresh Data");
        Button assignOrderBtn = new Button("📋 Auto-Assign Orders");

        startShiftBtn.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; " +
                "-fx-font-size: 13px; -fx-padding: 10 20; -fx-font-weight: bold;");
        endShiftBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; " +
                "-fx-font-size: 13px; -fx-padding: 10 20; -fx-font-weight: bold;");
        refreshBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; " +
                "-fx-font-size: 13px; -fx-padding: 10 20;");
        assignOrderBtn.setStyle("-fx-background-color: #9b59b6; -fx-text-fill: white; " +
                "-fx-font-size: 13px; -fx-padding: 10 20;");

        startShiftBtn.setOnAction(e -> startAllShifts());
        endShiftBtn.setOnAction(e -> endAllShifts());
        refreshBtn.setOnAction(e -> loadStaffData());
        assignOrderBtn.setOnAction(e -> {
            notificationCallback.accept("📋 Orders will be auto-assigned by OBSERVER pattern");
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Auto-Assignment");
            alert.setHeaderText("Observer Pattern Active");
            alert.setContentText("Orders are automatically assigned to available staff " +
                    "when they are created. Check the notifications panel!");
            alert.showAndWait();
        });

        controlBox.getChildren().addAll(startShiftBtn, endShiftBtn, refreshBtn, assignOrderBtn);

        // Таблица сотрудников
        staffTable = new TableView<>();
        setupStaffTable();
        staffTable.setItems(staffData);

        // Легенда
        HBox legend = createLegend();

        getChildren().addAll(title, subtitle, statsLabel, controlBox, staffTable, legend);
    }

    /**
     * Настройка таблицы персонала БЕЗ WARNINGS
     */
    private void setupStaffTable() {
        staffTable.setPrefHeight(400);
        staffTable.setStyle("-fx-font-size: 13px;");

        // Колонка: Имя
        TableColumn<Employee, String> nameCol = new TableColumn<>("Name");
        nameCol.setPrefWidth(150);
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        // Колонка: Должность
        TableColumn<Employee, String> roleCol = new TableColumn<>("Role");
        roleCol.setPrefWidth(100);
        roleCol.setCellValueFactory(new PropertyValueFactory<>("role"));

        // Колонка: Статус (вычисляемое поле)
        TableColumn<Employee, String> statusCol = new TableColumn<>("Status");
        statusCol.setPrefWidth(120);
        statusCol.setCellValueFactory(cellData -> {
            Employee emp = cellData.getValue();
            String status = emp.isOnDuty() ? "🟢 On Duty" : "🔴 Off Duty";
            return new SimpleStringProperty(status);
        });

        // Колонка: Нагрузка (вычисляемое поле)
        TableColumn<Employee, String> workloadCol = new TableColumn<>("Workload");
        workloadCol.setPrefWidth(180);
        workloadCol.setCellValueFactory(cellData -> {
            Employee emp = cellData.getValue();
            return new SimpleStringProperty(emp.getWorkloadStatus());
        });

        // Колонка: Текущие заказы
        TableColumn<Employee, Integer> currentOrdersCol = new TableColumn<>("Current Orders");
        currentOrdersCol.setPrefWidth(120);
        currentOrdersCol.setCellValueFactory(cellData -> {
            Employee emp = cellData.getValue();
            return new SimpleIntegerProperty(emp.getCurrentWorkload()).asObject();
        });

        // Колонка: Выполнено сегодня
        TableColumn<Employee, Integer> completedCol = new TableColumn<>("Completed Today");
        completedCol.setPrefWidth(140);
        completedCol.setCellValueFactory(new PropertyValueFactory<>("completedOrdersToday"));

        // Колонка: Эффективность
        TableColumn<Employee, String> efficiencyCol = new TableColumn<>("Efficiency");
        efficiencyCol.setPrefWidth(100);
        efficiencyCol.setCellValueFactory(cellData -> {
            Employee emp = cellData.getValue();
            double efficiency = emp.getEfficiency();
            String formatted = String.format("%.1f%%", efficiency);
            return new SimpleStringProperty(formatted);
        });

        // Колонка: Макс. заказов
        TableColumn<Employee, Integer> maxOrdersCol = new TableColumn<>("Max Orders");
        maxOrdersCol.setPrefWidth(100);
        maxOrdersCol.setCellValueFactory(new PropertyValueFactory<>("maxOrders"));

        // Добавляем все колонки
        staffTable.getColumns().add(nameCol);
        staffTable.getColumns().add(roleCol);
        staffTable.getColumns().add(statusCol);
        staffTable.getColumns().add(workloadCol);
        staffTable.getColumns().add(currentOrdersCol);
        staffTable.getColumns().add(completedCol);
        staffTable.getColumns().add(efficiencyCol);
        staffTable.getColumns().add(maxOrdersCol);

        // Цветная подсветка строк по нагрузке
        staffTable.setRowFactory(tv -> new TableRow<Employee>() {
            @Override
            protected void updateItem(Employee emp, boolean empty) {
                super.updateItem(emp, empty);
                if (empty || emp == null) {
                    setStyle("");
                } else {
                    if (!emp.isOnDuty()) {
                        setStyle("-fx-background-color: #ecf0f1;");
                    } else {
                        String workload = emp.getWorkloadStatus();
                        if (workload.contains("Heavy")) {
                            setStyle("-fx-background-color: #ffebee;"); // Красный фон
                        } else if (workload.contains("Moderate")) {
                            setStyle("-fx-background-color: #fff9c4;"); // Желтый фон
                        } else {
                            setStyle("-fx-background-color: #e8f5e9;"); // Зеленый фон
                        }
                    }
                }
            }
        });
    }

    /**
     * Загружаем данные о персонале
     */
    private void loadStaffData() {
        staffData.clear();
        staffData.addAll(restaurant.getStaffStatus());

        // Обновляем статистику
        Map<String, Object> stats = restaurant.getStaffStatistics();

        statsLabel.setText(String.format(
                "📊 Total Staff: %d | 🟢 On Duty: %d | 👨‍💼 Waiters: %d | 👨‍🍳 Chefs: %d | " +
                        "👔 Managers: %d | 📋 Assigned Orders: %d | ✅ Completed Today: %d",
                stats.get("totalStaff"),
                stats.get("onDuty"),
                stats.get("waiters"),
                stats.get("chefs"),
                stats.get("managers"),
                stats.get("assignedOrders"),
                stats.get("completedToday")
        ));

        notificationCallback.accept("👥 Staff data refreshed - " + staffData.size() + " employees");

        // Логируем детали каждого сотрудника
        System.out.println("\n=== STAFF STATUS ===");
        for (Employee emp : staffData) {
            System.out.printf("%s (%s): %s - %d/%d orders - %.1f%% efficiency%n",
                    emp.getName(),
                    emp.getRole(),
                    emp.isOnDuty() ? "ON DUTY" : "OFF DUTY",
                    emp.getCurrentWorkload(),
                    emp.getMaxOrders(),
                    emp.getEfficiency());
        }
        System.out.println("===================\n");
    }

    /**
     * Запускаем смену для всех
     */
    private void startAllShifts() {
        System.out.println("\n🟢 Starting shifts for all staff...");
        restaurant.startAllShifts();
        loadStaffData();
        notificationCallback.accept("🟢 All staff started their shifts");

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Shifts Started");
        alert.setHeaderText("All staff are now on duty!");
        alert.setContentText("Staff members can now receive order assignments " +
                "through the OBSERVER pattern.");
        alert.showAndWait();
    }

    /**
     * Заканчиваем смену для всех
     */
    private void endAllShifts() {
        System.out.println("\n🔴 Ending shifts for all staff...");

        // Подтверждение
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("End All Shifts");
        confirm.setHeaderText("Are you sure?");
        confirm.setContentText("This will end shifts for all staff members. " +
                "Active orders will remain assigned.");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                restaurant.endAllShifts();
                loadStaffData();
                notificationCallback.accept("🔴 All staff ended their shifts");

                Alert info = new Alert(Alert.AlertType.INFORMATION);
                info.setTitle("Shifts Ended");
                info.setHeaderText("All staff are now off duty");
                info.setContentText("Staff performance summary has been logged.");
                info.showAndWait();
            }
        });
    }

    /**
     * Создаем легенду для цветов
     */
    private HBox createLegend() {
        HBox legend = new HBox(30);
        legend.setAlignment(Pos.CENTER);
        legend.setPadding(new Insets(15, 0, 0, 0));
        legend.setStyle("-fx-background-color: #f8f9fa; -fx-padding: 15; " +
                "-fx-background-radius: 10;");

        Label title = new Label("Workload Legend: ");
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");

        HBox lightBox = createLegendItem("🟢 Light", "#e8f5e9", "< 40%");
        HBox moderateBox = createLegendItem("🟡 Moderate", "#fff9c4", "40-75%");
        HBox heavyBox = createLegendItem("🔴 Heavy", "#ffebee", "> 75%");
        HBox offDutyBox = createLegendItem("⚪ Off Duty", "#ecf0f1", "Not working");

        legend.getChildren().addAll(title, lightBox, moderateBox, heavyBox, offDutyBox);
        return legend;
    }

    private HBox createLegendItem(String text, String color, String hint) {
        HBox box = new HBox(8);
        box.setAlignment(Pos.CENTER_LEFT);

        Region colorBox = new Region();
        colorBox.setPrefSize(30, 20);
        colorBox.setStyle("-fx-background-color: " + color + "; " +
                "-fx-border-color: #bdc3c7; -fx-border-width: 1; " +
                "-fx-border-radius: 3; -fx-background-radius: 3;");

        Label label = new Label(text);
        label.setStyle("-fx-font-size: 12px; -fx-font-weight: bold;");

        Label hintLabel = new Label("(" + hint + ")");
        hintLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #7f8c8d;");

        box.getChildren().addAll(colorBox, label, hintLabel);
        return box;
    }
}