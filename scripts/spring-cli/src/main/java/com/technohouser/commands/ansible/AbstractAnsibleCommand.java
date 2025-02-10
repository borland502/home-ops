package com.technohouser.commands.ansible;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.technohouser.service.ExecService;
import com.technohouser.service.HostsService;
import com.technohouser.service.SecretsService;

public abstract class AbstractAnsibleCommand implements AnsibleCommand {

  protected final ExecService execService;
  protected final HostsService hostsService;
  protected final SecretsService secretsService;

  protected AbstractAnsibleCommand(ExecService execService, HostsService hostsService, SecretsService secretsService) {
    this.execService = execService;
    this.hostsService = hostsService;
    this.secretsService = secretsService;
  }

  @Override
  public ProcessBuilder getCommand(ANSIBLE_EXECUTABLES executable, String... args) {
    List<String> command = new ArrayList<>();
    command.add(executable.getExecutable());
    command.addAll(Arrays.asList(args));
    return new ProcessBuilder(command);
  }

  @Override
  public ProcessBuilder getCommand(ANSIBLE_EXECUTABLES executable, Path inventory, String... args) {
    List<String> command = new ArrayList<>();
    command.add(executable.getExecutable());
    command.add("-i");
    command.add(inventory.toString());
    command.addAll(Arrays.asList(args));
    return new ProcessBuilder(command);
  }

  @Override
  public ProcessBuilder getCommand(ANSIBLE_EXECUTABLES executable, Path inventory, String playbook, String... args) {
    List<String> command = new ArrayList<>();
    command.add(executable.getExecutable());
    command.add("-i");
    command.add(inventory.toString());
    command.add(playbook);
    command.addAll(Arrays.asList(args));
    return new ProcessBuilder(command);
  }

  @Override
  public ProcessBuilder getCommand(ANSIBLE_EXECUTABLES executable, Path inventory, String playbook, String user,
      String... args) {
    List<String> command = new ArrayList<>();
    command.add(executable.getExecutable());
    command.add("-i");
    command.add(inventory.toString());
    if (user != null && !user.isEmpty()) {
      command.add("-u");
      command.add(user);
    }
    command.add(playbook);
    command.addAll(Arrays.asList(args));
    return new ProcessBuilder(command);
  }
}
