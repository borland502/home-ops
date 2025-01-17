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

import static java.nio.file.Files.createTempFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.attribute.FileAttribute;
import java.util.concurrent.Callable;

import org.eclipse.jgit.api.Git;

import com.electronwill.nightconfig.core.file.FileConfig;

import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import utils.Assets;
import utils.DefaultPaths;
import utils.DefaultPaths.HomeOpsPaths;

/**
 *
 */
@Slf4j
@Command(name = "BootstrapRunner", mixinStandardHelpOptions = true, version = "BootstrapRunner 0.1", description = "BootstrapRunner made with jbang")
class BootstrapRunner implements Callable<Integer> {

  private static FileConfig config = Assets.Config.HOME_OPS.getConfig();

  static {
    try (FileConfig backupConfig = FileConfig.of(createTempFile("homeOps", ".toml", new FileAttribute<?>[0]))) {
      config.load();
      backupConfig.putAll(config.unmodifiable());
      backupConfig.save();
    } catch (IOException e) {
      log.error("Error loading configuration", e);
    }
  }

  public static void main(String... args) {
    int exitCode = new CommandLine(new BootstrapRunner()).execute(args);
    System.exit(exitCode);
  }

  @Override
  public Integer call() throws Exception {

    // Ensure default paths exist
    if (DefaultPaths.ensurePaths()) {
      log.info("Default paths were created or exist");
    }

    // Validate if homeOps config exists and is a directory
    File homeOpsDataFile = HomeOpsPaths.HOME_OPS_DATA_PATH.getPath().toFile();
    homeOpsDataFile.mkdirs();
    if (homeOpsDataFile.isDirectory()) {
      log.info("HomeOps data path exists");
    } else {
      log.warn("HomeOps data path does not exist -- checking out project from git");
      // Clone project from git
      try {
        Git.cloneRepository()
            .setURI("https://github.com/borland502/home-ops.git")
            .setDirectory(homeOpsDataFile)
            .call();
        log.info("Project successfully cloned from git");
      } catch (Exception e) {
        log.error("Error cloning project from git", e);
      }
      return 0;
    }

    // Sync with optional TOML files

    return 1;
  }

}
