///usr/bin/env jbang --quiet "$0" "$@" ; exit $?
//JAVA 21+
//DEPS org.projectlombok:lombok:1.18.36
//DEPS ch.qos.logback:logback-classic:1.5.16
//DEPS com.electronwill.night-config:toml:3.8.1
//DEPS com.google.guava:guava:30.1-jre
//DEPS org.eclipse.jgit:org.eclipse.jgit:7.1.0.202411261347-r
//DEPS info.picocli:picocli:4.6.3
//SOURCES utils/Exec.java
//SOURCES utils/Assets.java
//SOURCES utils/DefaultPaths.java
//JAVAC_OPTIONS -proc:full

import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.List;
import java.util.concurrent.Callable;

import com.electronwill.nightconfig.core.concurrent.SynchronizedConfig;
import com.electronwill.nightconfig.core.file.FileConfig;
import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import utils.Assets;
import utils.DefaultPaths.HomeOpsPaths;
import utils.Exec;

import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;

/**
 *
 */
@Slf4j
@Command(name = "BootstrapRunner", mixinStandardHelpOptions = true, version = "BootstrapRunner 0.1", description = "BootstrapRunner made with jbang")
class BootstrapRunner implements Callable<Integer> {

  private static FileConfig config;

  static {
    try {
      config = Assets.Config.HOME_OPS.getTomlConfig();
      config.load();
    } catch (IOException | InterruptedException e) {
      log.error("Error initializing configuration", e);
      System.exit(2);
    }
  }

  public static void main(String... args) {
    int exitCode = new CommandLine(new BootstrapRunner()).execute(args);
    System.exit(exitCode);
  }

  @Override
  public Integer call() throws Exception {
    List<ProcessBuilder> processBuilders = Lists.newArrayList();
    processBuilders.add(loadEnvVariables());
    processBuilders.add(installBrew());
    processBuilders.add(syncHostInfo());
    processBuilders.add(installSystemPackages());
    processBuilders.add(installBrewPackages());
    processBuilders.add(installKeePassXC());
    processBuilders.add(installChezmoi());
    boolean allProcessesExitedSuccessfully = processBuilders
        .stream().mapToInt(process -> {
          try {
            return process.start().waitFor();
          } catch (InterruptedException | IOException e) {
            log.error("Error waiting for process to exit", e);
            return 2;
          }
        }).allMatch(exitValue -> exitValue == 0);

    if (!allProcessesExitedSuccessfully) {
      log.error("One or more processes exited with a non-zero exit code");
      return 2;
    }

    log.info("All processes exited successfully");

    return 0;
  }

  private ProcessBuilder loadEnvVariables() {
    SynchronizedConfig envVars = config.get("bootstrap.constants");

    envVars.entrySet().forEach(e -> {
      String key = e.getKey();
      String value = e.getValue().toString();
      Path envPath = Path.of(System.getProperty("user.home"), ".env");
      String entry = key + "=" + "\"" + value + "\"" + System.lineSeparator();
      try {
        Files.writeString(envPath, entry, CREATE, APPEND);
      } catch (IOException e1) {
        log.error("Error writing to env file", e1);
      }
      System.setProperty(key, value);
    });

    return Exec.buildProcess("bash", new String[] { "-c", "env" })
        .redirectOutput(Redirect.INHERIT)
        .redirectError(Redirect.INHERIT);
  }

