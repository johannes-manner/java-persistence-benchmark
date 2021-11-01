package de.uniba.dsg.wss.data.model.ms;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * A customer of the wholesale supplier.
 *
 * @author Benedikt Full
 */
public class CustomerData extends PersonData {

  private final DistrictData districtRef;
  private final List<OrderData> orderRefs;
  private final List<PaymentData> paymentRefs;

  private final LocalDateTime since;
  private final String credit;
  private final double creditLimit;
  private final double discount;
  private final double balance;
  private final double yearToDatePayment;
  private final int paymentCount;
  private final int deliveryCount;
  private final String data;

  public CustomerData(String id,
                      String firstName,
                      String middleName,
                      String lastName,
                      AddressData addressData,
                      String phoneNumer,
                      String mail,
                      DistrictData districtRef,
                      LocalDateTime since,
                      String credit,
                      double creditLimit,
                      double discount,
                      double balance,
                      double yearToDatePayment,
                      int paymentCount,
                      int deliveryCount,
                      String data) {
    super(id,firstName,middleName,lastName,addressData,phoneNumer,mail);
    this.districtRef = districtRef;
    this.since = since;
    this.credit = credit;
    this.creditLimit = creditLimit;
    this.discount = discount;
    this.balance = balance;
    this.yearToDatePayment = yearToDatePayment;
    this.paymentCount = paymentCount;
    this.deliveryCount = deliveryCount;
    this.data = data;
    this.orderRefs = new ArrayList<>();
    this.paymentRefs = new ArrayList<>();
  }

  public DistrictData getDistrict() {
    return this.districtRef;
  }

  public LocalDateTime getSince() {
    return since;
  }

  public String getCredit() {
    return credit;
  }

  public double getCreditLimit() {
    return creditLimit;
  }

  public double getDiscount() {
    return discount;
  }

  public double getBalance() {
    return balance;
  }

  public double getYearToDatePayment() {
    return yearToDatePayment;
  }

  public int getPaymentCount() {
    return paymentCount;
  }

  public int getDeliveryCount() {
    return deliveryCount;
  }

  public String getData() {
    return data;
  }

  public List<OrderData> getOrderRefs(){
    return this.orderRefs;
  }

  public List<PaymentData> getPaymentRefs(){
    return this.paymentRefs;
  }

  @Override
  public CustomerData clone() {
    return (CustomerData) super.clone();
  }
}
