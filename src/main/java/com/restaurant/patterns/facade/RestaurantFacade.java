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
    private AtomicInteger nextOrderId;
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
        Table table = tableService.getTableByNumber(tableNumber);
        if (table == null || !table.isOccupied()) {
            return null;
        }

        OrderBuilder builder = new OrderBuilder()
                .setTableNumber(tableNumber)
                .setCustomer(customer);

        for (Dish dish : customizedDishes) {
            builder.addDish(dish);
        }

        Order order = builder.build();
        order.setOrderId(nextOrderId.getAndIncrement());

        registerObservers(order);

        order.setPricingStrategy(currentPricingStrategy);
        order.calculateTotal();

        table.assignOrder(order);

        assignStaffToOrder(order);

        orderService.addOrder(order);

        order.setStatus(OrderStatus.PREPARING);

        return order;
    }

    private void registerObservers(Order order) {
        order.attach(kitchenObserver);
        order.attach(managerObserver);
        order.attach(waiterObserver);
        order.attach(customerObserver);
    }

    private void assignStaffToOrder(Order order) {
        Employee waiter = findAvailableWaiter();
        if (waiter != null) {
            waiter.assignOrder(order);
            notificationService.notifyStaffAssignment(order, waiter.getName(), "waiter");
        }

        Employee chef = findAvailableChef();
        if (chef != null) {
            chef.assignOrder(order);
            notificationService.notifyStaffAssignment(order, chef.getName(), "chef");
        }
    }

    private Employee findAvailableWaiter() {
        for (Employee employee : staff) {
            if (employee.getRole().equalsIgnoreCase("waiter") && employee.canTakeMoreOrders()) {
                return employee;
            }
        }
        return null;
    }

    private Employee findAvailableChef() {
        for (Employee employee : staff) {
            if (employee.getRole().equalsIgnoreCase("chef") && employee.canTakeMoreOrders()) {
                return employee;
            }
        }
        return null;
    }

    public void markOrderReady(int orderId) {
        Order order = orderService.getOrderById(orderId);
        if (order != null && order.getStatus() == OrderStatus.PREPARING) {
            order.setStatus(OrderStatus.READY);
        }
    }

    public void markOrderServed(int orderId) {
        Order order = orderService.getOrderById(orderId);
        if (order != null && order.getStatus() == OrderStatus.READY) {
            order.setStatus(OrderStatus.SERVED);
        }
    }

    public boolean completeOrder(int orderId, String paymentMethod) {
        Order order = orderService.getOrderById(orderId);
        if (order == null) {
            return false;
        }

        boolean success = paymentService.processPayment(order.getTotalPrice(), paymentMethod);
        if (success) {
            order.setStatus(OrderStatus.PAID);

            notificationService.notifyOrderReady(order);

            String waiterName = order.getAssignedWaiter();
            String chefName = order.getAssignedChef();

            if (waiterName != null) {
                for (Employee employee : staff) {
                    if (employee.getName().equals(waiterName)) {
                        employee.completeOrder(order);
                        break;
                    }
                }
            }

            if (chefName != null) {
                for (Employee employee : staff) {
                    if (employee.getName().equals(chefName)) {
                        employee.completeOrder(order);
                        break;
                    }
                }
            }

            freeTable(order.getTableNumber());

            order.getCustomer().addOrderToHistory(order);

            return true;
        }

        return false;
    }

    public void startAllShifts() {
        for (Employee employee : staff) {
            if (!employee.isOnDuty()) {
                employee.startShift();
            }
        }
    }

    public void endAllShifts() {
        for (Employee employee : staff) {
            if (employee.isOnDuty()) {
                employee.endShift();
            }
        }
    }

    public Map<String, Object> getStaffStatistics() {
        Map<String, Object> stats = new HashMap<>();

        int onDuty = 0;
        int waiters = 0;
        int chefs = 0;
        int managers = 0;
        int totalAssignedOrders = 0;
        int totalCompletedToday = 0;

        for (Employee employee : staff) {
            if (employee.isOnDuty()) {
                onDuty++;
            }

            if (employee.getRole().equalsIgnoreCase("Waiter")) {
                waiters++;
            } else if (employee.getRole().equalsIgnoreCase("Chef")) {
                chefs++;
            } else if (employee.getRole().equalsIgnoreCase("Manager")) {
                managers++;
            }

            totalAssignedOrders += employee.getCurrentWorkload();
            totalCompletedToday += employee.getCompletedOrdersToday();
        }

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
        return menuService.removeDish(name);
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
        }
    }

    public void freeTable(int tableNumber) {
        tableService.freeTable(tableNumber);
    }

    public void setPricingStrategy(String strategyType) {
        if (strategyType.equalsIgnoreCase("happyhour")) {
            currentPricingStrategy = new HappyHourStrategy();
        } else if (strategyType.equalsIgnoreCase("weekend")) {
            currentPricingStrategy = new WeekendStrategy();
        } else if (strategyType.equalsIgnoreCase("loyalty")) {
            currentPricingStrategy = new LoyaltyDiscountStrategy(2);
        } else {
            currentPricingStrategy = new RegularPricingStrategy();
        }
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