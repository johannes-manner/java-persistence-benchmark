package de.uniba.dsg.jpb.data.gen.ms;

import de.uniba.dsg.jpb.data.access.ms.DataRoot;
import de.uniba.dsg.jpb.data.gen.DataProvider;
import de.uniba.dsg.jpb.data.gen.DatabaseWriter;
import de.uniba.dsg.jpb.data.model.ms.CarrierData;
import de.uniba.dsg.jpb.data.model.ms.EmployeeData;
import de.uniba.dsg.jpb.data.model.ms.ProductData;
import de.uniba.dsg.jpb.data.model.ms.WarehouseData;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "jpb.persistence.mode", havingValue = "ms")
public class MsDatabaseWriter
    implements DatabaseWriter<WarehouseData, EmployeeData, ProductData, CarrierData>,
        AutoCloseable {

  private DataRoot dataRoot;
  private boolean closed;

  public MsDatabaseWriter(DataRoot dataRoot) {
    this.dataRoot = dataRoot;
    closed = false;
  }

  @Override
  public void writeAll(
      DataProvider<WarehouseData, EmployeeData, ProductData, CarrierData> dataProvider) {
    if (closed) {
      return;
    }
    dataRoot.getProductRepository().saveAll(dataProvider.getProducts());
    dataRoot.getCarrierRepository().saveAll(dataProvider.getCarriers());
    dataRoot.getWarehouseRepository().saveAll(dataProvider.getWarehouses());
    dataRoot.getEmployeeRepository().saveAll(dataProvider.getEmployees());
  }

  @Override
  public void close() throws Exception {
    if (closed) {
      return;
    }
    dataRoot = null;
    closed = true;
  }
}
