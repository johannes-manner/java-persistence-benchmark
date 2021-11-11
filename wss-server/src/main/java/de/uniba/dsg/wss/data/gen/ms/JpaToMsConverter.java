package de.uniba.dsg.wss.data.gen.ms;

import de.uniba.dsg.wss.data.gen.jpa.JpaDataGenerator;
import de.uniba.dsg.wss.data.model.jpa.*;
import de.uniba.dsg.wss.data.model.ms.*;
import de.uniba.dsg.wss.util.Stopwatch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Converts a JPA data model to a MicroStream data model. Converting an existing model ensures that
 * both models are alike and at the same time eliminates the need for duplicate model generation
 * code.
 *
 * @see JpaDataGenerator
 * @author Benedikt Full
 */
public class JpaToMsConverter {

  private static final Logger LOG = LogManager.getLogger(JpaToMsConverter.class);
  private final List<ProductEntity> productEntities;
  private final List<CarrierEntity> carrierEntities;
  private final List<WarehouseEntity> warehouseEntities;
  private final List<EmployeeEntity> employeeEntities;

  private Map<String, ProductData> products;
  private Map<String, CarrierData> carriers;
  private Map<String, WarehouseData> warehouses;
  private Map<String, StockData> stocks;
  private Map<String, DistrictData> districts;
  private Map<String, EmployeeData> employees;
  private Map<String, CustomerData> customers;
  private Map<String, OrderData> orders;
  private List<OrderItemData> orderItems;
  private List<PaymentData> payments;

  public JpaToMsConverter(JpaDataGenerator dataGenerator) {
    if (dataGenerator.getWarehouses() == null) {
      dataGenerator.generate();
    }
    productEntities = dataGenerator.getProducts();
    carrierEntities = dataGenerator.getCarriers();
    warehouseEntities = dataGenerator.getWarehouses();
    employeeEntities = dataGenerator.getEmployees();
    products = null;
    carriers = null;
    warehouses = null;
    stocks = null;
    districts = null;
    employees = null;
    customers = null;
    orders = null;
    payments = null;
  }

  public void convert() {
    Stopwatch stopwatch = new Stopwatch(true);


    products = convertProducts(productEntities);
    carriers = convertCarriers(carrierEntities);
    warehouses = convertWarehouses(warehouseEntities);
    stocks = convertStocks(warehouseEntities);

    districts = convertDistricts(warehouseEntities);
    employees = convertEmployees(employeeEntities);
    customers = convertCustomers(warehouseEntities);
    orders = convertOrders(warehouseEntities);
    orderItems = convertOrderItems(warehouseEntities);
    payments = convertPayments(warehouseEntities);
    stopwatch.stop();
    LOG.info("Converted model data to MicroStream data, took {}", stopwatch.getDuration());
  }

  public Map<String, ProductData> getProducts() {
    return products;
  }

  public Map<String, CarrierData> getCarriers() {
    return carriers;
  }

  // UNCHANGED TO V1.0

  private Map<String, ProductData> convertProducts(List<ProductEntity> ps) {
    Map<String, ProductData> products = new HashMap<>();
    for (ProductEntity p : ps) {
      ProductData product = new ProductData(p.getId(),
              p.getImagePath(),
              p.getName(),
              p.getPrice(),
              p.getData());

      products.put(product.getId(), product);
    }
    LOG.debug("Converted {} products", products.size());
    return products;
  }

  private Map<String, CarrierData> convertCarriers(List<CarrierEntity> cs) {
    Map<String, CarrierData> carriers = new HashMap<>();
    for (CarrierEntity c : cs) {
      CarrierData carrier = new CarrierData(c.getId(),
              c.getName(),
              c.getPhoneNumber(),
              address(c.getAddress()));

      carriers.put(carrier.getId(), carrier);
    }
    LOG.debug("Converted {} carriers", carriers.size());
    return carriers;
  }

