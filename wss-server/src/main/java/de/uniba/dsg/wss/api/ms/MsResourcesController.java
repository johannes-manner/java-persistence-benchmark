package de.uniba.dsg.wss.api.ms;

import de.uniba.dsg.wss.api.ResourcesController;
import de.uniba.dsg.wss.data.model.ms.AddressData;
import de.uniba.dsg.wss.data.model.ms.DistrictData;
import de.uniba.dsg.wss.data.model.ms.MsDataRoot;
import de.uniba.dsg.wss.data.model.ms.WarehouseData;
import de.uniba.dsg.wss.data.model.ms.v2.StockData;
import de.uniba.dsg.wss.data.transfer.representations.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This controller provides read-only access to many of the resources managed by the server when
 * launched in MS persistence mode.
 *
 * @author Benedikt Full
 */
@RestController
@ConditionalOnProperty(name = "jpb.persistence.mode", havingValue = "ms")
public class MsResourcesController implements ResourcesController {

  // TODO check if this is really a good idea
  private final MsDataRoot dataRoot;
  private final ModelMapper modelMapper;

  @Autowired
  public MsResourcesController(MsDataRoot dataRoot) {

    this.dataRoot = dataRoot;
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

    modelMapper = new ModelMapper();
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
    return dataRoot.getWarehouses().entrySet().stream()
            .map(w -> modelMapper.map(w.getValue(), WarehouseRepresentation.class))
            .collect(Collectors.toList());
  }

  @GetMapping("w")
  public List<WarehouseGUI> getDebugWarehouseOutput(){
    return dataRoot.getWarehouses().entrySet().stream()
            .map(w -> createRepresentation(w.getValue()))
            .collect(Collectors.toList());
  }

  class WarehouseGUI{
    public String id;
    public String name;
    public AddressData address;
    public double salesTax;
    public double yearToDateBalance;

    public List<DistrictGUI> districtGUIS;
    class DistrictGUI{
      // primary key
      public String id;
      // foreign key (N:1)
      public String warehouseId;
      // foreign key (1:N)
      public List<CustomerGUI> customers;
      class CustomerGUI{
        // primary key
        public String id;
        // foreign key
        public String districtId;
        // foreign key (1:N) - break cyclic dependency here
        public List<String> orderStrings;
        // foreign key (1:N)
        public List<PaymentGUI> payments;

        public CustomerGUI(String id, String districtId, List<String> orders, List<PaymentGUI> payments){
          this.id = id;
          this.districtId = districtId;
          this.orderStrings = orders;
          this.payments = payments;
        }
      }
      // foreign key (1:N)
      public List<OrderGUI> orders;
      class OrderGUI{
        // primary key
        public String id;
        // foreign key (N:1)
        public String districtId;
        // (foreign key (N:1)
        public String customerId;
        // foreign key (1:1)
        public String carrierId;
        // foregin key (1:N)
        public List<OrderItemGUI> items;


        public OrderGUI(String id, String districtId, String customerId, String carrierId, List<OrderItemGUI> items) {
          this.id = id;
          this.districtId = districtId;
          this.customerId = customerId;
          this.carrierId = carrierId;
          this.items = items;
        }
      }

      class OrderItemGUI{
        // primary key
        public String id;
        // foreign key (N:1)
        public String orderId;
        // foregin key (N:1)
        public String warhouseId;
        // foreign key (N:1)
        public String productId;

        public OrderItemGUI(String id, String orderId, String warhouseId, String productId) {
          this.id = id;
          this.orderId = orderId;
          this.warhouseId = warhouseId;
          this.productId = productId;
        }
      }

      class PaymentGUI {
        // primary key
        public String id;
        // foreign key
        public String customerId;

        public PaymentGUI(String id, String customerId) {
          this.id = id;
          this.customerId = customerId;
        }
      }

      public DistrictGUI(DistrictData district) {
        this.id = district.getId();
        this.warehouseId = district.getWarehouse().getId();
        this.orders = district.getOrders().stream().map(c->
                new OrderGUI(c.getId(),
                        c.getDistrictRef().getId(),
                        c.getCustomerRef().getId(),
                        c.getCarrierRef() == null ? null : c.getCarrierRef().getId(),
                        c.getItems().stream().map(i ->
                                new OrderItemGUI(i.getId(),
                                        i.getOrderRef().getId(),
                                        i.getWarehouseRef().getId(),
                                        i.getProductRef().getId())).collect(Collectors.toList())
                )).collect(Collectors.toList());
        this.customers = district.getCustomers().stream().map(c -> new CustomerGUI(c.getId(),
                c.getDistrict().getId(),
                orders.stream().map(o -> o.id).collect(Collectors.toList()),
                c.getPaymentRefs().stream().map(p -> new PaymentGUI(p.getId(),
                        p.getCustomerRef().getId())).collect(Collectors.toList()))).collect(Collectors.toList());
        }

    }

    public List<StockGUI> stockGUIs;
    class StockGUI{
      // primary key
      public String id;
      // foreign key (N:1)
      public String warehouseId;
      // foreign key (1:1)
      public String productId;

      public StockGUI(StockData stock){
        this.id = stock.getId();
        this.warehouseId = stock.getWarehouseRef().getId();
        this.productId = stock.getProductRef().getId();
      }
    }



    public WarehouseGUI(WarehouseData data) {
      this.id = data.getId();
      this.name = data.getName();
      this.address = data.getAddress();
      this.salesTax = data.getSalesTax();
      this.yearToDateBalance = data.getYearToDateBalance();
      this.districtGUIS = new ArrayList<>();
      for(DistrictData district : data.getDistricts()) {
        districtGUIS.add(new DistrictGUI(district));
      }
      this.stockGUIs = new ArrayList<>();
      for(StockData stock : data.getStocks()){
        stockGUIs.add(new StockGUI(stock));
      }
    }
  }

  private WarehouseGUI createRepresentation(WarehouseData data) {
    return new WarehouseGUI(data);
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
