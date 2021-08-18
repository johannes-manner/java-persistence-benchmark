package de.uniba.dsg.jpb.server.data.gen;

import de.uniba.dsg.jpb.server.data.model.jpa.AddressEmbeddable;
import de.uniba.dsg.jpb.server.data.model.jpa.CarrierEntity;
import de.uniba.dsg.jpb.server.data.model.jpa.CustomerEntity;
import de.uniba.dsg.jpb.server.data.model.jpa.DistrictEntity;
import de.uniba.dsg.jpb.server.data.model.jpa.EmployeeEntity;
import de.uniba.dsg.jpb.server.data.model.jpa.OrderEntity;
import de.uniba.dsg.jpb.server.data.model.jpa.OrderItemEntity;
import de.uniba.dsg.jpb.server.data.model.jpa.PaymentEntity;
import de.uniba.dsg.jpb.server.data.model.jpa.ProductEntity;
import de.uniba.dsg.jpb.server.data.model.jpa.StockEntity;
import de.uniba.dsg.jpb.server.data.model.jpa.WarehouseEntity;
import de.uniba.dsg.jpb.server.data.model.ms.AddressData;
import de.uniba.dsg.jpb.server.data.model.ms.CarrierData;
import de.uniba.dsg.jpb.server.data.model.ms.CustomerData;
import de.uniba.dsg.jpb.server.data.model.ms.DistrictData;
import de.uniba.dsg.jpb.server.data.model.ms.EmployeeData;
import de.uniba.dsg.jpb.server.data.model.ms.OrderData;
import de.uniba.dsg.jpb.server.data.model.ms.OrderItemData;
import de.uniba.dsg.jpb.server.data.model.ms.PaymentData;
import de.uniba.dsg.jpb.server.data.model.ms.ProductData;
import de.uniba.dsg.jpb.server.data.model.ms.StockData;
import de.uniba.dsg.jpb.server.data.model.ms.WarehouseData;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class JpaToMsConverter {

  private final List<ProductEntity> productEntities;
  private final List<CarrierEntity> carrierEntities;
  private final List<WarehouseEntity> warehouseEntities;
  private final List<EmployeeEntity> employeeEntities;
  private List<ProductData> products;
  private List<WarehouseData> warehouses;
  private List<EmployeeData> employees;
  private List<CarrierData> carriers;

  public JpaToMsConverter(JpaDataGenerator dataGenerator) {
    if (dataGenerator.getWarehouses() == null) {
      dataGenerator.generate();
    }
    productEntities = dataGenerator.getProducts();
    carrierEntities = dataGenerator.getCarriers();
    warehouseEntities = dataGenerator.getWarehouses();
    employeeEntities = dataGenerator.getEmployees();
    products = null;
    warehouses = null;
    employees = null;
    carriers = null;
  }

  public void convert() {
    products = convertProducts(productEntities);
    carriers = convertCarriers(carrierEntities);
    warehouses = convertWarehouses(warehouseEntities, products, carriers);
    employees = convertEmployees(employeeEntities, warehouses);
  }

  public List<ProductData> getProducts() {
    return products;
  }

  public List<CarrierData> getCarriers() {
    return carriers;
  }

  public List<WarehouseData> getWarehouses() {
    return warehouses;
  }

  public List<EmployeeData> getEmployees() {
    return employees;
  }

  private List<ProductData> convertProducts(List<ProductEntity> ps) {
    List<ProductData> products = new ArrayList<>(ps.size());
    for (ProductEntity p : ps) {
      ProductData product = new ProductData();
      product.setId(p.getId());
      product.setName(p.getName());
      product.setImageId(p.getImageId());
      product.setPrice(p.getPrice());
      product.setData(p.getData());
      products.add(product);
    }
    return products;
  }

  private List<CarrierData> convertCarriers(List<CarrierEntity> cs) {
    List<CarrierData> carriers = new ArrayList<>(cs.size());
    for (CarrierEntity c : cs) {
      CarrierData carrier = new CarrierData();
      carrier.setId(c.getId());
      carrier.setName(c.getName());
      carrier.setPhoneNumber(c.getPhoneNumber());
      carrier.setAddress(address(c.getAddress()));
      carriers.add(carrier);
    }
    return carriers;
  }

  private List<WarehouseData> convertWarehouses(
      List<WarehouseEntity> ws, List<ProductData> products, List<CarrierData> carriers) {
    List<WarehouseData> warehouses = new ArrayList<>(ws.size());
    for (WarehouseEntity w : ws) {
      WarehouseData warehouse = new WarehouseData();
      warehouse.setId(w.getId());
      warehouse.setName(w.getName());
      warehouse.setAddress(address(w.getAddress()));
      warehouse.setYearToDateBalance(w.getYearToDateBalance());
      warehouse.setSalesTax(w.getSalesTax());
      warehouse.setStocks(
          w.getStocks().stream()
              .map(s -> stock(s, warehouse, findProductById(s.getProduct().getId(), products)))
              .collect(Collectors.toList()));
      warehouses.add(warehouse);
    }
    // Create districts and all related components, which require references to all warehouses
    for (WarehouseData w : warehouses) {
      WarehouseEntity entity = findWarehouseEntityById(w.getId(), ws);
      List<DistrictData> districts =
          entity.getDistricts().stream()
              .map(d -> district(d, w, warehouses, products, carriers))
              .collect(Collectors.toList());
      districts.forEach(d -> d.setWarehouse(w));
      w.setDistricts(districts);
    }
    return warehouses;
  }

  private List<EmployeeData> convertEmployees(
      List<EmployeeEntity> es, List<WarehouseData> warehouses) {
    List<EmployeeData> employees = new ArrayList<>(es.size());
    for (EmployeeEntity e : es) {
      EmployeeData employee = new EmployeeData();
      employee.setId(e.getId());
      employee.setAddress(address(e.getAddress()));
      employee.setFirstName(e.getFirstName());
      employee.setMiddleName(e.getMiddleName());
      employee.setLastName(e.getLastName());
      employee.setPhoneNumber(e.getPhoneNumber());
      employee.setEmail(e.getEmail());
      employee.setUsername(e.getUsername());
      employee.setPasswordHash(e.getPasswordHash());
      employee.setSalt(e.getSalt());
      employee.setTitle(e.getTitle());
      employee.setDistrict(findDistrictById(e.getDistrict().getId(), warehouses));
      employees.add(employee);
    }
    return employees;
  }

  private DistrictData district(
      DistrictEntity d,
      WarehouseData w,
      List<WarehouseData> ws,
      List<ProductData> ps,
      List<CarrierData> cs) {
    DistrictData district = new DistrictData();
    district.setId(d.getId());
    district.setAddress(address(d.getAddress()));
    district.setSalesTax(d.getSalesTax());
    district.setYearToDateBalance(d.getYearToDateBalance());
    district.setWarehouse(w);
    district.setOrders(orders(d.getOrders(), district, ws, ps, cs));
    district.setCustomers(customers(d.getCustomers(), district));
    return district;
  }

  private static List<CustomerData> customers(List<CustomerEntity> cs, DistrictData d) {
    List<CustomerData> customers = new ArrayList<>(cs.size());
    for (CustomerEntity c : cs) {
      CustomerData customer = new CustomerData();
      customer.setId(c.getId());
      customer.setAddress(address(c.getAddress()));
      customer.setFirstName(c.getFirstName());
      customer.setMiddleName(c.getMiddleName());
      customer.setLastName(c.getLastName());
      customer.setPhoneNumber(c.getPhoneNumber());
      customer.setEmail(c.getEmail());
      customer.setCredit(c.getCredit());
      customer.setCreditLimit(c.getCreditLimit());
      customer.setBalance(c.getBalance());
      customer.setDiscount(c.getDiscount());
      customer.setData(c.getData());
      customer.setPaymentCount(c.getPaymentCount());
      customer.setYearToDatePayment(c.getYearToDatePayment());
      customer.setPaymentCount(c.getPaymentCount());
      customer.setDeliveryCount(c.getDeliveryCount());
      customer.setPayments(payments(c.getPayments(), customer, d));
      customer.setOrders(findOrdersByCustomerId(c.getId(), d.getOrders()));
      customer.setDistrict(d);
    }
    return customers;
  }

  private static List<OrderData> orders(
      List<OrderEntity> os,
      DistrictData d,
      List<WarehouseData> ws,
      List<ProductData> ps,
      List<CarrierData> cs) {
    List<OrderData> orders = new ArrayList<>(os.size());
    for (OrderEntity o : os) {
      OrderData order = new OrderData();
      order.setId(o.getId());
      order.setItemCount(o.getItemCount());
      order.setEntryDate(o.getEntryDate());
      order.setFulfilled(o.isFulfilled());
      order.setCarrier(findCarrierById(o.getCarrier().getId(), cs));
      order.setAllLocal(o.isAllLocal());
      order.setDistrict(d);
      order.setCustomer(findCustomerById(o.getCustomer().getId(), d.getCustomers()));
      order.setItems(orderItems(o.getItems(), order, ws, ps));
      orders.add(order);
    }
    return orders;
  }

  private static List<OrderItemData> orderItems(
      List<OrderItemEntity> ois, OrderData o, List<WarehouseData> ws, List<ProductData> ps) {
    List<OrderItemData> orderItems = new ArrayList<>(ois.size());
    for (OrderItemEntity oi : ois) {
      OrderItemData orderItem = new OrderItemData();
      orderItem.setId(oi.getId());
      orderItem.setOrder(o);
      orderItem.setProduct(findProductById(oi.getProduct().getId(), ps));
      orderItem.setSupplyingWarehouse(findWarehouseById(oi.getSupplyingWarehouse().getId(), ws));
      orderItem.setAmount(oi.getAmount());
      orderItem.setQuantity(oi.getQuantity());
      orderItem.setNumber(oi.getNumber());
      orderItem.setDistInfo(oi.getDistInfo());
      orderItem.setDeliveryDate(oi.getDeliveryDate());
      orderItems.add(orderItem);
    }
    return orderItems;
  }

  private static List<PaymentData> payments(
      List<PaymentEntity> ps, CustomerData c, DistrictData d) {
    List<PaymentData> payments = new ArrayList<>(ps.size());
    for (PaymentEntity p : ps) {
      PaymentData payment = new PaymentData();
      payment.setId(p.getId());
      payment.setAmount(p.getAmount());
      payment.setDate(p.getDate());
      payment.setData(p.getData());
      payment.setCustomer(c);
      payment.setDistrict(d);
      payments.add(payment);
    }
    return payments;
  }

  private static StockData stock(StockEntity s, WarehouseData w, ProductData p) {
    StockData stock = new StockData();
    stock.setId(s.getId());
    stock.setWarehouse(w);
    stock.setProduct(p);
    stock.setData(s.getData());
    stock.setQuantity(s.getQuantity());
    stock.setDist01(s.getDist01());
    stock.setDist02(s.getDist02());
    stock.setDist03(s.getDist03());
    stock.setDist04(s.getDist04());
    stock.setDist05(s.getDist05());
    stock.setDist06(s.getDist06());
    stock.setDist07(s.getDist07());
    stock.setDist08(s.getDist08());
    stock.setDist09(s.getDist09());
    stock.setDist10(s.getDist10());
    stock.setOrderCount(s.getOrderCount());
    stock.setRemoteCount(s.getRemoteCount());
    stock.setOrderCount(s.getOrderCount());
    return stock;
  }

  private static AddressData address(AddressEmbeddable a) {
    AddressData address = new AddressData();
    address.setStreet1(a.getStreet1());
    address.setStreet2(a.getStreet2());
    address.setCity(a.getCity());
    address.setZipCode(a.getZipCode());
    address.setState(a.getState());
    return address;
  }

  private static List<OrderData> findOrdersByCustomerId(Long id, List<OrderData> orders) {
    return orders.stream()
        .filter(o -> o.getCustomer().getId().equals(id))
        .collect(Collectors.toList());
  }

  private static WarehouseEntity findWarehouseEntityById(Long id, List<WarehouseEntity> ws) {
    return ws.stream()
        .filter(w -> w.getId().equals(id))
        .findAny()
        .orElseThrow(IllegalArgumentException::new);
  }

  private static ProductData findProductById(Long id, List<ProductData> products) {
    return products.stream()
        .filter(p -> p.getId().equals(id))
        .findAny()
        .orElseThrow(IllegalArgumentException::new);
  }

  private static WarehouseData findWarehouseById(Long id, List<WarehouseData> warehouses) {
    return warehouses.stream()
        .filter(w -> w.getId().equals(id))
        .findAny()
        .orElseThrow(IllegalArgumentException::new);
  }

  private static CustomerData findCustomerById(Long id, List<CustomerData> customers) {
    return customers.stream()
        .filter(c -> c.getId().equals(id))
        .findAny()
        .orElseThrow(IllegalArgumentException::new);
  }

  private static CarrierData findCarrierById(Long id, List<CarrierData> carriers) {
    return carriers.stream()
        .filter(c -> c.getId().equals(id))
        .findAny()
        .orElseThrow(IllegalArgumentException::new);
  }

  private static DistrictData findDistrictById(Long id, List<WarehouseData> warehouses) {
    return warehouses.stream()
        .flatMap(w -> w.getDistricts().stream())
        .filter(d -> d.getId().equals(id))
        .findAny()
        .orElseThrow(IllegalArgumentException::new);
  }
}
