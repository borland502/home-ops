package com.technohouser.jobs;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import com.technohouser.config.JobsConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.technohouser.model.HostDto;
import com.technohouser.service.HostsService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class HostExportJob extends JobsConfig {

  private final HostsService hostsService;
  private final ObjectMapper objectMapper;

  @Value("${assets.hosts.path}")
  private Path staticHostPath;

  public HostExportJob(HostsService hostsService, ObjectMapper objectMapper) {
    this.hostsService = hostsService;
    this.objectMapper = objectMapper;
  }

  @Async
  @Scheduled(initialDelay = 3000, fixedRate = 1000 * 60 * 12)
  public void exportHosts() {
    List<HostDto> hosts = hostsService.getAllHosts();
    try {
      objectMapper.writeValue(staticHostPath.toFile(), hosts);
    } catch (IOException e) {
      log.error("Failed to export hosts", e);
    }
  }
}

