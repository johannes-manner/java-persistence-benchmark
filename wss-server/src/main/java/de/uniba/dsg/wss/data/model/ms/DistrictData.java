package de.uniba.dsg.wss.data.model.ms;

import java.util.ArrayList;
import java.util.List;

/**
 * A district is one of ten areas supplied by a specific {@link WarehouseData warehouse}. Each
 * district is administered by a single {@link EmployeeData employee} and has 3000 {@link
 * CustomerData customers}.
 *
 * @author Benedikt Full
 */
public class DistrictData extends BaseData {

  private final WarehouseData warehouseRef;

  private final String name;
  private final AddressData address;
  private final double salesTax;
  private final double yearToDateBalance;

  private final List<CustomerData> customerRefs;
  private final List<OrderData> orderRefs;

  public DistrictData(String id,
                      WarehouseData warehouse,
                      String name,
                      AddressData address,
                      double salesTax,
                      double yearToDateBalance) {
    super(id);
    this.warehouseRef = warehouse;
    this.name = name;
    this.address = address;
    this.salesTax = salesTax;
    this.yearToDateBalance = yearToDateBalance;
    this.customerRefs = new ArrayList<>();
    this.orderRefs = new ArrayList<>();
  }

  public WarehouseData getWarehouse() {
    return warehouseRef;
  }

  public String getName() {
    return name;
  }

  public AddressData getAddress() {
    return address;
  }

  public double getSalesTax() {
    return salesTax;
  }

  public double getYearToDateBalance() {
    return yearToDateBalance;
  }

  public List<CustomerData> getCustomers(){
    return this.customerRefs;
  }

  public List<OrderData> getOrders(){
    return this.orderRefs;
  }

}
