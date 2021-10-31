package de.uniba.dsg.wss.data.model.ms;

import java.time.LocalDateTime;

/**
 * A payment made by a {@link CustomerData customer}.
 *
 * @author Benedikt Full
 */
public class PaymentData extends BaseData{

  private String customerId;
  private String districtId;
  private LocalDateTime date;
  private double amount;
  private String data;

  public String getCustomerId() {
    return customerId;
  }

  public void setCustomerId(String customerId) {
    this.customerId = customerId;
  }

  public String getDistrictId() {
    return districtId;
  }

  public void setDistrictId(String districtId) {
    this.districtId = districtId;
  }

  public LocalDateTime getDate() {
    return date;
  }

  public void setDate(LocalDateTime date) {
    this.date = date;
  }

  public double getAmount() {
    return amount;
  }

  public void setAmount(double amount) {
    this.amount = amount;
  }

  public String getData() {
    return data;
  }

  public void setData(String data) {
    this.data = data;
  }

  @Override
  public PaymentData clone() {
    return (PaymentData) super.clone();
  }
}
