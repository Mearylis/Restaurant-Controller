package com.restaurant.patterns.facade;

import com.restaurant.services.*;
import com.restaurant.models.*;
import com.restaurant.patterns.builder.OrderBuilder;
import com.restaurant.patterns.factory.*;
import com.restaurant.patterns.observer.*;
import com.restaurant.patterns.strategy.*;
import com.restaurant.patterns.decorator.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;


public class RestaurantFacade {
    private MenuService menuService;
    private OrderService orderService;
    private TableService tableService;
    private PaymentService paymentService;
    private NotificationService notificationService;
    private List<Employee> staff;
    private AtomicInteger nextOrderId; // ✅ Thread-safe ID generation
    private PricingStrategy currentPricingStrategy;
    private Map<String, DishFactory> dishFactories;

    private KitchenObserver kitchenObserver;
    private ManagerObserver managerObserver;
    private WaiterObserver waiterObserver;
    private CustomerObserver customerObserver;

    public RestaurantFacade() {
        initializeServices();
        initializeDishFactories();
        initializeDefaultData();
        initializeStaff();

        System.out.println("🎭 RestaurantFacade initialized with 6 patterns:");
        System.out.println("   🔨 Builder, 🏭 Factory, 💵 Strategy");
        System.out.println("   🎨 Decorator, 👁️ Observer, 🎭 Facade");
    }

    private void initializeServices() {
        this.menuService = new MenuService();
        this.orderService = new OrderService();
        this.tableService = new TableService();
        this.paymentService = new PaymentService();
        this.notificationService = new NotificationService();
        this.staff = new ArrayList<>();
        this.nextOrderId = new AtomicInteger(1001);
        this.currentPricingStrategy = new RegularPricingStrategy();


        this.kitchenObserver = KitchenObserver.getInstance();
        this.managerObserver = ManagerObserver.getInstance();
        this.waiterObserver = WaiterObserver.getInstance();
        this.customerObserver = CustomerObserver.getInstance();
    }

    private void initializeDishFactories() {
        dishFactories = new HashMap<>();
        dishFactories.put("appetizer", new AppetizerFactory());
        dishFactories.put("maincourse", new MainCourseFactory());
        dishFactories.put("dessert", new DessertFactory());
        dishFactories.put("beverage", new BeverageFactory());
    }

    private void initializeDefaultData() {
        // Создаем столы
        for (int i = 1; i <= 10; i++) {
            tableService.addTable(new Table(i, 2));
        }
        for (int i = 11; i <= 15; i++) {
            tableService.addTable(new Table(i, 4));
        }

        addDishToMenu("appetizer", "Caesar Salad", 8.99);
        addDishToMenu("appetizer", "Garlic Bread", 4.99);
        addDishToMenu("maincourse", "Grilled Steak", 24.99);
        addDishToMenu("maincourse", "Pasta Carbonara", 16.99);
        addDishToMenu("dessert", "Chocolate Cake", 6.50);
        addDishToMenu("beverage", "Fresh Orange Juice", 4.50);
    }

    private void initializeStaff() {
        staff.add(new Employee("John Smith", "Waiter"));
        staff.add(new Employee("Sarah Johnson", "Waiter"));
        staff.add(new Employee("Maria Garcia", "Chef"));
        staff.add(new Employee("David Lee", "Chef"));
        staff.add(new Employee("Robert Brown", "Manager"));


        startAllShifts();
    }


    public Order placeOrderWithCustomDishes(int tableNumber, Customer customer, List<Dish> customizedDishes) {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("🎭 FACADE: Creating order with customized dishes");
        System.out.println("=".repeat(50));

        Table table = tableService.getTableByNumber(tableNumber);
        if (table == null || !table.isOccupied()) {
            System.out.println("❌ Table must be occupied first!");
            return null;
        }

        System.out.println("🔨 Using BUILDER PATTERN...");
        OrderBuilder builder = new OrderBuilder()
                .setTableNumber(tableNumber)
                .setCustomer(customer);


        for (Dish dish : customizedDishes) {
            builder.addDish(dish);
        }

        Order order = builder.build();
        order.setOrderId(nextOrderId.getAndIncrement());

        System.out.println("👁️ Using OBSERVER PATTERN...");
        registerObservers(order);

        System.out.println("💵 Using STRATEGY PATTERN: " + currentPricingStrategy.getDescription());
        order.setPricingStrategy(currentPricingStrategy);
        order.calculateTotal();

        // 5. Назначаем заказ столу
        table.assignOrder(order);

        // 6. Назначаем сотрудников
        System.out.println("👥 Assigning staff members...");
        assignStaffToOrder(order);

        // 7. Сохраняем заказ
        orderService.addOrder(order);

        order.setStatus(OrderStatus.PREPARING);

        System.out.println("✅ Order #" + order.getOrderId() + " created successfully!");
        System.out.println("Total: $" + String.format("%.2f", order.getTotalPrice()));
        System.out.println("=".repeat(50) + "\n");

        return order;
    }

