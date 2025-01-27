package com.technohouser.commands.sync;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import utils.DefaultPaths.HomeOpsPaths;
import java.util.concurrent.Callable;

@ShellComponent
public class SyncHostInfo implements Callable<String> {

  private ProcessBuilder syncHostInfo() {
    return utils.Exec.buildProcess("sudo", "npx", "systeminformation")
        .directory(HomeOpsPaths.HOME_OPS_CONFIG_PATH.getPath().toFile())
        .redirectOutput(HomeOpsPaths.HOME_OPS_CONFIG_PATH.getPath().resolve("host.json").toFile());
  }

  @ShellMethod(value = "Sync host information", key = "sync host info", group = "sync")
  @Override
  public String call() throws Exception {
    if (syncHostInfo().start().waitFor() != 0) {
      return "Failed to sync host information";
    }
    return "Successfully synced host information";
  }

}
