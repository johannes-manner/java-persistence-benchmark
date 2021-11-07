package de.uniba.dsg.wss.data.access.ms;

import de.uniba.dsg.wss.data.model.ms.MsDataRoot;
import de.uniba.dsg.wss.data.model.ms.OrderData;
import de.uniba.dsg.wss.data.model.ms.OrderItemData;
import de.uniba.dsg.wss.service.ms.MsTransactionException;
import de.uniba.dsg.wss.service.ms.StockUpdateDTO;
import one.microstream.storage.types.StorageManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

// Singleton by default
@Component
@ConditionalOnProperty(name = "jpb.persistence.mode", havingValue = "ms")
public class DataConsistencyManager {

  private static final Logger LOG = LogManager.getLogger(DataConsistencyManager.class);

  // low level synchronization here - one lock for updating the overall order
  private final Object stockLock = new Object();

  private final StorageManager storageManger;
  private final MsDataRoot dataRoot;

  @Autowired
  public DataConsistencyManager(StorageManager storageManager, MsDataRoot dataRoot) {
    this.storageManger = storageManager;
    this.dataRoot = dataRoot;
  }

  private List<OrderItemData> updateStock(OrderData order, List<StockUpdateDTO> stockUpdates) {
    synchronized (stockLock) {
      List<OrderItemData> orderItemList = new ArrayList<>();
      int i = 0;
      for(i = 0 ; i < stockUpdates.size() ; i++) {
        // update all the items, if an update fails, compensate the changes
        StockUpdateDTO stockUpdate = stockUpdates.get(i);
        if(stockUpdate.getStockData().reduceQuantity(stockUpdate.getQuantity()) == false){
          LOG.info("Out of stock " + stockUpdate.getStockData().getId());
          break;
        } else {
          OrderItemData orderItem = new OrderItemData(order,
                  stockUpdate.getStockData().getProductRef(),
                  stockUpdate.getStockData().getWarehouseRef(),
                  i,
                  null,
                  stockUpdate.getQuantity(),
                  stockUpdate.getStockData().getQuantity(),
                  stockUpdate.getQuantity() * stockUpdate.getStockData().getProductRef().getPrice(),
                  // TODO random dist info
                  stockUpdate.getStockData().getDist01());
          orderItemList.add(orderItem);
//          LOG.info("Stock operation successful for stock entry " + stockUpdate.getStockData().getId());
        }
      }

      // compensate the first transactions, if some of the updates fail
      if(i != stockUpdates.size()){
        for(int j = 0 ; j < i ; j++){
          StockUpdateDTO stockUpdate = stockUpdates.get(j);
          stockUpdate.getStockData().undoReduceQuantityOperation(stockUpdate.getQuantity());
          LOG.info("Undo stock operation for stock entry " + stockUpdate.getStockData().getId());
        }
        return List.of();
      }
      return orderItemList;
    }
  }

  public OrderData storeOrder(OrderData order, List<StockUpdateDTO> stockUpdates) throws MsTransactionException{

    synchronized (this.storageManger){
      List<OrderItemData> itemList = this.updateStock(order, stockUpdates);
      if(itemList.isEmpty()){
        throw new MsTransactionException("Order Item Update failed");
      }
      order.getItems().addAll(itemList);

      this.dataRoot.getOrders().put(order.getId(), order);

      // referential integrity (customer and district)
      order.getDistrictRef().getOrders().put(order.getId(), order);
      order.getCustomerRef().getOrderRefs().put(order.getId(), order);

      // A single store is faster as making a store for each object separately
      this.storageManger.storeRoot();
      return order;
    }
  }

  public void storeRoot(){
    synchronized (this.storageManger){
      this.storageManger.storeRoot();
    }
  }
}
