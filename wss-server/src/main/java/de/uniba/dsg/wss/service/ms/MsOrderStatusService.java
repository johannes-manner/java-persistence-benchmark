package de.uniba.dsg.wss.service.ms;

import de.uniba.dsg.wss.data.model.ms.CustomerData;
import de.uniba.dsg.wss.data.model.ms.MsDataRoot;
import de.uniba.dsg.wss.data.model.ms.OrderData;
import de.uniba.dsg.wss.data.model.ms.OrderItemData;
import de.uniba.dsg.wss.data.transfer.messages.OrderItemStatusResponse;
import de.uniba.dsg.wss.data.transfer.messages.OrderStatusRequest;
import de.uniba.dsg.wss.data.transfer.messages.OrderStatusResponse;
import de.uniba.dsg.wss.service.OrderStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@ConditionalOnProperty(name = "jpb.persistence.mode", havingValue = "ms")
public class MsOrderStatusService extends OrderStatusService {

  private final MsDataRoot dataRoot;

  @Autowired
  public MsOrderStatusService(MsDataRoot dataRoot){
    this.dataRoot = dataRoot;
  }

  @Override
  public OrderStatusResponse process(OrderStatusRequest req) {

    CustomerData customer;
    if(req.getCustomerId() == null) {
      customer = dataRoot.getCustomers().entrySet().stream()
              .parallel()
              .filter(c -> c.getValue().getEmail().equals(req.getCustomerEmail()))
              .findAny()
              .orElseThrow(() -> new IllegalStateException("Failed to find customer with email " + req.getCustomerEmail()))
              .getValue();
    } else {
      customer = dataRoot.getCustomers().get(req.getCustomerId());
      if(customer == null) {
        throw new IllegalStateException("Failed to find customer with email " + req.getCustomerEmail());
      }
    }

    OrderData mostRecentOrder = customer.getOrderRefs().entrySet().stream()
            .map(o -> o.getValue())
            .max(Comparator.comparing(OrderData::getEntryDate))
            .orElseThrow(IllegalStateException::new);

    synchronized (mostRecentOrder.getId()) {
      return toOrderStatusResponse(req, mostRecentOrder, customer, toOrderItemStatusResponse(mostRecentOrder.getItems()));
    }
  }

  private static List<OrderItemStatusResponse> toOrderItemStatusResponse(List<OrderItemData> orderItems) {
    List<OrderItemStatusResponse> responses = new ArrayList<>(orderItems.size());
    for (OrderItemData item : orderItems) {
      OrderItemStatusResponse res = new OrderItemStatusResponse();
      res.setSupplyingWarehouseId(item.getSupplyingWarehouseRef().getId());
      res.setProductId(item.getProductRef().getId());
      res.setQuantity(item.getQuantity());
      res.setAmount(item.getAmount());
      res.setDeliveryDate(item.getDeliveryDate());
      responses.add(res);
    }
    return responses;
  }

  private static OrderStatusResponse toOrderStatusResponse(
      OrderStatusRequest req,
      OrderData order,
      CustomerData customer,
      List<OrderItemStatusResponse> itemStatusResponses) {
    OrderStatusResponse res = new OrderStatusResponse();
    res.setWarehouseId(req.getWarehouseId());
    res.setDistrictId(req.getDistrictId());
    res.setCustomerId(customer.getId());
    res.setCustomerFirstName(customer.getFirstName());
    res.setCustomerMiddleName(customer.getMiddleName());
    res.setCustomerLastName(customer.getLastName());
    res.setCustomerBalance(customer.getBalance());
    res.setOrderId(order.getId());
    res.setOrderEntryDate(order.getEntryDate());
    res.setOrderCarrierId(order.getCarrierRef() == null ? null : order.getCarrierRef().getId());
    res.setItemStatus(itemStatusResponses);
    return res;
  }
}
