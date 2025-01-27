package com.technohouser.config.properties.toml;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "brew")
public class BrewProperties {
  private List<String> packages;
}