  // NEW
  private Map<String, WarehouseData> convertWarehouses(List<WarehouseEntity> ws) {
    Map<String, WarehouseData> warehouses = new HashMap<>();
    for (WarehouseEntity w : ws) {
      WarehouseData warehouse = new WarehouseData(w.getId(), w.getName(), address(w.getAddress()),w.getSalesTax());

      // NEW relaxing the concurrency thing here, since at the data generation step, the procedure is implemented single threaded
      warehouse.increaseYearToBalance(w.getYearToDateBalance());
      warehouses.put(warehouse.getId(), warehouse);

    }
    LOG.debug("Converted {} warehouses", warehouses.size());
    return warehouses;
  }

  /**
   * Districts are now also added to the warehouse (bidirectional relationship)
   *
   * @param ws
   * @return
   */
  private Map<String, DistrictData> convertDistricts(List<WarehouseEntity> ws) {
    Map<String, DistrictData> districts = new HashMap<>();
    for (WarehouseEntity w : ws) {
      WarehouseData warehouse = this.warehouses.get(w.getId());
      Map<String, DistrictData> districtsForWarehouse = warehouse.getDistricts();

      for (DistrictEntity d : w.getDistricts()) {
        // referential integrity...
        DistrictData districtData = district(d,warehouse);
        districtsForWarehouse.put(districtData.getId(), districtData);

        districts.put(districtData.getId(), districtData);
      }
    }
    LOG.debug("Converted {} districts", districts.size());
    return districts;
  }

  public Map<String,WarehouseData> getWarehousesMap() {
    return warehouses;
  }

  private Map<String, StockData> convertStocks(List<WarehouseEntity> ws) {
    Map<String, StockData> stocks = new HashMap<>();
    for(WarehouseEntity warehouseEntity : ws) {
      WarehouseData warehouse = this.warehouses.get(warehouseEntity.getId());
      for(StockEntity stockEntity : warehouseEntity.getStocks()){
        // create stock data
        StockData stockData = this.stock(stockEntity, warehouse);
        // add stock to warehouse
        warehouse.getStocks().add(stockData);
        stocks.put(stockData.getId(), stockData);
      }
    }

    LOG.debug("Converted {} stocks", stocks.size());
    return stocks;
  }

  public Map<String, DistrictData> getDistricts() {
    return districts;
  }

  public Map<String, EmployeeData> getEmployees() {
    return employees;
  }

  public Map<String, CustomerData> getCustomers() {
    return customers;
  }

  public Map<String, OrderData> getOrders() {
    return orders;
  }

  public List<OrderItemData> getOrderItems() {
    return orderItems;
  }

  public List<PaymentData> getPayments() {
    return payments;
  }



  private StockData stock(StockEntity s, WarehouseData warehouse) {
    StockData stock = new StockData(warehouse,
            this.products.get(s.getProduct().getId()),
            s.getQuantity(),
            s.getYearToDateBalance(),
            s.getOrderCount(),
            s.getRemoteCount(),
            s.getData(),
            s.getDist01(),
            s.getDist02(),
            s.getDist03(),
            s.getDist04(),
            s.getDist05(),
            s.getDist06(),
            s.getDist07(),
            s.getDist08(),
            s.getDist09(),
            s.getDist10());
    return stock;
  }

  private Map<String, EmployeeData> convertEmployees(List<EmployeeEntity> es) {
    Map<String, EmployeeData> employees = new HashMap<>();
    for (EmployeeEntity e : es) {
      EmployeeData employee = new EmployeeData(e.getId(),
              e.getFirstName(),
              e.getMiddleName(),
              e.getLastName(),
              address(e.getAddress()),
              e.getPhoneNumber(),
              e.getEmail(),
              e.getTitle(),
              e.getUsername(),
              e.getPassword(),
              this.districts.get(e.getDistrict().getId()));

      employees.put(employee.getUsername(), employee);
    }
    LOG.debug("Converted {} employees", employees.size());
    return employees;
  }

  private Map<String, CustomerData> convertCustomers(List<WarehouseEntity> ws) {
    List<CustomerEntity> cs = new ArrayList<>();
    for (WarehouseEntity w : ws) {
      for (DistrictEntity d : w.getDistricts()) {
        cs.addAll(d.getCustomers());
      }
    }
    return customers(cs);
  }

