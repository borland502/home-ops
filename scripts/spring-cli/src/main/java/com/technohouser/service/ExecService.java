package com.technohouser.service;

import com.google.common.collect.Lists;
import com.technohouser.utils.DefaultPaths;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.apache.logging.log4j.util.Strings;
import org.springframework.context.Phased;
import org.springframework.context.SmartLifecycle;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service()
@DependsOn("environment")
public class ExecService implements Phased, SmartLifecycle {

  private final Environment environment;

  public ExecService(Environment environment) {
    this.environment = environment;
  }

  public enum ShellVar {
    zsh, bash, sh
  }

  /**
   * We want this service to run almost as early as possible in the spring boot sequence, but only
   * after the environment has been set up.
   *
   * @return Integer.MIN_VALUE
   */
  @Override
  public int getPhase() {
    return Integer.MIN_VALUE;
  }

  public ProcessBuilder exec(String command, String... args) {
    // Validate that the command is existing and is executable
    if (Strings.isBlank(command)) {
      throw new IllegalArgumentException("Command cannot be null or empty");
    }

    List<String> statement = Lists.asList(command, args);

    // By default, dump all environment variables into the process
    Map<String, String> envVars = System.getenv();
    ProcessBuilder pb = new ProcessBuilder(statement);
    pb.environment().putAll(envVars);

    // assume shell is in path
    pb.command(String.valueOf(statement.addAll(0, List.of(getShell().name(), "-c"))));
    return pb;
  }

  /**
   * Get the shell that is currently being used by the system. This is determined by the SHELL
   * environment variable.
   *
   * @return ShellVar zsh, bash, or sh
   */
  public ShellVar getShell() {
    Path shellPath = Path.of(Objects.requireNonNull(this.environment.getProperty("SHELL")));

    if (!shellPath.toFile().exists() && !shellPath.toFile().canExecute()) {
      throw new IllegalArgumentException(
          "Shell path does not exist or is not executable: " + shellPath);
    }

    ShellVar shellVar = ShellVar.valueOf(shellPath.getFileName().toString().toLowerCase());

    return switch (shellVar) {
      case zsh -> ShellVar.zsh;
      case bash -> ShellVar.bash;
      case sh -> ShellVar.sh;
    };

  }

  @Override
  public void start() {
    // validate Path variables and create Directories
    DefaultPaths.ensurePaths();


  }

  @Override
  public void stop() {

  }

  @Override
  public boolean isRunning() {
    return false;
  }
}
