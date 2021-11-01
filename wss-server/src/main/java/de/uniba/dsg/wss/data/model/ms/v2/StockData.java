package de.uniba.dsg.wss.data.model.ms.v2;

import de.uniba.dsg.wss.data.model.ms.BaseData;
import de.uniba.dsg.wss.data.model.ms.ProductData;
import de.uniba.dsg.wss.data.model.ms.WarehouseData;

/**
 * The available amount of a specific {@link ProductData product} at some {@link WarehouseData
 * warehouse}.
 *
 * @author Benedikt Full
 */
public class StockData extends BaseData {

  private final WarehouseData warehouseRef;
  private final ProductData productRef;

  private int quantity;
  private double yearToDateBalance;
  private int orderCount;
  private int remoteCount;
  private final String data;
  private final String dist01;
  private final String dist02;
  private final String dist03;
  private final String dist04;
  private final String dist05;
  private final String dist06;
  private final String dist07;
  private final String dist08;
  private final String dist09;
  private final String dist10;

  public StockData(String id, WarehouseData warehouseRef, ProductData productRef, int quantity, double yearToDateBalance, int orderCount, int remoteCount, String data, String dist01, String dist02, String dist03, String dist04, String dist05, String dist06, String dist07, String dist08, String dist09, String dist10) {
    super(id);
    this.warehouseRef = warehouseRef;
    this.productRef = productRef;
    this.quantity = quantity;
    this.yearToDateBalance = yearToDateBalance;
    this.orderCount = orderCount;
    this.remoteCount = remoteCount;
    this.data = data;
    this.dist01 = dist01;
    this.dist02 = dist02;
    this.dist03 = dist03;
    this.dist04 = dist04;
    this.dist05 = dist05;
    this.dist06 = dist06;
    this.dist07 = dist07;
    this.dist08 = dist08;
    this.dist09 = dist09;
    this.dist10 = dist10;
  }

  public WarehouseData getWarehouseRef() {
    return warehouseRef;
  }

  public ProductData getProductRef() {
    return productRef;
  }

  public int getQuantity() {
    return quantity;
  }

  public void setQuantity(int quantity) {
    this.quantity = quantity;
  }

  public double getYearToDateBalance() {
    return yearToDateBalance;
  }

  public void setYearToDateBalance(double yearToDateBalance) {
    this.yearToDateBalance = yearToDateBalance;
  }

  public int getOrderCount() {
    return orderCount;
  }

  public void setOrderCount(int orderCount) {
    this.orderCount = orderCount;
  }

  public int getRemoteCount() {
    return remoteCount;
  }

  public void setRemoteCount(int remoteCount) {
    this.remoteCount = remoteCount;
  }

  public String getData() {
    return data;
  }

  public String getDist01() {
    return dist01;
  }

  public String getDist02() {
    return dist02;
  }

  public String getDist03() {
    return dist03;
  }

  public String getDist04() {
    return dist04;
  }

  public String getDist05() {
    return dist05;
  }

  public String getDist06() {
    return dist06;
  }

  public String getDist07() {
    return dist07;
  }

  public String getDist08() {
    return dist08;
  }

  public String getDist09() {
    return dist09;
  }

  public String getDist10() {
    return dist10;
  }

  @Override
  public StockData clone() {
    return (StockData) super.clone();
  }
}
