package com.technohouser.commands.ansible;

import java.nio.file.Path;

import com.technohouser.service.ExecService;
import com.technohouser.service.HostsService;
import com.technohouser.service.SecretsService;

public interface AnsibleCommand {

  ExecService execService = null;
  HostsService hostsService = null;
  SecretsService secretsService = null;

  enum ANSIBLE_INVENTORIES {
    ALL(Path.of(System.getProperty("user.home"), ".ansible", "inventory")),
    STATIC_INVENTORY(Path.of(System.getProperty("user.home"), ".ansible", "inventory", "hosts.json"));

    private final Path inventory;

    ANSIBLE_INVENTORIES(Path inventory) {
      this.inventory = inventory;
    }

    public Path getInventory() {
      return inventory;
    }
  }

  enum ANSIBLE_EXECUTABLES {
    ANSIBLE_PLAYBOOK("ansible-playbook"),
    ANSIBLE("ansible"),
    ANSIBLE_GALAXY("ansible-galaxy"),
    ANSIBLE_INVENTORY("ansible-inventory"),
    ANSIBLE_VAULT("ansible-vault");

    private final String executable;

    ANSIBLE_EXECUTABLES(String executable) {
      this.executable = executable;
    }

    public String getExecutable() {
      return executable;
    }
  }

  ProcessBuilder getCommand(ANSIBLE_EXECUTABLES executable, String... args);

  ProcessBuilder getCommand(ANSIBLE_EXECUTABLES executable, Path inventory, String... args);

  ProcessBuilder getCommand(ANSIBLE_EXECUTABLES executable, Path inventory, String playbook, String... args);

  ProcessBuilder getCommand(ANSIBLE_EXECUTABLES executable, Path inventory, String playbook, String user,
      String... args);
}
