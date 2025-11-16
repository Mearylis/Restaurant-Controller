package com.restaurant.services;

import com.restaurant.models.*;
import java.util.*;

public class TableService {
    private List<Table> tables;

    public TableService() {
        this.tables = new ArrayList<>();
    }

    public void addTable(Table table) {
        tables.add(table);
    }

    public boolean isTableAvailable(int tableNumber) {
        return tables.stream()
                .filter(t -> t.getTableNumber() == tableNumber)
                .findFirst()
                .map(t -> !t.isOccupied())
                .orElse(false);
    }

    public void occupyTable(int tableNumber) {
        tables.stream()
                .filter(t -> t.getTableNumber() == tableNumber)
                .findFirst()
                .ifPresent(t -> {
                    if (!t.isOccupied()) {
                        t.occupyTable(new Customer("Guest", "000-0000", "guest@email.com"));
                    }
                });
    }

    public void freeTable(int tableNumber) {
        tables.stream()
                .filter(t -> t.getTableNumber() == tableNumber)
                .findFirst()
                .ifPresent(Table::freeTable);
    }

    public List<Table> getAvailableTables() {
        List<Table> available = new ArrayList<>();
        for (Table table : tables) {
            if (!table.isOccupied()) {
                available.add(table);
            }
        }
        return available;
    }

    public List<Table> getAllTables() {
        return new ArrayList<>(tables);
    }

    public Table getTableByNumber(int tableNumber) {
        return tables.stream()
                .filter(t -> t.getTableNumber() == tableNumber)
                .findFirst()
                .orElse(null);
    }
}