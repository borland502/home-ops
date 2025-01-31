package com.technohouser.commands.sync;

import com.technohouser.service.ExecService;
import com.technohouser.utils.DefaultPaths.HomeOpsPaths;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import java.util.concurrent.Callable;

@ShellComponent
public class SyncHostInfo implements Callable<String> {

  private final ExecService execService;

  public SyncHostInfo(ExecService execService) {
    this.execService = execService;
  }

  private ProcessBuilder syncHostInfo() {
    return execService.exec("sudo", "npx", "systeminformation")
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
