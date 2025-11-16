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
        for (Table table : tables) {
            if (table.getTableNumber() == tableNumber) {
                return !table.isOccupied();
            }
        }
        return false;
    }

    public void occupyTable(int tableNumber, Customer customer) {
        for (Table table : tables) {
            if (table.getTableNumber() == tableNumber) {
                if (!table.isOccupied()) {
                    table.occupyTable(customer);
                }
                break;
            }
        }
    }

    public void freeTable(int tableNumber) {
        for (Table table : tables) {
            if (table.getTableNumber() == tableNumber) {
                table.freeTable();
                break;
            }
        }
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
        for (Table table : tables) {
            if (table.getTableNumber() == tableNumber) {
                return table;
            }
        }
        return null;
    }
}