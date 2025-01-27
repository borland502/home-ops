package com.technohouser.commands.dasbootstrap;

import com.technohouser.config.properties.toml.AptProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.shell.context.InteractionMode;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import java.lang.ProcessBuilder.Redirect;
import java.util.concurrent.Callable;

@Slf4j
@ShellComponent
public class SystemInstall implements Callable<String> {

  private final AptProperties aptProperties;

  public SystemInstall(AptProperties aptProperties) {
    this.aptProperties = aptProperties;
  }

private ProcessBuilder installSystemPackages() throws Exception {
  // Check if apt is available
  Process checkApt = new ProcessBuilder("which", "apt").start();
  if (checkApt.waitFor() != 0) {
    throw new Exception("apt package manager is not available");
  }

  return utils.Exec.buildProcess(
      "bash",
      "-c",
      "sudo apt-get -y update && sudo apt-get -y install "
          + String.join(" ", aptProperties.getPackages()))
      .redirectOutput(Redirect.INHERIT).redirectError(Redirect.INHERIT);
}

  @ShellMethod(value = "Update and Install Apt packages", key = {"install apt", "apt"}, group = "dasbootstrap", interactionMode = InteractionMode.INTERACTIVE)
  @Override
  public String call() throws Exception {
    if(installSystemPackages().start().waitFor() != 0) {
      return "Failed to install packages";
    }
    return "Successfully installed packages";
  }
}
