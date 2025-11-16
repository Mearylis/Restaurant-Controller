package com.restaurant.ui;

import com.restaurant.models.Dish;
import com.restaurant.models.Order;
import com.restaurant.patterns.builder.OrderBuilder;
import com.restaurant.patterns.decorator.ExtraCheeseDecorator;
import com.restaurant.patterns.factory.DishFactory;
import com.restaurant.patterns.factory.MainCourseFactory;
import com.restaurant.patterns.strategy.PricingStrategy;
import com.restaurant.patterns.strategy.HappyHourStrategy;

/**
 * Unit тесты для проверки паттернов (можно использовать JUnit)
 */
public class PatternTests {

    public static void runAllTests() {
        System.out.println("\n🧪 === RUNNING TESTS ===\n");

        testBuilderPattern();
        testDecoratorPattern();
        testFactoryPattern();
        testStrategyPattern();

        System.out.println("\n✅ All tests passed!");
    }

    private static void testBuilderPattern() {
        System.out.println("Testing Builder Pattern...");

        OrderBuilder builder = new OrderBuilder();
        Order order = builder
                .setTableNumber(1)
                .addDish(new Dish("Test Dish", 10.0))
                .build();

        assert order.getTableNumber() == 1;
        assert order.getDishes().size() == 1;
        System.out.println("✓ Builder Pattern works correctly");
    }

    private static void testDecoratorPattern() {
        System.out.println("Testing Decorator Pattern...");

        Dish dish = new Dish("Base", 10.0);
        dish = new ExtraCheeseDecorator(dish);

        assert dish.getPrice() == 12.0;
        assert dish.getDescription().contains("Extra Cheese");
        System.out.println("✓ Decorator Pattern works correctly");
    }

    private static void testFactoryPattern() {
        System.out.println("Testing Factory Pattern...");

        DishFactory factory = new MainCourseFactory();
        Dish dish = factory.createDish("Test", 20.0);

        assert dish.getCategory().equals("Main Course");
        assert dish.getPrice() == 20.0;
        System.out.println("✓ Factory Pattern works correctly");
    }

    private static void testStrategyPattern() {
        System.out.println("Testing Strategy Pattern...");

        PricingStrategy happyHour = new HappyHourStrategy();
        double discounted = happyHour.calculatePrice(100.0);

        assert discounted == 80.0; // 20% off
        System.out.println("✓ Strategy Pattern works correctly");
    }
}
