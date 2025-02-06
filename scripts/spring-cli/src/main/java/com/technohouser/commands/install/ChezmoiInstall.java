package com.technohouser.commands.install;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.Callable;
import com.technohouser.service.ExecService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

@Slf4j
@ShellComponent
public class ChezmoiInstall implements Callable<String> {

  private final ExecService execService;

  public ChezmoiInstall(ExecService execService) {
    this.execService = execService;
  }

  private ProcessBuilder chezmoiCheck() {
    return execService.rawExec("chezmoi", "--version").redirectOutput(
        ProcessBuilder.Redirect.INHERIT).redirectError(ProcessBuilder.Redirect.INHERIT);
  }

  private ProcessBuilder chezmoiInstall() {
    return execService.rawExec("bash", "-c", "curl -sfL https://git.io/chezmoi | sh").redirectOutput(
        ProcessBuilder.Redirect.INHERIT).redirectError(ProcessBuilder.Redirect.INHERIT);
  }

  private ProcessBuilder chezmoiInit() {
    return execService.rawExec("chezmoi", "init").redirectOutput(ProcessBuilder.Redirect.INHERIT)
        .redirectError(ProcessBuilder.Redirect.INHERIT);
  }

  private ProcessBuilder chezmoiApply() {
    return execService.rawExec("chezmoi", "apply").redirectOutput(ProcessBuilder.Redirect.INHERIT)
        .redirectError(ProcessBuilder.Redirect.INHERIT);
  }

  private ProcessBuilder cloneRepository(Path dotfilesPath) {
    if (Files.exists(dotfilesPath)) {
      log.info("Repository already exists at {}", dotfilesPath);
      return execService.rawExec("echo", "Repository already exists at " + dotfilesPath);
    }
    return execService.rawExec("bash", "-c",
        String.format("git clone --recurse-submodules https://github.com/borland502/home-ops %s",
            dotfilesPath)).redirectOutput(ProcessBuilder.Redirect.INHERIT)
        .redirectError(ProcessBuilder.Redirect.INHERIT);
  }

  @ShellMethod(value = "Install chezmoi and apply dotfiles", key = "install chezmoi", group = "dasbootstrap")
  @Override
  public String call() throws Exception {
    String xdgDataHome = System.getenv("XDG_DATA_HOME");
    if (xdgDataHome == null) {
      xdgDataHome = System.getProperty("user.home") + "/.local/share";
    }
    Path dotfilesPath = Path.of(xdgDataHome, "automation", "home-ops", "scripts", "dotfiles");
    Files.createDirectories(Path.of(xdgDataHome, "automation"));

    List<ProcessBuilder> processBuilders = List.of(cloneRepository(dotfilesPath), chezmoiCheck(),
        chezmoiInstall(), chezmoiInit(), chezmoiApply());

    List<Integer> exitCodes = processBuilders.stream().map(pb -> {
      try {
        return pb.start().waitFor();
      } catch (Exception e) {
        log.error("Error executing process", e);
        return -1;
      }
    }).toList();

    if (exitCodes.stream().reduce(0, Integer::sum) != 0) {
      return "One or more processes failed during the chezmoi installation";
    }

    return "Successfully installed chezmoi and applied dotfiles";
  }

}
