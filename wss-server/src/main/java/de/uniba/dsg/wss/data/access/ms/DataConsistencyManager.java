package de.uniba.dsg.wss.data.access.ms;

import de.uniba.dsg.wss.service.ms.StockUpdateDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Helper for running JACIS transactions. While the {@link} provides various
 * convenience methods for running transactions, some necessary in the context of this application
 * are missing. For example, the container has no method which allows a transaction with a return
 * value being retried for a certain amount of times.
 *
 * <p>An instance of this class can be used to execute exactly one transaction. Using any of the
 * {@code commit(...)} methods will result in the manager from changing its internal state to closed
 * before returning, resulting in an {@link IllegalStateException} being thrown upon any further
 * calls to any state-altering methods of the instance.
 *
 * @author Benedikt Full
 */
// Singleton by default
@Component
@ConditionalOnProperty(name = "jpb.persistence.mode", havingValue = "ms")
public class DataConsistencyManager {

  private static final Logger LOG = LogManager.getLogger(DataConsistencyManager.class);

  // low level synchronization here - one lock for updating the overall order
  private final Object stockLock = new Object();


  // TODO document and remove logs when tested the stuff
  public boolean updateStock(List<StockUpdateDTO> stockUpdates) {
    synchronized (stockLock) {

      int i = 0;
      for(i = 0 ; i < stockUpdates.size() ; i++) {
        // update all the items, if an update fails, compensate the changes
        StockUpdateDTO stockUpdate = stockUpdates.get(i);
        if(stockUpdate.getStockData().reduceQuantity(stockUpdate.getQuantity()) == false){
          LOG.info("Out of stock " + stockUpdate.getStockData().getId());
          break;
        } else {
          LOG.info("Stock operation sucessfull for stock entry " + stockUpdate.getStockData().getId());
        }
      }

      // compensate the first transactions, if some of the updates fail
      if(i != stockUpdates.size()){
        for(int j = 0 ; j < i ; j++){
          StockUpdateDTO stockUpdate = stockUpdates.get(j);
          stockUpdate.getStockData().undoReduceQuantityOperation(stockUpdate.getQuantity());
          LOG.info("Undo stock operation for stock entry " + stockUpdate.getStockData().getId());
        }
        return false;
      }
      return true;
    }
  }
}
