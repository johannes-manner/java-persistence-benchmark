package de.uniba.dsg.wss.api.ms;

import de.uniba.dsg.wss.api.ResourcesController;
import de.uniba.dsg.wss.data.transfer.representations.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * This controller provides read-only access to many of the resources managed by the server when
 * launched in MS persistence mode.
 *
 * @author Benedikt Full
 */
@RestController
@ConditionalOnProperty(name = "jpb.persistence.mode", havingValue = "ms")
public class MsResourcesController implements ResourcesController {

//  private final ModelMapper modelMapper;


  public MsResourcesController(
//      JacisStore<String, CarrierData> carrierStore,
//      JacisStore<String, WarehouseData> warehouseStore,
//      JacisStore<String, DistrictData> districtStore,
//      JacisStore<String, StockData> stockStore,
//      JacisStore<String, CustomerData> customerStore,
//      JacisStore<String, OrderData> orderStore,
//      JacisStore<String, EmployeeData> employeeStore,
//      JacisStore<String, ProductData> productStore) {
//    this.carrierStore = carrierStore;
//    this.warehouseStore = warehouseStore;
//    this.districtStore = districtStore;
//    this.stockStore = stockStore;
//    this.customerStore = customerStore;
//    this.orderStore = orderStore;
//    this.employeeStore = employeeStore;
//    this.productStore = productStore;
  ) {
//    modelMapper = new ModelMapper();
  }

  @Override
  public Iterable<ProductRepresentation> getProducts() {

    return null;
//    return productStore
//        .streamReadOnly()
//        .parallel()
//        .map(p -> modelMapper.map(p, ProductRepresentation.class))
//        .collect(Collectors.toList());
  }

  @Override
  public ResponseEntity<EmployeeRepresentation> getEmployee(String username) {
    return null;
//    EmployeeData employee =
//        employeeStore.streamReadOnly(e -> e.getUsername().equals(username)).findAny().orElse(null);
//    if (employee == null) {
//      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
//    }
//    return ResponseEntity.ok(modelMapper.map(employee, EmployeeRepresentation.class));
  }

  @Override
  public List<WarehouseRepresentation> getWarehouses() {
    return null;
//    return warehouseStore
//        .streamReadOnly()
//        .map(w -> modelMapper.map(w, WarehouseRepresentation.class))
//        .collect(Collectors.toList());
  }

  @Override
  public ResponseEntity<List<DistrictRepresentation>> getWarehouseDistricts(String warehouseId) {
    return null;
//    if (!warehouseStore.containsKey(warehouseId)) {
//      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
//    }
//    List<DistrictRepresentation> districts =
//        districtStore
//            .streamReadOnly(d -> d.getWarehouseId().equals(warehouseId))
//            .parallel()
//            .map(d -> modelMapper.map(d, DistrictRepresentation.class))
//            .collect(Collectors.toList());
//    return ResponseEntity.ok(districts);
  }

  @Override
  public ResponseEntity<List<StockRepresentation>> getWarehouseStocks(String warehouseId) {
    return null;
//    if (!warehouseStore.containsKey(warehouseId)) {
//      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
//    }
//    List<StockRepresentation> stocks =
//        stockStore
//            .streamReadOnly(s -> s.getWarehouseId().equals(warehouseId))
//            .parallel()
//            .map(s -> modelMapper.map(s, StockRepresentation.class))
//            .collect(Collectors.toList());
//    return ResponseEntity.ok(stocks);
  }

  @Override
  public ResponseEntity<List<CustomerRepresentation>> getDistrictCustomers(
      String warehouseId, String districtId) {
    return null;
//    if (!warehouseStore.containsKey(warehouseId) || !districtStore.containsKey(districtId)) {
//      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
//    }
//
//    List<CustomerRepresentation> customers =
//        customerStore
//            .streamReadOnly(c -> c.getDistrictId().equals(districtId))
//            .parallel()
//            .map(c -> modelMapper.map(c, CustomerRepresentation.class))
//            .collect(Collectors.toList());
//    return ResponseEntity.ok(customers);
  }

  @Override
  public ResponseEntity<List<OrderRepresentation>> getDistrictOrders(
      String warehouseId, String districtId) {
    return null;
//    if (!warehouseStore.containsKey(warehouseId) || !districtStore.containsKey(districtId)) {
//      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
//    }
//
//    List<OrderRepresentation> orders =
//        orderStore
//            .streamReadOnly(o -> o.getDistrictId().equals(districtId))
//            .parallel()
//            .map(o -> modelMapper.map(o, OrderRepresentation.class))
//            .collect(Collectors.toList());
//    return ResponseEntity.ok(orders);
  }

  @Override
  public List<CarrierRepresentation> getCarriers() {
    return null;
//    return carrierStore
//        .streamReadOnly()
//        .map(c -> modelMapper.map(c, CarrierRepresentation.class))
//        .collect(Collectors.toList());
//  }
  }
}
