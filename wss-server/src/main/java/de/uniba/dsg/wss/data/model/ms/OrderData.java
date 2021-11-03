package de.uniba.dsg.wss.data.model.ms;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * An order issued by a {@link CustomerData customer} for a certain amount of {@link ProductData
 * products}.
 *
 * @see OrderItemData
 * @author Benedikt Full
 */
public class OrderData extends BaseData{

  private final DistrictData districtRef;
  private final CustomerData customerRef;
  private final CarrierData carrierRef;

  private final LocalDateTime entryDate;
  private final int itemCount;
  private final boolean allLocal;
  private final boolean fulfilled;

  private final List<OrderItemData> items;

  public OrderData(String id, DistrictData districtRef, CustomerData customerRef, CarrierData carrierRef, LocalDateTime entryDate, int itemCount, boolean allLocal, boolean fulfilled) {
    super(id);
    this.districtRef = districtRef;
    this.customerRef = customerRef;
    this.carrierRef = carrierRef;
    this.entryDate = entryDate;
    this.itemCount = itemCount;
    this.allLocal = allLocal;
    this.fulfilled = fulfilled;
    this.items = new ArrayList<>();
  }

  public DistrictData getDistrictRef() {
    return districtRef;
  }

  public CustomerData getCustomerRef() {
    return customerRef;
  }

  public CarrierData getCarrierRef() {
    return carrierRef;
  }

  public LocalDateTime getEntryDate() {
    return entryDate;
  }

  public int getItemCount() {
    return itemCount;
  }

  public boolean isAllLocal() {
    return allLocal;
  }

  public boolean isFulfilled() {
    return fulfilled;
  }

  public List<OrderItemData> getItems() {
    return items;
  }
}