  private ProcessBuilder installKeePassXC() {
    String osName = System.getProperty("os.name").toLowerCase();
    if (osName.contains("mac")) {
      try {
        Path tempDir = Files.createTempDirectory("keepassxc");
        Path dmgPath = tempDir.resolve("KeePassXC.dmg");
        Path digestPath = tempDir.resolve("KeePassXC.dmg.DIGEST");

        // Download DMG and digest files
        HttpClient client = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.ALWAYS).build();
        client.send(HttpRequest.newBuilder()
            .uri(URI.create(
                "https://github.com/keepassxreboot/keepassxc/releases/download/2.7.9/KeePassXC-2.7.9-arm64.dmg"))
            .GET().build(), BodyHandlers.ofFile(dmgPath));
        client.send(HttpRequest.newBuilder()
            .uri(URI.create(
                "https://github.com/keepassxreboot/keepassxc/releases/download/2.7.9/KeePassXC-2.7.9-arm64.dmg.DIGEST"))
            .GET().build(), BodyHandlers.ofFile(digestPath));

        // Verify SHA-256 hash
        String command = String.format("cd %s && shasum -a 256 -c %s && " +
            "hdiutil attach %s && " +
            "sudo cp -R /Volumes/KeePassXC/KeePassXC.app /Applications/ && " +
            "hdiutil detach /Volumes/KeePassXC",
            tempDir, digestPath.getFileName(), dmgPath.getFileName());

        return Exec.buildProcess("bash", "-c", command)
            .redirectOutput(Redirect.INHERIT)
            .redirectError(Redirect.INHERIT);
      } catch (IOException | InterruptedException e) {
        log.error("Error installing KeePassXC on MacOS", e);
        return new ProcessBuilder().inheritIO();
      }
    } else {
      return Exec.buildProcess(
          "bash",
          "-c",
          "DISTRO=$(jq -r .os.distro ~/.config/home-ops/host.json); " +
              "if [[ \"$DISTRO\" == *\"Debian\"* ]]; then " +
              "sudo apt-get -y update && sudo apt-get -y install keepassxc; " +
              "else " +
              "echo \"Unsupported distro: $DISTRO\"; " +
              "fi")
          .redirectOutput(Redirect.INHERIT)
          .redirectError(Redirect.INHERIT);
    }
  }

  private ProcessBuilder installChezmoi() {
    try {
      String xdgDataHome = System.getenv("XDG_DATA_HOME");
      if (xdgDataHome == null) {
        xdgDataHome = System.getProperty("user.home") + "/.local/share";
      }
      Path dotfilesPath = Path.of(xdgDataHome, "automation", "home-ops", "scripts", "dotfiles");
      Files.createDirectories(dotfilesPath);

      String command = String.format("touch %s/.env && chezmoi init --source %s && chezmoi apply --source %s",
          System.getProperty("user.home"),
          dotfilesPath,
          dotfilesPath);

      return Exec.buildProcess("bash", "-c", command)
          .redirectOutput(Redirect.INHERIT)
          .redirectError(Redirect.INHERIT);
    } catch (IOException e) {
      log.error("Error creating directories for chezmoi", e);
      return new ProcessBuilder().inheritIO();
    }
  }

  private ProcessBuilder installSystemPackages() {
    List<String> aptPackages = config.get("apt.packages");
    return Exec.buildProcess(
        "bash",
        "-c",
        "DISTRO=$(jq -r .os.distro ~/.config/home-ops/host.json); if [[ \"$DISTRO\" == *\"Debian\"* ]]; then sudo apt-get -y update && sudo apt-get -y install "
            + String.join(" ", aptPackages) + "; else echo \"Unsupported distro: $DISTRO\"; fi")
        .redirectOutput(Redirect.INHERIT).redirectError(Redirect.INHERIT);
  }

  private ProcessBuilder installBrewPackages() {
    // Install Homebrew packages
    try {
      Path zshrcPath = Path.of(System.getProperty("user.home"), "/.zshrc");
      String osName = System.getProperty("os.name").toLowerCase();
      String brewPath = osName.contains("mac") ? "/opt/homebrew/bin/brew" : "/home/linuxbrew/.linuxbrew/bin/brew";

      Files.writeString(zshrcPath,
          "\neval \"$(" + brewPath + " shellenv)\"\n",
          APPEND, CREATE);

      List<String> brewPackages = config.get("brew.packages");
      List<String> arguments = new java.util.ArrayList<>();
      arguments.add("-c");
      arguments.add(
          "source " + zshrcPath + " && brew update && brew upgrade && brew install " + String.join(" ", brewPackages));

      return Exec.buildProcess("bash", arguments.toArray(new String[0]))
          .redirectOutput(Redirect.INHERIT)
          .redirectError(Redirect.INHERIT);
    } catch (IOException | ClassCastException e) {
      log.error("Error installing brew packages", e);
      return new ProcessBuilder().inheritIO();
    }
  }

  private ProcessBuilder installBrew() {
    // Install Homebrew
    try {
      Path tempFile = Files.createTempFile("brew", ".sh", new FileAttribute<?>[0]);
      HttpRequest request = HttpRequest.newBuilder()
          .uri(URI.create("https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh"))
          .GET()
          .build();
      HttpClient client = HttpClient.newBuilder()
          .followRedirects(HttpClient.Redirect.ALWAYS)
          .build();
      client.send(request, BodyHandlers.ofFile(tempFile));

      Files.setPosixFilePermissions(tempFile, PosixFilePermissions.fromString("rwxr-xr-x"));
      return Exec.buildProcess("bash", "-c", tempFile.toAbsolutePath().toString())
          .redirectOutput(Redirect.INHERIT)
          .redirectError(Redirect.INHERIT);
    } catch (IOException | InterruptedException e) {
      log.error("Error during Homebrew installation", e);
      return new ProcessBuilder().inheritIO(); // Return an empty ProcessBuilder on error
    }
  }

  private ProcessBuilder syncHostInfo() {
    // Write system information to inventory.json using systeminformation npm
    // library
    ProcessBuilder processBuilder = Exec.buildProcess("npx", "systeminformation")
        .directory(HomeOpsPaths.HOME_OPS_CONFIG_PATH.getPath().toFile())
        .redirectOutput(HomeOpsPaths.HOME_OPS_CONFIG_PATH.getPath().resolve("host.json").toFile());

    return processBuilder;

  }

}
