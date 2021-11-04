package de.uniba.dsg.wss.data.model.ms;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MsDataRoot {

    private final Map<String, WarehouseData> warehouses;
    private final Map<String, EmployeeData> employees;
    private final Map<String, CustomerData> customers;
    // map contains a compound key: warehouseId+productId
    private final Map<String, StockData> stocks;
    private final Map<String, OrderData> orders;

    public MsDataRoot(){
        warehouses = new ConcurrentHashMap<>();
        employees = new ConcurrentHashMap<>();
        customers = new ConcurrentHashMap<>();
        stocks = new ConcurrentHashMap<>();
        orders = new ConcurrentHashMap<>();
    }

    public Map<String, WarehouseData> getWarehouses() {
        return warehouses;
    }

    public Map<String, EmployeeData> getEmployees(){
        return employees;
    }

    public Map<String, CustomerData> getCustomers() {
        return customers;
    }

    public Map<String, StockData> getStocks() {
        return stocks;
    }

    public Map<String, OrderData> getOrders() {
        return orders;
    }
}
