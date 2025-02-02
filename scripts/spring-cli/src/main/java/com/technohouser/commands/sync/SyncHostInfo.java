package com.technohouser.commands.sync;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.technohouser.model.HostDto;
import com.technohouser.service.ExecService;
import com.technohouser.service.ExecService.ShellVar;
import com.technohouser.service.HostsService;
import java.util.concurrent.Callable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

@Slf4j
@ShellComponent
public class SyncHostInfo implements Callable<String> {

  private final ExecService execService;
  private final HostsService hostsService;
  private final ObjectMapper objectMapper;

  public SyncHostInfo(ExecService execService, HostsService hostsService,
      ObjectMapper objectMapper) {
    this.execService = execService;
    this.hostsService = hostsService;
    this.objectMapper = objectMapper;
  }

  private ProcessBuilder getHostInfo() throws Exception {
    return execService.exec(ShellVar.no_command, "npx", "systeminformation");
  }

  @ShellMethod(value = "Sync host information", key = "sync host info", group = "sync")
  public String call() throws Exception {
    log.info("Syncing host information");

    Process process = getHostInfo().start();
    HostDto hostDto = objectMapper.readValue(process.getInputStream(), HostDto.class);
    hostsService.updateHostInfo(hostDto);

    if (process.waitFor() != 0) {
      throw new RuntimeException("Failed to get host information");
    }

    return "Host information synced successfully";

  }
}
