package com.technohouser.commands.export;

import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.toml.TomlWriter;
import com.technohouser.model.HostDto;
import com.technohouser.service.HostsService;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.concurrent.Callable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

@Slf4j
@ShellComponent
public class ExportHostsInfo implements Callable<String> {

  private final HostsService hostsService;
  private final ObjectMapper objectMapper = new ObjectMapper();

  @Value("${assets.hosts.path}")
  private Path assetsHostsPath;

  public ExportHostsInfo(HostsService hostsService) {
    this.hostsService = hostsService;
  }

  @ShellMethod(key = "export hosts info", group = "export", value = "Exports hosts info as TOML")
  public String exportHostsInfo() throws Exception {
    return call();
  }

  // Add helper method to recursively convert Maps to Configs
  private Object convertValue(Object val) {
    if (val instanceof Map) {
      com.electronwill.nightconfig.core.Config config = com.electronwill.nightconfig.core.Config.inMemory();
      ((Map<?, ?>) val).forEach((k, v) -> config.set(k.toString(), convertValue(v)));
      return config;
    } else if (val instanceof List) {
      return ((List<?>) val).stream()
          .map(this::convertValue)
          .collect(Collectors.toList());
    }
    return val;
  }

  @Override
  public String call() throws Exception {
    List<HostDto> allHosts = hostsService.getAllHosts();
    log.info("All hosts info: {}", allHosts);

    // Convert HostDto objects to a Config instance for each host.
    List<com.electronwill.nightconfig.core.Config> hostConfigs = allHosts.stream()
      .map(host -> {
          // Convert to Map
          Map<String, Object> hostMap = objectMapper.convertValue(host, new TypeReference<Map<String, Object>>() {});
          // Create a new Config and set each entry
          com.electronwill.nightconfig.core.Config cfg = com.electronwill.nightconfig.core.Config.inMemory();
          hostMap.forEach((k, v) -> cfg.set(k, convertValue(v)));
          return cfg;
      })
      .collect(Collectors.toList());

    TomlWriter writer = new TomlWriter();
    // Ensure the directory exists
    try (Writer fileWriter = Files.newBufferedWriter(assetsHostsPath)) {
      // Write the list of hosts as a TOML array of tables
      com.electronwill.nightconfig.core.Config config = com.electronwill.nightconfig.core.Config.inMemory();
      config.set("hosts", hostConfigs);
      writer.write(config, fileWriter);
    }
    return "Exported to hosts db to " + assetsHostsPath;
  }
}
