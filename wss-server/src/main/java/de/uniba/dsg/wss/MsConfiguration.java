package de.uniba.dsg.wss;

import de.uniba.dsg.wss.data.model.ms.MsDataRoot;
import de.uniba.dsg.wss.util.Stopwatch;
import one.microstream.afs.nio.types.NioFileSystem;
import one.microstream.storage.embedded.types.EmbeddedStorageFoundation;
import one.microstream.storage.embedded.types.EmbeddedStorageManager;
import one.microstream.storage.types.Storage;
import one.microstream.storage.types.StorageChannelCountProvider;
import one.microstream.storage.types.StorageConfiguration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * Configures all beans which are required for MicroStream-based persistence.
 *
 * <p>This includes many beans relevant for data access, for which the <a
 * href="https://github.com/JanWiemer/jacis">JACIS framework</a> is being used. This is due to the
 * fact that MicroStream itself is only a storage engine, which provides no support for
 * transactions.
 *
 * @author Benedikt Full
 */
@Configuration
@ConditionalOnProperty(name = "jpb.persistence.mode", havingValue = "ms")
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class})
public class MsConfiguration {

  private static final Logger LOG = LogManager.getLogger(MsConfiguration.class);

  private final Environment environment;

  @Autowired
  public MsConfiguration(Environment environment) {
    this.environment = environment;
  }

  @Bean
  public MsDataRoot createDataRoot(){
    return new MsDataRoot();
  }

  @Bean
  public EmbeddedStorageManager embeddedStorageManager() {
    NioFileSystem fileSystem = NioFileSystem.New();
    EmbeddedStorageFoundation<?> foundation =
        EmbeddedStorageFoundation.New()
            .setConfiguration(
                StorageConfiguration.Builder()
                    .setHousekeepingController(Storage.HousekeepingController(1_000, 10_000_000))
                    .setDataFileEvaluator(
                        Storage.DataFileEvaluator(1024 * 1024, 1024 * 1024 * 8, 0.75))
                    .setEntityCacheEvaluator(
                        Storage.EntityCacheEvaluator(86_400_000, 1_000_000_000))
                    .setStorageFileProvider(
                        Storage.FileProviderBuilder(fileSystem)
                            .setDirectory(
                                fileSystem.ensureDirectoryPath(
                                    environment.getRequiredProperty("jpb.ms.storage.dir")))
                            .createFileProvider())
                    .setChannelCountProvider(
                        StorageChannelCountProvider.New(
                            Integer.highestOneBit(Runtime.getRuntime().availableProcessors() - 1)))
                    .createConfiguration());

    EmbeddedStorageManager storageManager = foundation.createEmbeddedStorageManager();

    Stopwatch stopwatch = new Stopwatch(true);
    storageManager.start();
    stopwatch.stop();
    LOG.info("Started MicroStream storage manager, took {}", stopwatch.getDuration());

    return storageManager;
  }
}