    private void registerObservers(Order order) {

        order.attach(kitchenObserver);
        order.attach(managerObserver);
        order.attach(waiterObserver);
        order.attach(customerObserver);

        System.out.println("👁️ OBSERVER pattern: 4 SINGLETON observers attached to order #" + order.getOrderId());
    }

    private void assignStaffToOrder(Order order) {
        // Ищем свободного официанта
        Employee waiter = staff.stream()
                .filter(e -> e.getRole().equalsIgnoreCase("waiter"))
                .filter(Employee::canTakeMoreOrders)
                .findFirst()
                .orElse(null);

        if (waiter != null) {
            waiter.assignOrder(order);
            // ✅ УВЕДОМЛЕНИЕ О НАЗНАЧЕНИИ
            notificationService.notifyStaffAssignment(order, waiter.getName(), "waiter");
        } else {
            System.out.println("⚠️ No available waiters!");
        }

        // Ищем свободного повара
        Employee chef = staff.stream()
                .filter(e -> e.getRole().equalsIgnoreCase("chef"))
                .filter(Employee::canTakeMoreOrders)
                .findFirst()
                .orElse(null);

        if (chef != null) {
            chef.assignOrder(order);
            // ✅ УВЕДОМЛЕНИЕ О НАЗНАЧЕНИИ
            notificationService.notifyStaffAssignment(order, chef.getName(), "chef");
        } else {
            System.out.println("⚠️ No available chefs!");
        }
    }

    // ========== ✅ НОВЫЕ МЕТОДЫ ДЛЯ ДОСТУПА К СТАТИСТИКЕ ==========

