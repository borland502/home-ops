package com.technohouser.commands.ansible;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.technohouser.model.HostDto;
import com.technohouser.service.ExecService;
import com.technohouser.service.ExecService.ShellVar;
import com.technohouser.service.HostsService;
import com.technohouser.service.SecretsService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import java.util.concurrent.Callable;

@Slf4j
@ShellComponent
public class AnsibleSyncHostInfo extends AbstractAnsibleCommand implements Callable<String> {

  private final ExecService execService;
  private final HostsService hostsService;
  private final SecretsService secretsService;
  private final ObjectMapper objectMapper;

  public AnsibleSyncHostInfo(ExecService execService, HostsService hostsService, SecretsService secretsService,
      ObjectMapper objectMapper) {
    super(execService, hostsService, secretsService);
    this.execService = execService;
    this.hostsService = hostsService;
    this.secretsService = secretsService;
    this.objectMapper = objectMapper;
  }

  @ShellMethod(value = "Sync host information", key = "sync host info", group = "sync")
  public String call() throws Exception {
    return "Host information synced successfully";
  }
}
