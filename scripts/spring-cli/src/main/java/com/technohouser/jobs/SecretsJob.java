package com.technohouser.jobs;

import com.technohouser.config.JobsConfig;
import com.technohouser.service.SecretsService;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SecretsJob extends JobsConfig {

  private final SecretsService secretsService;

  public SecretsJob(SecretsService secretsService) {
    this.secretsService = secretsService;
  }

  @Async
  @Scheduled(fixedRate = 1000 * 60 * 3)
  public void saveSecretsIfDirty() {
    if (secretsService.isDirty()) {
      secretsService.saveDatabase();
    }
  }
}
