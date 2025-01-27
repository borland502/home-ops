package com.technohouser.config.properties.db;

import com.google.common.collect.Maps;
import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * A utility class responsible for loading and periodically refreshing database properties from a
 * specified table. It uses the Spring JDBC Template to execute queries against the database and
 * stores the loaded properties in an internal map.
 *
 * @author Anders Swanson
 */
@Slf4j
@Data
public class DatabasePropertyLoader implements AutoCloseable {

  private static Timer timer;
  private final String table;
  private Map<String, String> properties = Maps.newLinkedHashMap();

  /**
   * Constructs a new instance of DatabasePropertyLoader that loads and periodically refreshes
   * database properties from the specified table using the provided JdbcTemplate.
   *
   * @param table   the name of the database table containing the properties
   * @param refresh the duration between automatic refreshes of the properties, or 0 ms to disable
   *                property refresh. The default refresh rate is 10 minutes if not specified.
   */
  public DatabasePropertyLoader(String table, Duration refresh) {
    this.table = table;
    reload();
    long refreshMillis = Optional.ofNullable(refresh).orElse(Duration.ofMinutes(10)).toMillis();
    if (refreshMillis > 0) {
      synchronized (DatabasePropertyLoader.class) {
        if (timer == null) {
          timer = new Timer(true);
          timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
              reload();
            }
          }, refreshMillis, refreshMillis);
        }
      }
    }
  }

  /**
   * Reloads the database properties from the specified table into memory. This method executes a
   * SQL query to retrieve all key-value pairs from the table, then updates the internal map of
   * properties with the retrieved values.
   */
  private void reload() {

  }

  @Override
  public void close() throws Exception {

  }
}
