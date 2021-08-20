package de.uniba.dsg.jpb;

import de.uniba.dsg.jpb.data.gen.jpa.JpaDataGenerator;
import de.uniba.dsg.jpb.data.gen.ms.JpaToMsConverter;
import de.uniba.dsg.jpb.data.gen.ms.MsDatabaseWriter;
import de.uniba.dsg.jpb.util.Stopwatch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "jpb.persistence.mode", havingValue = "ms")
public class MsDataInitializer extends DataInitializer {

  private static final Logger LOG = LogManager.getLogger(MsDataInitializer.class);
  private final MsDatabaseWriter databaseWriter;

  @Autowired
  public MsDataInitializer(
      Environment environment, PasswordEncoder passwordEncoder, MsDatabaseWriter databaseWriter) {
    super(environment, passwordEncoder);
    this.databaseWriter = databaseWriter;
  }

  @Override
  public void run(String... args) throws Exception {
    JpaDataGenerator jpaDataGenerator = createJpaDataGenerator();
    LOG.info("Beginning model data generation");
    Stopwatch stopwatch = new Stopwatch(true);
    jpaDataGenerator.generate();
    stopwatch.stop();
    LOG.info("Model data generation took {} seconds", stopwatch.getDurationSeconds());
    stopwatch.start();
    JpaToMsConverter converter = new JpaToMsConverter(jpaDataGenerator);
    converter.convert();
    stopwatch.stop();
    LOG.info(
        "Successfully converted model data to MicroStream data, took {} milliseconds",
        stopwatch.getDurationMillis());
    stopwatch.start();
    databaseWriter.writeAll(converter);
    databaseWriter.close();
    stopwatch.stop();
    LOG.info(
        "Successfully wrote model data to MicroStream storage, took {} seconds",
        stopwatch.getDurationSeconds());
  }

  @Override
  boolean generateIds() {
    return true;
  }
}
