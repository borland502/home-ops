package com.technohouser.jobs.exports;

import com.technohouser.repository.HostRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class HostExportJob {

  private final HostRepository hostRepository;

  public HostExportJob(HostRepository hostRepository) {
    this.hostRepository = hostRepository;
  }

  @Async
  @Scheduled(fixedRate = 1000 * 60 * 3)
  public void saveSecretsIfDirty() {

  }

}