    public Map<String, Object> getKitchenStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("notificationCount", kitchenObserver.getNotificationCount());
        stats.put("ordersInProgress", kitchenObserver.getOrdersInProgress());
        stats.put("recentNotifications", kitchenObserver.getRecentNotifications());
        return stats;
    }

    public Map<String, Object> getManagerAnalytics() {
        return managerObserver.getAnalytics();
    }

    public void printManagerReport() {
        managerObserver.printAnalyticsReport();
    }

    public Map<String, Integer> getWaiterPerformance() {
        return waiterObserver.getAllWaiterStats();
    }

    public Set<String> getVIPCustomers() {
        return customerObserver.getVIPCustomers();
    }
    
    private Employee findAvailableWaiter() {
        return staff.stream()
                .filter(e -> e.getRole().equalsIgnoreCase("waiter"))
                .filter(Employee::canTakeMoreOrders)
                .findFirst()
                .orElse(null);
    }

    private Employee findAvailableChef() {
        return staff.stream()
                .filter(e -> e.getRole().equalsIgnoreCase("chef"))
                .filter(Employee::canTakeMoreOrders)
                .findFirst()
                .orElse(null);
    }


    public void markOrderReady(int orderId) {
        Order order = orderService.getOrderById(orderId);
        if (order != null && order.getStatus() == OrderStatus.PREPARING) {
            order.setStatus(OrderStatus.READY);
            System.out.println("✅ Order #" + orderId + " is READY!");
        }
    }


    public void markOrderServed(int orderId) {
        Order order = orderService.getOrderById(orderId);
        if (order != null && order.getStatus() == OrderStatus.READY) {
            order.setStatus(OrderStatus.SERVED);
            System.out.println("✅ Order #" + orderId + " SERVED to table!");
        }
    }


    public boolean completeOrder(int orderId, String paymentMethod) {
        Order order = orderService.getOrderById(orderId);
        if (order == null) {
            System.out.println("❌ Order not found!");
            return false;
        }

        boolean success = paymentService.processPayment(order.getTotalPrice(), paymentMethod);
        if (success) {
            order.setStatus(OrderStatus.PAID);

            // ✅ ИСПРАВЛЕНИЕ: Используем NotificationService
            notificationService.notifyOrderReady(order);

            // Освобождаем сотрудников
            String waiterName = order.getAssignedWaiter();
            String chefName = order.getAssignedChef();

            if (waiterName != null) {
                staff.stream()
                        .filter(e -> e.getName().equals(waiterName))
                        .findFirst()
                        .ifPresent(e -> e.completeOrder(order));
            }

            if (chefName != null) {
                staff.stream()
                        .filter(e -> e.getName().equals(chefName))
                        .findFirst()
                        .ifPresent(e -> e.completeOrder(order));
            }

            // Освобождаем стол
            freeTable(order.getTableNumber());

            // Добавляем в историю клиента
            order.getCustomer().addOrderToHistory(order);

            System.out.println("💰 Order #" + orderId + " completed and paid!");
            return true;
        }

        return false;
    }

    public Dish customizeDish(String dishName, List<String> addons) {
        System.out.println("🎨 Using DECORATOR PATTERN...");

        Dish dish = menuService.getDishByName(dishName);
        if (dish == null) return null;

        System.out.println("   Base: " + dish.getName() + " ($" + dish.getPrice() + ")");

        for (String addon : addons) {
            switch (addon.toLowerCase()) {
                case "cheese":
                    dish = new ExtraCheeseDecorator(dish);
                    System.out.println("   + Extra Cheese ($2.00)");
                    break;
                case "bacon":
                    dish = new BaconDecorator(dish);
                    System.out.println("   + Bacon ($3.50)");
                    break;
                case "spicy":
                    dish = new SpicyDecorator(dish);
                    System.out.println("   + Spicy Sauce ($1.00)");
                    break;
                case "double":
                    dish = new DoublePortionDecorator(dish);
                    System.out.println("   + Double Portion (+80%)");
                    break;
                case "glutenfree":
                    dish = new GlutenFreeDecorator(dish);
                    System.out.println("   + Gluten Free ($2.50)");
                    break;
            }
        }

        System.out.println("   Final: " + dish.getDescription() + " ($" + dish.getPrice() + ")");
        return dish;
    }


    public void startAllShifts() {
        System.out.println("\n🟢 Starting all shifts...");
        for (Employee employee : staff) {
            if (!employee.isOnDuty()) {
                employee.startShift();
            }
        }
        System.out.println("✅ All staff started their shifts");
    }

    public void endAllShifts() {
        System.out.println("\n🔴 Ending all shifts...");
        for (Employee employee : staff) {
            if (employee.isOnDuty()) {
                employee.endShift();
            }
        }
        System.out.println("✅ All staff ended their shifts");
    }

    public Map<String, Object> getStaffStatistics() {
        Map<String, Object> stats = new HashMap<>();

        long onDuty = staff.stream().filter(Employee::isOnDuty).count();
        long waiters = staff.stream().filter(e -> e.getRole().equalsIgnoreCase("Waiter")).count();
        long chefs = staff.stream().filter(e -> e.getRole().equalsIgnoreCase("Chef")).count();
        long managers = staff.stream().filter(e -> e.getRole().equalsIgnoreCase("Manager")).count();

        int totalAssignedOrders = staff.stream()
                .mapToInt(Employee::getCurrentWorkload).sum();
        int totalCompletedToday = staff.stream()
                .mapToInt(Employee::getCompletedOrdersToday).sum();

        stats.put("totalStaff", staff.size());
        stats.put("onDuty", onDuty);
        stats.put("waiters", waiters);
        stats.put("chefs", chefs);
        stats.put("managers", managers);
        stats.put("assignedOrders", totalAssignedOrders);
        stats.put("completedToday", totalCompletedToday);

        return stats;
    }


    public boolean removeDishFromMenu(String name) {
        boolean removed = menuService.removeDish(name);
        if (removed) {
            System.out.println("🗑️ Removed from menu: " + name);
        }
        return removed;
    }

    public void addDishToMenu(String category, String name, double price) {
        DishFactory factory = dishFactories.get(category.toLowerCase());
        if (factory != null) {
            Dish dish = factory.createDish(name, price);
            menuService.addDish(dish);
        }
    }

    public void occupyTable(int tableNumber, Customer customer) {
        Table table = tableService.getTableByNumber(tableNumber);
        if (table != null && !table.isOccupied()) {
            table.occupyTable(customer);
            System.out.println("🟢 Table #" + tableNumber + " occupied by " + customer.getName());
        }
    }

    public void freeTable(int tableNumber) {
        tableService.freeTable(tableNumber);
        System.out.println("🔴 Table #" + tableNumber + " freed");
    }


    public void setPricingStrategy(String strategyType) {
        switch (strategyType.toLowerCase()) {
            case "happyhour":
                currentPricingStrategy = new HappyHourStrategy();
                break;
            case "weekend":
                currentPricingStrategy = new WeekendStrategy();
                break;
            case "loyalty":
                currentPricingStrategy = new LoyaltyDiscountStrategy(2);
                break;
            default:
                currentPricingStrategy = new RegularPricingStrategy();
        }
        System.out.println("💵 Strategy changed to: " + currentPricingStrategy.getDescription());
    }


    public boolean processPayment(int orderId, String paymentMethod) {
        return completeOrder(orderId, paymentMethod);
    }


    public List<Table> getAvailableTables() {
        return tableService.getAvailableTables();
    }

    public List<Table> getAllTables() {
        return tableService.getAllTables();
    }

    public List<Order> getAllOrders() {
        return orderService.getAllOrders();
    }

    public List<Dish> getMenu() {
        return menuService.getAllDishes();
    }

    public List<Employee> getStaffStatus() {
        return new ArrayList<>(staff);
    }

    public Order getOrderById(int orderId) {
        return orderService.getOrderById(orderId);
    }
}