package com.technohouser.commands.dasbootstrap;

import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;
import com.technohouser.config.properties.json.HostProperties;
import com.technohouser.config.properties.toml.BrewProperties;
import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.List;
import java.util.concurrent.Callable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.shell.context.InteractionMode;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

@Slf4j
@ShellComponent
public class BrewInstall implements Callable<String> {

  private final HostProperties hostProperties;
  private final BrewProperties brewProperties;

  public BrewInstall(HostProperties hostProperties, BrewProperties brewProperties) {
    this.hostProperties = hostProperties;
    this.brewProperties = brewProperties;
  }

  private ProcessBuilder installBrew() {
    // Install Homebrew
    try {
      Path tempFile = Files.createTempFile("brew", ".sh");
      HttpRequest request = HttpRequest.newBuilder()
          .uri(URI.create("https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh"))
          .GET().build();
      try (HttpClient client = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.ALWAYS)
          .build()) {
        client.send(request, BodyHandlers.ofFile(tempFile));
      }

      Files.setPosixFilePermissions(tempFile, PosixFilePermissions.fromString("rwxr-xr-x"));
      return utils.Exec.buildProcess("bash", "-c", tempFile.toAbsolutePath().toString())
          .redirectOutput(Redirect.INHERIT).redirectError(Redirect.INHERIT);
    } catch (IOException | InterruptedException e) {
      log.error("Error during Homebrew installation", e);
      return new ProcessBuilder().inheritIO(); // Return an empty ProcessBuilder on error
    }
  }

  private ProcessBuilder installBrewPackages() {
    // Install Homebrew packages
    try {
      Path zshrcPath = Path.of(System.getProperty("user.home"), "/.zshrc");
      String osName = hostProperties.getOs().getPlatform();
      String brewPath;
      if (osName.contains("darwin")) {
        brewPath = "/opt/homebrew/bin/brew";
      } else {
        Path homeLinuxbrew = Path.of("/home/linuxbrew/.linuxbrew/bin/brew");
        Path userHomeLinuxbrew = Path.of(System.getProperty("user.home"), ".linuxbrew/bin/brew");

        if (Files.exists(homeLinuxbrew)) {
          brewPath = homeLinuxbrew.toString();
        } else if (Files.exists(userHomeLinuxbrew)) {
          brewPath = userHomeLinuxbrew.toString();
        } else {
          throw new IOException("Could not find Homebrew installation");
        }
      }

      Files.writeString(zshrcPath, "\neval \"$(" + brewPath + " shellenv)\"\n", APPEND, CREATE);

      List<String> brewPackages = brewProperties.getPackages();
      List<String> arguments = new java.util.ArrayList<>();
      arguments.add("-c");
      arguments.add(
          "source " + zshrcPath + " && brew update && brew upgrade && brew install " + String.join(
              " ", brewPackages));

      return utils.Exec.buildProcess("bash", arguments.toArray(new String[0]))
          .redirectOutput(Redirect.INHERIT).redirectError(Redirect.INHERIT);
    } catch (IOException | ClassCastException e) {
      log.error("Error installing brew packages", e);
      return new ProcessBuilder().inheritIO();
    }
  }

  @ShellMethod(value = "Install brew and brew packages", key = {
      "install brew","brew"}, group = "dasbootstrap", interactionMode = InteractionMode.INTERACTIVE)
  @Override
  public String call() throws Exception {
    try {
      ProcessBuilder checkBrew = utils.Exec.buildProcess("brew", "--version");
      if (checkBrew.start().waitFor() != 0) {
        if (installBrew().start().waitFor() != 0) {
          throw new Exception("Failed to install Homebrew");
        }
      }
    } catch (IOException | InterruptedException e) {
      throw new Exception("Error checking or installing Homebrew", e);
    }
    if (installBrewPackages().start().waitFor() != 0) {
      return "Failed to install packages";
    }
    return "Successfully installed packages";
  }

}
