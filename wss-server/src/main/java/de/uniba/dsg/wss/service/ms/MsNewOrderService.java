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
import java.time.LocalDateTime;
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

    // create order
    OrderData order = new OrderData(districtData,
            customerData,
            LocalDateTime.now(),
            stockUpdates.size()
            );

    // create order items
    List<OrderItemData> orderItems = new ArrayList<>();
    int i=0;
    // create return dtos
    double orderItemSum = 0;
    List<NewOrderResponseItem> dtoItems = new ArrayList<>();

    for(StockUpdateDTO stockUpdateDTO : stockUpdates) {
      i++;
      OrderItemData orderItem = new OrderItemData(order,
              stockUpdateDTO.getStockData().getProductRef(),
              stockUpdateDTO.getStockData().getWarehouseRef(),
              i,
              null,
              stockUpdateDTO.getQuantity(),
              stockUpdateDTO.getQuantity() * stockUpdateDTO.getStockData().getProductRef().getPrice(),
              getRandomDistrictInfo(stockUpdateDTO.getStockData()));

      // add to business object
      orderItems.add(orderItem);

      // add to response object
      dtoItems.add(new NewOrderResponseItem(orderItem.getSupplyingWarehouseRef().getId(),
              orderItem.getProductRef().getId(),
              orderItem.getProductRef().getName(),
              orderItem.getProductRef().getPrice(),
              orderItem.getAmount(),
              orderItem.getQuantity(),
              // TODO ask why
              0,
              determineBrandGeneric(orderItem.getProductRef().getData(), stockUpdateDTO.getStockData().getData())));
      orderItemSum += orderItem.getAmount();
    }

    // referential integrity
    // until now - all the state is stack state of the method
    order.getItems().addAll(orderItems);

    // add the order to the object graph - concurrent hash map :)
    this.dataRoot.getOrders().put(order.getId(), order);


    // prepare response object
    NewOrderResponse res = newOrderResponse(req,
            order.getId(),
            order.getEntryDate(),
            warehouseData.getSalesTax(),
            districtData.getSalesTax(),
            customerData.getCredit(),
            customerData.getDiscount(),
            customerData.getLastName());
    res.setTotalAmount(calcOrderTotal(orderItemSum, customerData.getDiscount(), warehouseData.getSalesTax(), districtData.getSalesTax()));
    res.setOrderItems(dtoItems);
    return res;
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
}
