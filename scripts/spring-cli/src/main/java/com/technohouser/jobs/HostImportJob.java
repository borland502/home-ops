package com.technohouser.jobs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.technohouser.config.JobsConfig;
import com.technohouser.model.HostDto;
import com.technohouser.service.HostsService;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class HostImportJob extends JobsConfig {

  private final HostsService hostsService;
  private static final String DIRECTORY_PATH =
      System.getenv("XDG_DATA_HOME") + "/automation/home-db/json-intake";

  public HostImportJob(HostsService hostsService) {
    this.hostsService = hostsService;
  }

  @Async
  @Scheduled(fixedRate = 1000 * 60 * 12)
  public void importHostsFromJson() {
    try (Stream<Path> paths = Files.walk(Paths.get(DIRECTORY_PATH))) {
      paths.filter(Files::isRegularFile).filter(path -> path.toString().endsWith(".json"))
          .forEach(this::processJsonFile);
    } catch (IOException e) {
      log.error("Failed to import hosts from JSON", e);
    }
  }

  private void processJsonFile(Path path) {
    try (FileInputStream inputStream = new FileInputStream(path.toFile())) {
      HostDto hostDto = new ObjectMapper().readValue(inputStream, HostDto.class);
      hostsService.updateHostInfo(hostDto);
      Files.delete(path); // Optionally delete the file after processing
    } catch (IOException e) {
      log.error("Failed to process JSON file: {}", path, e);
    }
  }
}
