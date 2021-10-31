package de.uniba.dsg.wss.data.model.ms;

import java.time.LocalDateTime;

/**
 * An order issued by a {@link CustomerData customer} for a certain amount of {@link ProductData
 * products}.
 *
 * @see OrderItemData
 * @author Benedikt Full
 */
public class OrderData extends BaseData{

  private String districtId;
  private String customerId;
  private String carrierId;
  private LocalDateTime entryDate;
  private int itemCount;
  private boolean allLocal;
  private boolean fulfilled;

  public String getDistrictId() {
    return districtId;
  }

  public void setDistrictId(String districtId) {
    this.districtId = districtId;
  }

  public String getCustomerId() {
    return customerId;
  }

  public void setCustomerId(String customerId) {
    this.customerId = customerId;
  }

  public String getCarrierId() {
    return carrierId;
  }

  public void setCarrierId(String carrierId) {
    this.carrierId = carrierId;
  }

  public LocalDateTime getEntryDate() {
    return entryDate;
  }

  public void setEntryDate(LocalDateTime entryDate) {
    this.entryDate = entryDate;
  }


  public int getItemCount() {
    return itemCount;
  }

  public void setItemCount(int itemCount) {
    this.itemCount = itemCount;
  }

  public boolean isAllLocal() {
    return allLocal;
  }

  public void setAllLocal(boolean allLocal) {
    this.allLocal = allLocal;
  }

  public boolean isFulfilled() {
    return fulfilled;
  }

  public void setFulfilled(boolean fulfilled) {
    this.fulfilled = fulfilled;
  }

  @Override
  public OrderData clone() {
    return (OrderData) super.clone();
  }
}
