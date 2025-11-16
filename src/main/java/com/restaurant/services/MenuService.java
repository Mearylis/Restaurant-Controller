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
        for (Dish dish : menu) {
            if (dish.getName().equalsIgnoreCase(name)) {
                return dish;
            }
        }
        return null;
    }

    public List<Dish> getAllDishes() {
        return new ArrayList<>(menu);
    }

    public boolean removeDish(String name) {
        for (Iterator<Dish> iterator = menu.iterator(); iterator.hasNext();) {
            Dish dish = iterator.next();
            if (dish.getName().equalsIgnoreCase(name)) {
                iterator.remove();
                return true;
            }
        }
        return false;
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