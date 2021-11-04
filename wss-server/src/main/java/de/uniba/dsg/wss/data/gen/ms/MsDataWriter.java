package de.uniba.dsg.wss.data.gen.ms;

import de.uniba.dsg.wss.data.gen.DataWriter;
import de.uniba.dsg.wss.data.model.ms.MsDataRoot;
import de.uniba.dsg.wss.util.Stopwatch;
import one.microstream.storage.embedded.types.EmbeddedStorageManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Can be used to write a wholesale supplier data model to MicroStream-based storage via the JACIS
 * stores.
 *
 * @author Benedikt Full
 */
@Component
@ConditionalOnProperty(name = "jpb.persistence.mode", havingValue = "ms")
public class MsDataWriter implements DataWriter<JpaToMsConverter> {

  private static final Logger LOG = LogManager.getLogger(MsDataWriter.class);

  private final EmbeddedStorageManager storageManager;
  private final MsDataRoot msDataRoot;

  @Autowired
  public MsDataWriter(EmbeddedStorageManager storageManager, MsDataRoot dataRoot) {
    this.storageManager = storageManager;
    this.msDataRoot = dataRoot;
  }

  @Override
  public void writeAll(JpaToMsConverter converter) {
    Stopwatch stopwatch = new Stopwatch(true);

    this.msDataRoot.getWarehouses().putAll(converter.getWarehousesMap());
    this.msDataRoot.getEmployees().putAll(converter.getEmployees());
    this.msDataRoot.getCustomers().putAll(converter.getCustomers());
    this.msDataRoot.getStocks().putAll(converter.getSocksOptimized());
    this.msDataRoot.getOrders().putAll(converter.getOrders());
    this.storageManager.setRoot(this.msDataRoot);
    this.storageManager.storeRoot();

    stopwatch.stop();
    LOG.info("Wrote model data to MicroStream storage, took {}", stopwatch.getDuration());
  }
}
