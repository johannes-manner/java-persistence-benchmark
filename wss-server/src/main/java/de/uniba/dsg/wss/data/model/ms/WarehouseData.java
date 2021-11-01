package de.uniba.dsg.wss.data.model.ms;

import com.google.common.util.concurrent.AtomicDouble;
import de.uniba.dsg.wss.data.model.ms.v2.StockData;

import java.util.ArrayList;
import java.util.List;

/**
 * A warehouse of the wholesale supplier.
 *
 * @author Benedikt Full
 */
public class WarehouseData extends BaseData{

  private final String name;
  private final AddressData address;
  private AtomicDouble salesTax;
  private AtomicDouble yearToDateBalance;

  // TODO check this
  private final List<DistrictData> districtRefs;
  private final List<StockData> stockRefs;

  public WarehouseData(String id, String name, AddressData address){
    super(id);
    this.name = name;
    this.address = address;
    salesTax = new AtomicDouble(0.0);
    yearToDateBalance = new AtomicDouble(0.0);
    districtRefs = new ArrayList<>();
    stockRefs = new ArrayList<>();
  }

  public String getName() {
    return name;
  }

  public AddressData getAddress() {
    return address;
  }

  public double getSalesTax() {
    return salesTax.doubleValue();
  }

  public boolean updateSalesTax(double oldSalesTax, double newSalesTax) {
    return this.salesTax.compareAndSet(oldSalesTax, newSalesTax);
  }

  public double getYearToDateBalance() {
    return yearToDateBalance.doubleValue();
  }

  public boolean updateYearToDateBalance(double oldYearToDateBalance,double newYearToDateBalance) {
    return this.yearToDateBalance.compareAndSet(oldYearToDateBalance, newYearToDateBalance);
  }

  public List<DistrictData> getDistricts() {
    return this.districtRefs;
  }

  public List<StockData> getStocks(){
    return this.stockRefs;
  }

  @Override
  public WarehouseData clone() {
    return (WarehouseData) super.clone();
  }
}
