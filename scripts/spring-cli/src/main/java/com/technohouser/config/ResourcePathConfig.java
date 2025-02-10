package com.technohouser.config;

import java.nio.file.Path;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.FileSystemResource;

@Slf4j
@Configuration
public class ResourcePathConfig implements InitializingBean {

  public final Environment environment;

  @Value("${XDG_DATA_HOME}")
  private Path XDG_DATA_HOME;

  @Value("${XDG_CONFIG_HOME}")
  private Path XDG_CONFIG_HOME;

  public ResourcePathConfig(Environment environment) {
    this.environment = environment;
  }

  public Path getDataRoot() {
    return XDG_DATA_HOME.resolve("automation/home-ops");
  }

  public Path getConfigRoot() {
    return XDG_CONFIG_HOME.resolve("home-ops");
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    log.info("XDG_DATA_HOME: {}", XDG_DATA_HOME);
    log.info("XDG_CONFIG_HOME: {}", XDG_CONFIG_HOME);
  }

  FileSystemResource fromTomlPath(Path path) {
    return new FileSystemResource(getConfigRoot().resolve(path));
  }

}
