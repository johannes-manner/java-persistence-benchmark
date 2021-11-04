package de.uniba.dsg.wss.data.transfer.messages;

public class NewOrderResponseItem {

  private final String supplyingWarehouseId;
  private final String itemId;
  private final String itemName;
  private final double itemPrice;
  private final double amount;
  private final int quantity;
  private final int stockQuantity;
  private final String brandGeneric;

  public NewOrderResponseItem(String supplyingWarehouseId, String itemId, String itemName, double itemPrice, double amount, int quantity, int stockQuantity, String brandGeneric) {
    this.supplyingWarehouseId = supplyingWarehouseId;
    this.itemId = itemId;
    this.itemName = itemName;
    this.itemPrice = itemPrice;
    this.amount = amount;
    this.quantity = quantity;
    this.stockQuantity = stockQuantity;
    this.brandGeneric = brandGeneric;
  }
}
