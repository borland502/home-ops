package com.technohouser.config.properties.toml;

import lombok.Data;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Configuration;
import java.util.List;

@Data
@Configuration
@ConfigurationProperties(prefix = "apt")
public class AptProperties {

  private List<String> packages;

  @Cacheable(value = "aptPackages")
  public List<String> getPackages() {
    return packages;
  }

}
