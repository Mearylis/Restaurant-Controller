package com.restaurant.services;

import com.restaurant.models.*;
import java.util.*;

public class MenuService {
    private List<Dish> menu;

    public MenuService() {
        this.menu = new ArrayList<>();
    }

    public void addDish(Dish dish) {
        menu.add(dish);
    }

    public Dish getDishByName(String name) {
        return menu.stream()
                .filter(d -> d.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    public List<Dish> getAllDishes() {
        return new ArrayList<>(menu);
    }

    public boolean removeDish(String name) {
        return menu.removeIf(d -> d.getName().equalsIgnoreCase(name));
    }

    public boolean updateDish(String oldName, Dish newDish) {
        for (int i = 0; i < menu.size(); i++) {
            if (menu.get(i).getName().equalsIgnoreCase(oldName)) {
                menu.set(i, newDish);
                return true;
            }
        }
        return false;
    }
}