package de.uniba.dsg.wss.service.ms;

import com.google.common.util.concurrent.Uninterruptibles;
import de.uniba.dsg.wss.data.access.ms.DataConsistencyManager;
import de.uniba.dsg.wss.data.model.ms.*;
import de.uniba.dsg.wss.data.transfer.messages.NewOrderRequest;
import de.uniba.dsg.wss.data.transfer.messages.NewOrderRequestItem;
import de.uniba.dsg.wss.data.transfer.messages.NewOrderResponse;
import de.uniba.dsg.wss.data.transfer.messages.NewOrderResponseItem;
import de.uniba.dsg.wss.service.NewOrderService;
import one.microstream.storage.embedded.types.EmbeddedStorageManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import javax.transaction.TransactionalException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@ConditionalOnProperty(name = "jpb.persistence.mode", havingValue = "ms")
public class MsNewOrderService extends NewOrderService {

  private static final Logger LOG = LogManager.getLogger(MsNewOrderService.class);

  private static final int MAX_RETRIES = 5;
  private static final long RETRY_TIME = 100;
  private final DataConsistencyManager consistencyManager;
  private final EmbeddedStorageManager storageManager;
  private final MsDataRoot dataRoot;

  @Autowired
  public MsNewOrderService(DataConsistencyManager consistencyManager, EmbeddedStorageManager storageManager, MsDataRoot dataRoot){
    this.consistencyManager = consistencyManager;
    this.storageManager = storageManager;
    this.dataRoot = dataRoot;
  }

  @Override
  public NewOrderResponse process(NewOrderRequest req) {

    // get basic data for transaction
    WarehouseData warehouseData = dataRoot.getWarehouses().get(req.getWarehouseId());
    DistrictData districtData = warehouseData.getDistricts().get(req.getDistrictId());
    CustomerData customerData = dataRoot.getCustomers().get(req.getCustomerId());

    if(warehouseData == null || districtData == null || customerData == null) {
      throw new IllegalArgumentException();
    }

    // Get all supplying warehouses and products to ensure no invalid ids have been provided
    // optimization: checking if stock is available for warehouse and product
    List<StockUpdateDTO> stockUpdates = new ArrayList<>();
    for(NewOrderRequestItem item : req.getItems()) {
      StockData stock = dataRoot.getStocks().get(item.getSupplyingWarehouseId()+item.getProductId());
      if(stock == null) {
        throw new IllegalArgumentException();
      }
      StockUpdateDTO stockUpdate = new StockUpdateDTO(stock, item.getQuantity());
      stockUpdates.add(stockUpdate);
    }

    // Implementing retry mechanism (TODO refactor in an own class?)
    boolean updateSuccessful = false;
    for(int i = 0 ; i < MAX_RETRIES; i++) {
      // Update stock entries (CriticalSection!)
      updateSuccessful = this.consistencyManager.updateStock(stockUpdates);
      if(updateSuccessful){
        break;
      } else {
        Uninterruptibles.sleepUninterruptibly(RETRY_TIME, TimeUnit.MILLISECONDS);
      }
    }

    if(updateSuccessful == false){
      LOG.info("Cancel order processing - retries exceeded");
      // TODO make this better
      throw new TransactionalException("Can't process order", null);
    }

    // create order items



      return null;
//    TransactionManager transactionManager = new TransactionManager(container, 5, 100);
//    return transactionManager.commit(
//        () -> {
//
//
//            OrderItemData orderItem = new OrderItemData();
//            orderItem.setOrderId(order.getId());
//            orderItem.setNumber(i + 1);
//            orderItem.setProductId(reqItem.getProductId());
//            orderItem.setSupplyingWarehouseId(reqItem.getSupplyingWarehouseId());
//            orderItem.setDeliveryDate(null);
//            orderItem.setQuantity(reqItem.getQuantity());
//            orderItem.setAmount(orderItemProducts.get(i).getPrice() * reqItem.getQuantity());
//            orderItem.setDistInfo(getRandomDistrictInfo(stock));
//
//            orderItemStore.update(orderItem.getId(), orderItem);
//
//            NewOrderResponseItem responseLine = newOrderResponseLine(orderItem);
//            responseLines.add(responseLine);
//            responseLine.setStockQuantity(stock.getQuantity());
//            responseLine.setItemName(product.getName());
//            responseLine.setItemPrice(product.getPrice());
//            responseLine.setAmount(product.getPrice() * orderItemQuantity);
//            responseLine.setBrandGeneric(determineBrandGeneric(product.getData(), stock.getData()));
//
//            orderItemSum += orderItem.getAmount();
//          }
//
//          // Prepare the response object
//          NewOrderResponse res =
//              newOrderResponse(
//                  req,
//                  order.getId(),
//                  order.getEntryDate(),
//                  warehouse.getSalesTax(),
//                  district.getSalesTax(),
//                  customer.getCredit(),
//                  customer.getDiscount(),
//                  customer.getLastName());
//          res.setOrderId(order.getId());
//          res.setOrderTimestamp(order.getEntryDate());
//          res.setTotalAmount(
//              calcOrderTotal(
//                  orderItemSum,
//                  customer.getDiscount(),
//                  warehouse.getSalesTax(),
//                  district.getSalesTax()));
//          res.setOrderItems(responseLines);
//
//          return res;
//        });
  }

  private String getRandomDistrictInfo(StockData stock) {
    return randomDistrictData(
        List.of(
            stock.getDist01(),
            stock.getDist02(),
            stock.getDist03(),
            stock.getDist04(),
            stock.getDist05(),
            stock.getDist06(),
            stock.getDist07(),
            stock.getDist08(),
            stock.getDist09(),
            stock.getDist10()));
  }

  private static NewOrderResponseItem newOrderResponseLine(OrderItemData item) {
    NewOrderResponseItem requestLine = new NewOrderResponseItem();
//    requestLine.setSupplyingWarehouseId(item.getSupplyingWarehouseId());
//    requestLine.setItemId(item.getProductId());
//    requestLine.setItemPrice(0);
//    requestLine.setAmount(item.getAmount());
//    requestLine.setQuantity(item.getQuantity());
//    requestLine.setStockQuantity(0);
//    requestLine.setBrandGeneric(null);
    return requestLine;
  }
}
