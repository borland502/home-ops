package com.technohouser.apps;

import com.technohouser.config.properties.toml.AptProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.shell.standard.ShellComponent;

/**
 *
 */
@Slf4j
@ShellComponent
class Dasbootstrap {


  private final AptProperties aptProperties;

  Dasbootstrap(AptProperties aptProperties) {
    this.aptProperties = aptProperties;
  }

  //private static FileConfig config;

  //static {
  //  try {
  //    config = Assets.Config.HOME_OPS.getTomlConfig();
  //    config.load();
  //  } catch (IOException | InterruptedException e) {
  //    log.error("Error initializing configuration", e);
  //    System.exit(2);
  //  }
  //}

  //@Override
  //public Integer call() throws Exception {
  //  List<ProcessBuilder> processBuilders = Lists.newArrayList();
  //  processBuilders.add(installBrew());
  //  processBuilders.add(syncHostInfo());
  //  processBuilders.add(installSystemPackages());
  //  processBuilders.add(installBrewPackages());
  //  processBuilders.add(uploadKeepassDb());
  //  processBuilders.add(configureChezmoi());
  //  boolean allProcessesExitedSuccessfully = processBuilders.reversed()
  //      .stream().mapToInt(process -> {
  //        try {
  //          Process p = process.start();
  //          if (!p.waitFor(1, TimeUnit.MINUTES)) {
  //            p.destroyForcibly();
  //            log.error("Process timed out after 1 minute");
  //            return 2;
  //          }
  //          return p.exitValue();
  //        } catch (InterruptedException | IOException e) {
  //          log.error("Error waiting for process to exit", e);
  //          return 2;
  //        }
  //      }).allMatch(exitValue -> exitValue == 0);
  //
  //  if (!allProcessesExitedSuccessfully) {
  //    log.error("One or more processes exited with a non-zero exit code");
  //    return 2;
  //  }
  //
  //  log.info("All processes exited successfully");
  //
  //  return 0;
  //}

  //private ProcessBuilder uploadKeepassDb() {
  //  log.info("Please upload your KeePass database and token using the file upload utility...");
  //  ProcessBuilder uploader = utils.Exec.buildProcess("jbang", "UploadFileApp.java")
  //      .redirectOutput(Redirect.INHERIT)
  //      .redirectError(Redirect.INHERIT);
  //
  //  log.info(
  //      "The upload servlet for keepass db / keepass token is listening on the target server, port 7080.  CTRL+C to exit");
  //
  //  return uploader;
  //}

  // TODO: Need to have transferred the keepass db and token to the host prior to
  // this stage
  //private ProcessBuilder configureChezmoi() {
  //  try {
  //    String xdgDataHome = System.getenv("XDG_DATA_HOME");
  //    if (xdgDataHome == null) {
  //      xdgDataHome = System.getProperty("user.home") + "/.local/share";
  //    }
  //    Path dotfilesPath = Path.of(xdgDataHome, "automation", "home-ops", "scripts", "dotfiles");
  //    Files.createDirectories(dotfilesPath);
  //
  //    String command = String.format("touch %s/.env && chezmoi init --source %s && chezmoi apply --source %s",
  //        System.getProperty("user.home"),
  //        dotfilesPath,
  //        dotfilesPath);
  //
  //    return utils.Exec.buildProcess("bash", "-c", command)
  //        .redirectOutput(Redirect.INHERIT)
  //        .redirectError(Redirect.INHERIT);
  //  } catch (IOException e) {
  //    log.error("Error creating directories for chezmoi", e);
  //    return new ProcessBuilder().inheritIO();
  //  }
  //}



  //private ProcessBuilder installBrewPackages() {
  //  // Install Homebrew packages
  //  try {
  //    Path zshrcPath = Path.of(System.getProperty("user.home"), "/.zshrc");
  //    String osName = System.getProperty("os.name").toLowerCase();
  //    String brewPath;
  //    if (osName.contains("mac")) {
  //      brewPath = "/opt/homebrew/bin/brew";
  //    } else {
  //      Path homeLinuxbrew = Path.of("/home/linuxbrew/.linuxbrew/bin/brew");
  //      Path userHomeLinuxbrew = Path.of(System.getProperty("user.home"), ".linuxbrew/bin/brew");
  //
  //      if (Files.exists(homeLinuxbrew)) {
  //        brewPath = homeLinuxbrew.toString();
  //      } else if (Files.exists(userHomeLinuxbrew)) {
  //        brewPath = userHomeLinuxbrew.toString();
  //      } else {
  //        throw new IOException("Could not find Homebrew installation");
  //      }
  //    }
  //
  //    Files.writeString(zshrcPath,
  //        "\neval \"$(" + brewPath + " shellenv)\"\n",
  //        APPEND, CREATE);
  //
  //    List<String> brewPackages = config.get("brew.packages");
  //    List<String> arguments = new java.util.ArrayList<>();
  //    arguments.add("-c");
  //    arguments.add(
  //        "source " + zshrcPath + " && brew update && brew upgrade && brew install " + String.join(" ", brewPackages));
  //
  //    return utils.Exec.buildProcess("bash", arguments.toArray(new String[0]))
  //        .redirectOutput(Redirect.INHERIT)
  //        .redirectError(Redirect.INHERIT);
  //  } catch (IOException | ClassCastException e) {
  //    log.error("Error installing brew packages", e);
  //    return new ProcessBuilder().inheritIO();
  //  }
  //}

  //private ProcessBuilder installBrew() {
  //  // Install Homebrew
  //  try {
  //    Path tempFile = Files.createTempFile("brew", ".sh", new FileAttribute<?>[0]);
  //    HttpRequest request = HttpRequest.newBuilder()
  //        .uri(URI.create("https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh"))
  //        .GET()
  //        .build();
  //    try (HttpClient client = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.ALWAYS)
  //        .build()) {
  //      client.send(request, BodyHandlers.ofFile(tempFile));
  //    }
  //
  //    Files.setPosixFilePermissions(tempFile, PosixFilePermissions.fromString("rwxr-xr-x"));
  //    return utils.Exec.buildProcess("bash", "-c", tempFile.toAbsolutePath().toString())
  //        .redirectOutput(Redirect.INHERIT)
  //        .redirectError(Redirect.INHERIT);
  //  } catch (IOException | InterruptedException e) {
  //    log.error("Error during Homebrew installation", e);
  //    return new ProcessBuilder().inheritIO(); // Return an empty ProcessBuilder on error
  //  }
  //}

  //private ProcessBuilder syncHostInfo() {
  //  // Write system information to inventory.json using systeminformation npm
  //  // library
  //
  //  return utils.Exec.buildProcess("sudo", "npx", "systeminformation")
  //      .directory(HomeOpsPaths.HOME_OPS_CONFIG_PATH.getPath().toFile())
  //      .redirectOutput(HomeOpsPaths.HOME_OPS_CONFIG_PATH.getPath().resolve("host.json").toFile());
  //
  //}
  //
}