  private Map<String, CustomerData> customers(List<CustomerEntity> cs) {
    Map<String, CustomerData> customers = new HashMap<>();
    for (CustomerEntity c : cs) {
      CustomerData customer = new CustomerData(c.getId(),
              c.getFirstName(),
              c.getMiddleName(),
              c.getLastName(),
              address(c.getAddress()),
              c.getPhoneNumber(),
              c.getEmail(),
              // referential integrity
              this.districts.get(c.getDistrict().getId()),
              c.getSince(),
              c.getCredit(),
              c.getCreditLimit(),
              c.getDiscount(),
              c.getBalance(),
              c.getYearToDatePayment(),
              c.getPaymentCount(),
              c.getDeliveryCount(),
              c.getData());

      customers.put(customer.getId(), customer);
      // referential integrity
      this.districts.get(customer.getDistrict().getId()).getCustomers().add(customer);
    }
    LOG.debug("Converted {} customers", customers.size());
    return customers;
  }

  private Map<String, OrderData> convertOrders(List<WarehouseEntity> ws) {
    Map<String, OrderData> orders = new HashMap<>();
    for (WarehouseEntity w : ws) {
      for (DistrictEntity d : w.getDistricts()) {
        DistrictData district = this.districts.get(d.getId());
        for(OrderEntity o : d.getOrders()) {
          OrderData order = new OrderData(o.getId(),
                  district,
                  // referential integrity
                  this.customers.get(o.getCustomer().getId()),
                  // referential integrity
                  this.carriers.get(o.getCarrier() == null ? null : o.getCarrier().getId()),
                  o.getEntryDate(),
                  o.getItemCount(),
                  o.isAllLocal(),
                  o.isFulfilled());

          orders.put(order.getId(), order);
          // referential integrity
          district.getOrders().put(order.getId(), order);
          this.customers.get(order.getCustomerRef().getId()).getOrderRefs().put(order.getId(), order);
        }
      }
    }
    return orders;
  }

  private List<OrderItemData> convertOrderItems(List<WarehouseEntity> ws) {
    List<OrderItemData> ois = new ArrayList<>();
    for (WarehouseEntity w : ws) {
      for (DistrictEntity d : w.getDistricts()) {
        for (OrderEntity o : d.getOrders()) {
          OrderData order = this.orders.get(o.getId());
          for (OrderItemEntity i : o.getItems()) {
            OrderItemData item = new OrderItemData(i.getId(),
                    order,
                    this.products.get(i.getProduct().getId()),
                    this.warehouses.get(i.getSupplyingWarehouse().getId()),
                    i.getNumber(),
                    i.getDeliveryDate(),
                    i.getQuantity(),
                    0, // ok for this initial values
                    i.getAmount(),
                    i.getDistInfo()
            );

            ois.add(item);
            //referential integrity
            order.getItems().add(item);
          }
        }
      }
    }
    return ois;
  }

  private List<PaymentData> convertPayments(List<WarehouseEntity> ws) {
    List<PaymentEntity> ps =
        ws.parallelStream()
            .flatMap(
                w ->
                    w.getDistricts().parallelStream()
                        .flatMap(
                            d ->
                                d.getCustomers().parallelStream()
                                    .flatMap(c -> c.getPayments().stream())))
            .collect(Collectors.toList());

    List<PaymentData> payments = new ArrayList<>();
    for (PaymentEntity p : ps) {
      PaymentData payment = new PaymentData(p.getId(),
              this.customers.get(p.getCustomer().getId()),
              p.getDate(),
              p.getAmount(),
              p.getData());

      payments.add(payment);
      // referential integrity
      this.customers.get(p.getCustomer().getId()).getPaymentRefs().add(payment);

    }

    return payments;
  }

  private DistrictData district(DistrictEntity d, WarehouseData warehouse) {
    DistrictData district = new DistrictData(
            d.getId(),
            warehouse,
            d.getName(),
            address(d.getAddress()),
            d.getSalesTax(),
            d.getYearToDateBalance()
    );
    return district;
  }

  private static AddressData address(AddressEmbeddable a) {
    return new AddressData(
        a.getStreet1(), a.getStreet2(), a.getZipCode(), a.getCity(), a.getState());
  }

  public Map<String, StockData> getStocks() {
    return this.stocks;
  }
}
