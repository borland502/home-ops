package com.technohouser;

import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ConfigurableBootstrapContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.shell.command.annotation.CommandScan;

@Slf4j
@EnableScheduling
@EnableAsync
@EnableCaching
@EnableConfigurationProperties
@CommandScan(basePackages = "com.technohouser.command")
@EnableJpaRepositories(basePackages = "com.technohouser.repository")
@EntityScan(basePackages = "com.technohouser.entities")
@SpringBootApplication(scanBasePackages = "com.technohouser")
public class HomeOpsApplication implements SpringApplicationRunListener {

  public static void main(String[] args) {
    SpringApplication application = new SpringApplicationBuilder(HomeOpsApplication.class)
        .addCommandLineProperties(true)
        .build();
    application.run(args);
  }

  @Override
  public void starting(ConfigurableBootstrapContext bootstrapContext) {
    log.debug("Starting HomeOpsApplication");
    SpringApplicationRunListener.super.starting(bootstrapContext);
  }

  @Override
  public void ready(ConfigurableApplicationContext context, Duration timeTaken) {
    log.info("HomeOpsApplication is ready");
    SpringApplicationRunListener.super.ready(context, timeTaken);
  }

  @Override
  public void failed(ConfigurableApplicationContext context, Throwable exception) {
    log.error("HomeOpsApplication failed to start", exception);
    System.exit(2);
  }

}
