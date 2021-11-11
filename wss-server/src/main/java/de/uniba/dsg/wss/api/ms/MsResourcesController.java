package de.uniba.dsg.wss.api.ms;

import de.uniba.dsg.wss.api.ResourcesController;
import de.uniba.dsg.wss.data.model.ms.*;
import de.uniba.dsg.wss.data.transfer.representations.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

  private final MsDataRoot dataRoot;
  private final ModelMapper modelMapper;

  @Autowired
  public MsResourcesController(MsDataRoot dataRoot) {

    this.dataRoot = dataRoot;
    modelMapper = new ModelMapper();
  }

  @Override
  public Iterable<ProductRepresentation> getProducts() {

    return this.dataRoot.getProducts().entrySet().stream()
            .parallel()
            .map(p -> modelMapper.map(p.getValue(), ProductRepresentation.class))
            .collect(Collectors.toList());
  }

  @Override
  public ResponseEntity<EmployeeRepresentation> getEmployee(String username) {
    EmployeeData employee = this.dataRoot.getEmployees().get(username);
    if (employee == null) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }
    return ResponseEntity.ok(modelMapper.map(employee, EmployeeRepresentation.class));
  }

  @Override
  public List<WarehouseRepresentation> getWarehouses() {
    return dataRoot.getWarehouses().entrySet().stream()
            .map(w -> modelMapper.map(w.getValue(), WarehouseRepresentation.class))
            .collect(Collectors.toList());
  }


  @Override
  public ResponseEntity<List<DistrictRepresentation>> getWarehouseDistricts(String warehouseId) {
    WarehouseData warehouse = this.dataRoot.getWarehouses().get(warehouseId);
    if (warehouse == null) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }
    List<DistrictRepresentation> districtRepresentations = warehouse.getDistricts().entrySet()
            .parallelStream()
            .map(d -> modelMapper.map(d.getValue(), DistrictRepresentation.class))
            .collect(Collectors.toList());

    return ResponseEntity.ok(districtRepresentations);
  }

  @Override
  public ResponseEntity<List<StockRepresentation>> getWarehouseStocks(String warehouseId) {
    WarehouseData warehouse = this.dataRoot.getWarehouses().get(warehouseId);
    if (warehouse == null) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }
    List<StockRepresentation> stockRepresentations = warehouse.getStocks()
            .parallelStream()
            .map(s -> modelMapper.map(s, StockRepresentation.class))
            .collect(Collectors.toList());
    return ResponseEntity.ok(stockRepresentations);
  }

  @Override
  public ResponseEntity<List<CustomerRepresentation>> getDistrictCustomers(String warehouseId, String districtId) {
    WarehouseData warehouse = this.dataRoot.getWarehouses().get(warehouseId);
    if (warehouse == null) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }
    DistrictData district = warehouse.getDistricts().get(districtId);
    if (district == null) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }

    List<CustomerRepresentation> customerRepresentations = district.getCustomers().parallelStream()
            .map(c -> modelMapper.map(c, CustomerRepresentation.class))
            .collect(Collectors.toList());
    return ResponseEntity.ok(customerRepresentations);
  }

  @Override
  public ResponseEntity<List<OrderRepresentation>> getDistrictOrders(String warehouseId, String districtId) {
    WarehouseData warehouse = this.dataRoot.getWarehouses().get(warehouseId);
    if (warehouse == null) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }
    DistrictData district = warehouse.getDistricts().get(districtId);
    if (district == null) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }

    List<OrderRepresentation> orderRepresentations = district.getOrders().entrySet().parallelStream()
            .map(o -> modelMapper.map(o.getValue(), OrderRepresentation.class))
            .collect(Collectors.toList());
    return ResponseEntity.ok(orderRepresentations);
  }

  @Override
  public List<CarrierRepresentation> getCarriers() {
    return this.dataRoot.getCarriers().entrySet().parallelStream()
            .map(c -> modelMapper.map(c.getValue(), CarrierRepresentation.class))
            .collect(Collectors.toList());
  }

  // ONLY FOR DEBUGGING

  @GetMapping("w")
  public RootGUI getDebugWarehouseOutput(){
    return new RootGUI(
            dataRoot.getWarehouses().entrySet().stream()
                    .map(w -> new WarehouseGUI(w.getValue()))
                    .collect(Collectors.toList()),
            dataRoot.getEmployees().entrySet().stream()
                    .map(e -> new EmployeeGUI(e.getValue()))
                    .collect(Collectors.toList())
    );
  }

  class RootGUI {
    public List<WarehouseGUI> warehouseGUIS;
    public List<EmployeeGUI> employeeGUIS;

    public RootGUI(List<WarehouseGUI> warehouseGUIS, List<EmployeeGUI> employeeGUIS) {
      this.warehouseGUIS = warehouseGUIS;
      this.employeeGUIS = employeeGUIS;
    }
  }

  class EmployeeGUI {
    // primary key
    public String id;
    // foreign key (N:1)
    public String districtId;

    public EmployeeGUI(EmployeeData employee) {
      this.id = employee.getId();
      this.districtId = employee.getDistrictRef().getId();
    }
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
        this.orders = district.getOrders().entrySet().stream()
                .map(c-> c.getValue())
                .map(c ->
                        new OrderGUI(c.getId(),
                                c.getDistrictRef().getId(),
                                c.getCustomerRef().getId(),
                                c.getCarrierRef() == null ? null : c.getCarrierRef().getId(),
                                c.getItems().stream().map(i ->
                                        new OrderItemGUI(i.getId(),
                                                i.getOrderRef().getId(),
                                                i.getSupplyingWarehouseRef().getId(),
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
      for(DistrictData district : data.getDistricts().entrySet().stream().map(Map.Entry::getValue).collect(Collectors.toList())) {
        districtGUIS.add(new DistrictGUI(district));
      }
      this.stockGUIs = new ArrayList<>();
      for(StockData stock : data.getStocks()){
        stockGUIs.add(new StockGUI(stock));
      }
    }
  }
}
