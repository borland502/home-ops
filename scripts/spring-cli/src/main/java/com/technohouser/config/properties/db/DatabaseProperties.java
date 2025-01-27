package com.technohouser.config.properties.db;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import java.time.Duration;
import java.util.List;

@Data
@ConfigurationProperties(prefix = "db")
@Component
public class DatabaseProperties {

  private Duration propertyRefreshInterval;

  private List<PropertySource> propertySources;

  public static class PropertySource {
    private String table;
  }

}
