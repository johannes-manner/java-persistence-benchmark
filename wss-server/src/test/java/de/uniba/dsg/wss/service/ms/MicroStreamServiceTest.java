package de.uniba.dsg.wss.service.ms;

import de.uniba.dsg.wss.data.model.ms.*;
import one.microstream.storage.embedded.types.EmbeddedStorageManager;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ExtendWith(SpringExtension.class)
@Import(MicroStreamTestConfiguration.class)
public abstract class MicroStreamServiceTest {

  protected static final int WAREHOUSES = 5;
  protected static final int PRODUCTS = 10;
  protected static final int DISTRICTS = 2*WAREHOUSES;
  protected static final int CUSTOMERS = 2*DISTRICTS;
  @Autowired
  protected EmbeddedStorageManager storageManager;
  @Autowired
  protected MsDataRoot msDataRoot;


  public MicroStreamServiceTest() {
  }

  public void prepareTestStorage() {

    Map<String, WarehouseData> warehouses = new HashMap<>();
    for(int i = 0; i < WAREHOUSES; i++) {
      String id = "W" + i;
      warehouses.put(id, new WarehouseData(id, id, new AddressData("", "" , "","","")));
    }

    Map<String, ProductData> products = new HashMap<>();
    for(int i = 0; i < PRODUCTS; i++) {
      String id = "P" + i;
      products.put(id, new ProductData(id, "", id, 12.99 + i, id + "-data"));
    }

    Map<String, StockData> stocks = new HashMap<>();
    for(int i = 0 ; i < WAREHOUSES ; i++) {
      for(int j = 0 ; j < PRODUCTS ; j++) {
        if((i+j)%2 == 0){
          String stockId = "W" + i + "P" + j;
          WarehouseData warehouseData = warehouses.get("W" + i);
          ProductData productData = products.get("P" + j);
          stocks.put(warehouseData.getId()+productData.getId(), new StockData(stockId,
                  warehouseData,
                  productData,
                  i+j,
                  0,
                  0,
                  0,
                  stockId,
                  stockId + "-1",
                  stockId + "-2",
                  stockId + "-3",
                  stockId + "-4",
                  stockId + "-5",
                  stockId + "-6",
                  stockId + "-7",
                  stockId + "-8",
                  stockId + "-9",
                  stockId + "-10"));
        }
      }
    }

    Map<String, DistrictData> districts = new HashMap<>();
    for(int i = 0; i < DISTRICTS; i++) {
      String districtId = "D" + i;
      WarehouseData warehouseData = warehouses.get("W" + (i%WAREHOUSES));
      DistrictData districtData = new DistrictData(districtId,
              warehouseData,
              districtId,
              new AddressData("", "" , "","",""),
              1.19,
              0);
      warehouseData.getDistricts().put(districtId, districtData);
      districts.put(districtId,districtData);
    }

    Map<String, CustomerData> customers = new HashMap<>();
    for (int i = 0 ; i < CUSTOMERS; i++){
      String customerId = "C" + i;
      DistrictData districtData = districts.get("D" + (i%DISTRICTS));
      CustomerData customerData = new CustomerData(customerId,
              customerId+"-first",
              customerId+"-middle",
              customerId+"-last",
              new AddressData("", "" , "","",""),
              customerId+"-phone",
              customerId+"-mail",
              districtData,
              LocalDateTime.now(),
              "",
              0,
              0.1,
              13.45 + i,
              0,
              0,
              0,
              customerId+"-data"
              );

      customers.put(customerId, customerData);
      districtData.getCustomers().add(customerData);
    }

    // remove all data from data root - cannot instantiate another root object since then I get null pointers in the logic classes
    // bean has to be the same object
    this.msDataRoot.getOrders().clear();
    this.msDataRoot.getEmployees().clear();
    this.msDataRoot.getWarehouses().clear();
    this.msDataRoot.getStocks().clear();
    this.msDataRoot.getCustomers().clear();

    // add all data to data root :)
    this.msDataRoot.getWarehouses().putAll(warehouses);
    this.msDataRoot.getStocks().putAll(stocks);
    this.msDataRoot.getCustomers().putAll(customers);

    this.storageManager.setRoot(msDataRoot);
    this.storageManager.storeRoot();
  }
}
