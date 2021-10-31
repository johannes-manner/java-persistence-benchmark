package de.uniba.dsg.wss.data.model.ms;

/**
 * A warehouse of the wholesale supplier.
 *
 * @author Benedikt Full
 */
public class WarehouseData extends BaseData{

  private String name;
  private AddressData address;
  private double salesTax;
  private double yearToDateBalance;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public AddressData getAddress() {
    return address;
  }

  public void setAddress(AddressData address) {
    this.address = address;
  }

  public double getSalesTax() {
    return salesTax;
  }

  public void setSalesTax(double salesTax) {
    this.salesTax = salesTax;
  }

  public double getYearToDateBalance() {
    return yearToDateBalance;
  }

  public void setYearToDateBalance(double yearToDateBalance) {
    this.yearToDateBalance = yearToDateBalance;
  }

  @Override
  public WarehouseData clone() {
    return (WarehouseData) super.clone();
  }
}
