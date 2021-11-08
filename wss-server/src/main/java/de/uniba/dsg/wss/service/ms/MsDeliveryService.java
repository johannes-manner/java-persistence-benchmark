package de.uniba.dsg.wss.service.ms;

import de.uniba.dsg.wss.data.access.ms.DataConsistencyManager;
import de.uniba.dsg.wss.data.model.ms.CarrierData;
import de.uniba.dsg.wss.data.model.ms.MsDataRoot;
import de.uniba.dsg.wss.data.model.ms.OrderData;
import de.uniba.dsg.wss.data.model.ms.WarehouseData;
import de.uniba.dsg.wss.data.transfer.messages.DeliveryRequest;
import de.uniba.dsg.wss.data.transfer.messages.DeliveryResponse;
import de.uniba.dsg.wss.service.DeliveryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@ConditionalOnProperty(name = "jpb.persistence.mode", havingValue = "ms")
public class MsDeliveryService extends DeliveryService {

  private final DataConsistencyManager consistencyManager;
  private final MsDataRoot dataRoot;

  @Autowired
  public MsDeliveryService(DataConsistencyManager consistencyManager, MsDataRoot dataRoot) {
    this.consistencyManager = consistencyManager;
    this.dataRoot = dataRoot;
  }

  @Override
  public DeliveryResponse process(DeliveryRequest req) {

    WarehouseData warehouse = this.dataRoot.getWarehouses().get(req.getWarehouseId());
    CarrierData carrier = this.dataRoot.getCarriers().get(req.getCarrierId());

    // Find an order for each district (the oldest unfulfilled order)
    List<OrderData> oldestOrderForEachDistrict = warehouse.getDistricts().entrySet().stream()
            .map(dEntry -> dEntry.getValue().getOrders().entrySet().stream()
                    .map(oEntry -> oEntry.getValue())
                    .filter(OrderData::isNotFulfilled)
                    .sorted()
                    .findFirst())
            .filter(Optional::isPresent)
            .map(o -> o.get())
            .collect(Collectors.toList());

    // update fulfillment status
    // update carrier information
    // for each order item, set delivery date to now and sum amount
    // Update customer balance and delivery count
    this.consistencyManager.deliverOldestOrders(oldestOrderForEachDistrict, carrier);
//          // Actually deliver the orders
//          for (OrderData order : orders) {
//            double amountSum = 0;
//
//            // For each order item, set delivery date to now and sum amount
//            List<OrderItemData> orderItems =
//                allOrderItems.stream()
//                    .filter(i -> i.getOrderId().equals(order.getId()))
//                    .peek(i -> i.setDeliveryDate(LocalDateTime.now()))
//                    .collect(Collectors.toList());
//            if (orderItems.isEmpty()) {
//              throw new IllegalStateException("Order has no items");
//            }
//            amountSum += orderItems.stream().mapToDouble(OrderItemData::getAmount).sum();
//
//            // Update customer balance and delivery count
//            CustomerData customer = customerStore.get(order.getCustomerId());
//            customer.setBalance(customer.getBalance() + amountSum);
//            customer.setDeliveryCount(customer.getDeliveryCount() + 1);
//            customerStore.update(customer.getId(), customer);
//          }
//          orderItemStore.update(allOrderItems, OrderItemData::getId);
//
    return new DeliveryResponse(req);
  }
}
