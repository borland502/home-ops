package com.technohouser.utils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Map;

import com.technohouser.utils.DefaultPaths.HomeOpsPaths;
import org.eclipse.jgit.api.Git;

import com.electronwill.nightconfig.core.concurrent.SynchronizedConfig;
import com.electronwill.nightconfig.core.file.FileConfig;
import com.electronwill.nightconfig.core.file.FileNotFoundAction;

import lombok.extern.slf4j.Slf4j;

import static java.lang.System.getProperty;

import java.io.File;
import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;

/**
 * Assets -- a resource loading utility class
 */
@Slf4j
public class Assets {

  public enum Config {
    HOME_OPS;

    private final FileConfig tomlConfig;

    Config() {
      if (DefaultPaths.ensurePaths()) {
        log.info("Default paths were created or exist");
      }

      Path configPath = Paths.get(getProperty("user.home"), ".config", "home-ops", "default.toml");
      DefaultPaths.ensurePath(configPath.getParent());

      tomlConfig = FileConfig
          .builder(configPath)
          .autoreload()
          .onFileNotFound(FileNotFoundAction.CREATE_EMPTY)
          .preserveInsertionOrder()
          .sync()
          .build();
    }

    public FileConfig getTomlConfig() throws InterruptedException, IOException {
      syncConfig();

      // TOML [[]] list of tables object conversion
      List<SynchronizedConfig> envVars = tomlConfig.get("env.vars");

      // TODO: Abstract out toml loading and perhaps create a pull request to author
      // There should be one flat map of all env vars
      SynchronizedConfig envVarsConfig = envVars.getFirst();
      envVarsConfig.entrySet().stream().flatMap(e -> {
        String key = e.getKey();
        String value = e.getValue().toString();
        return Map.of(key, value).entrySet().stream();
      }).forEach(e -> {
        String key = e.getKey();
        String value = e.getValue();
        Path envPath = Paths.get(System.getProperty("user.home"), ".env");
        String entry = key + "=" + "\"" + value + "\"" + System.lineSeparator();
        try {
          Files.writeString(envPath, entry, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e1) {
          log.error("Error writing to env file", e1);
        }
        System.setProperty(key, value);
      });

      loadEnvVariables().start().waitFor();
      checkoutHomeOps();
      return tomlConfig;
    }

    private ProcessBuilder loadEnvVariables() {
      SynchronizedConfig envVars = tomlConfig.get("bootstrap.constants");

      envVars.entrySet().forEach(e -> {
        String key = e.getKey();
        String value = e.getValue().toString();
        Path envPath = Path.of(System.getProperty("user.home"), ".env");
        String entry = key + "=" + "\"" + value + "\"" + System.lineSeparator();
        try {
          Files.writeString(envPath, entry, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e1) {
          log.error("Error writing to env file", e1);
        }
        System.setProperty(key, value);
      });

      return new ProcessBuilder("bash", "-c", "env")
          .redirectOutput(Redirect.INHERIT)
          .redirectError(Redirect.INHERIT);
    }

    private void checkoutHomeOps() {
      DefaultPaths.ensurePath(HomeOpsPaths.HOME_OPS_DATA_PATH.getPath().getParent());
      File homeOpsDataFile = HomeOpsPaths.HOME_OPS_DATA_PATH.getPath().toFile();
      if (homeOpsDataFile.isDirectory()) {
        log.info("HomeOps data path exists");
      } else {
        log.warn("HomeOps data path does not exist -- checking out project from git");
        // Clone project from git
        try {
          Git.cloneRepository()
              .setURI("https://github.com/borland502/home-ops.git")
              .setDirectory(homeOpsDataFile)
              .setCloneSubmodules(true)
              .call();
          log.info("Project successfully cloned from git");
        } catch (Exception e) {
          log.error("Error cloning project from git", e);
        }
      }
    }

    private void syncConfig() throws InterruptedException, IOException {

      // Sync bootstrap configurations from bootstrap.toml
      FileConfig bootstrapConfig = FileConfig
          .of(HomeOpsPaths.HOME_OPS_DATA_PATH.getPath().resolve("scripts/dotfiles/.chezmoidata/bootstrap.toml")
              .toFile());
      bootstrapConfig.load();
      tomlConfig.putAll(bootstrapConfig.unmodifiable());

      // Sync with default files from default config but do not overwrite existing
      // values
      FileConfig defaultConfig = FileConfig
          .of(HomeOpsPaths.HOME_OPS_DATA_PATH.getPath().resolve("config/default.toml").toFile());
      defaultConfig.load();
      tomlConfig.addAll(defaultConfig.unmodifiable());

      // Chezmoi files are canonical sources of truth so replace any values that
      // match
      FileConfig envConfig = FileConfig
          .of(HomeOpsPaths.HOME_OPS_DATA_PATH.getPath().resolve("scripts/dotfiles/.chezmoidata/env.toml").toFile());
      envConfig.load();
      tomlConfig.putAll(envConfig.unmodifiable());

      FileConfig inventoryConfig = FileConfig
          .of(HomeOpsPaths.HOME_OPS_DATA_PATH.getPath().resolve("scripts/dotfiles/.chezmoidata/inventory.toml")
              .toFile());
      inventoryConfig.load();
      tomlConfig.putAll(inventoryConfig.unmodifiable());

      FileConfig packagesConfig = FileConfig
          .of(HomeOpsPaths.HOME_OPS_DATA_PATH.getPath().resolve("scripts/dotfiles/.chezmoidata/packages.toml")
              .toFile());
      packagesConfig.load();
      tomlConfig.putAll(packagesConfig.unmodifiable());

      // persist changes
      tomlConfig.save();
    }
  }

}
