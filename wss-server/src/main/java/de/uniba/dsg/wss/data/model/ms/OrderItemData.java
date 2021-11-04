package de.uniba.dsg.wss.data.model.ms;

import java.time.LocalDateTime;

/**
 * Defines {@link ProductData product}, quantity, supplying {@link WarehouseData warehouse} and
 * other properties of an individual {@link OrderData order} item.
 *
 * @author Benedikt Full
 */
public class OrderItemData extends BaseData {

  private final OrderData orderRef;
  private final ProductData productRef;
  private final WarehouseData supplyingWarehouseRef;

  private final int number;
  private final LocalDateTime deliveryDate;
  private final int quantity;
  private final double amount;
  private final String distInfo;

  public OrderItemData(OrderData orderRef, ProductData productRef, WarehouseData supplyingWarehouseRef, int number, LocalDateTime deliveryDate, int quantity, double amount, String distInfo) {
    super();
    this.orderRef = orderRef;
    this.productRef = productRef;
    this.supplyingWarehouseRef = supplyingWarehouseRef;
    this.number = number;
    // TODO
    this.deliveryDate = deliveryDate;
    this.quantity = quantity;
    this.amount = amount;
    this.distInfo = distInfo;
  }

  // JPA conversion constructor
  public OrderItemData(String id, OrderData orderRef, ProductData productRef, WarehouseData supplyingWarehouseRef, int number, LocalDateTime deliveryDate, int quantity, double amount, String distInfo) {
    super(id);
    this.orderRef = orderRef;
    this.productRef = productRef;
    this.supplyingWarehouseRef = supplyingWarehouseRef;
    this.number = number;
    this.deliveryDate = deliveryDate;
    this.quantity = quantity;
    this.amount = amount;
    this.distInfo = distInfo;
  }

  public OrderData getOrderRef() {
    return orderRef;
  }

  public ProductData getProductRef() {
    return productRef;
  }

  public WarehouseData getSupplyingWarehouseRef() {
    return supplyingWarehouseRef;
  }

  public int getNumber() {
    return number;
  }

  public LocalDateTime getDeliveryDate() {
    return deliveryDate;
  }

  public int getQuantity() {
    return quantity;
  }

  public double getAmount() {
    return amount;
  }

  public String getDistInfo() {
    return distInfo;
  }

}
