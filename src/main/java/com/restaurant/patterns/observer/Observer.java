package com.restaurant.patterns.observer;
import com.restaurant.models.Order;
public interface Observer {
    void update(Order order);
}