package de.uniba.dsg.wss.data.model.ms;

import java.time.LocalDateTime;

/**
 * Defines {@link ProductData product}, quantity, supplying {@link WarehouseData warehouse} and
 * other properties of an individual {@link OrderData order} item.
 *
 * @author Benedikt Full
 */
public class OrderItemData extends BaseData {

  private String orderId;
  private int number;
  private String productId;
  private String supplyingWarehouseId;
  private LocalDateTime deliveryDate;
  private int quantity;
  private double amount;
  private String distInfo;

  public String getOrderId() {
    return orderId;
  }

  public void setOrderId(String orderId) {
    this.orderId = orderId;
  }

  public int getNumber() {
    return number;
  }

  public void setNumber(int number) {
    this.number = number;
  }

  public String getProductId() {
    return productId;
  }

  public void setProductId(String productId) {
    this.productId = productId;
  }

  public String getSupplyingWarehouseId() {
    return supplyingWarehouseId;
  }

  public void setSupplyingWarehouseId(String supplyingWarehouseId) {
    this.supplyingWarehouseId = supplyingWarehouseId;
  }

  public LocalDateTime getDeliveryDate() {
    return deliveryDate;
  }

  public void setDeliveryDate(LocalDateTime deliveryDate) {
    this.deliveryDate = deliveryDate;
  }

  public int getQuantity() {
    return quantity;
  }

  public void setQuantity(int quantity) {
    this.quantity = quantity;
  }

  public double getAmount() {
    return amount;
  }

  public void setAmount(double amount) {
    this.amount = amount;
  }

  public String getDistInfo() {
    return distInfo;
  }

  public void setDistInfo(String distInfo) {
    this.distInfo = distInfo;
  }

  @Override
  public OrderItemData clone() {
    return (OrderItemData) super.clone();
  }
}
