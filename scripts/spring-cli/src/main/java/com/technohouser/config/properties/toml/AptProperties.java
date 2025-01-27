package com.technohouser.config.properties.toml;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import java.util.List;

@Data
@Configuration
@ConfigurationProperties(prefix = "apt")
public class AptProperties {

  private List<String> packages;


}
