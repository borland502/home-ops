package utils;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import com.electronwill.nightconfig.core.ConfigFormat;
import com.electronwill.nightconfig.core.ConfigSpec;
import com.electronwill.nightconfig.core.file.FileConfig;
import com.electronwill.nightconfig.core.file.FileNotFoundAction;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.graph.ImmutableNetwork;

import lombok.extern.slf4j.Slf4j;

import static java.lang.System.getenv;
import static java.nio.file.Files.createFile;

/**
 * Assets -- a resource loading utility class
 */
@Slf4j
public class Assets {

  public enum Config {
    HOME_OPS;

    private FileConfig tomlConfig;
    private FileConfig jsonConfig;
    private ConfigSpec spec;

    Config() {
      tomlConfig = FileConfig
          .builder(Paths.get(getenv("XDG_CONFIG_HOME"), "home-ops", "default.toml"))
          .autoreload()
          .onFileNotFound(FileNotFoundAction.CREATE_EMPTY)
          .preserveInsertionOrder()
          .sync()
          .build();

      jsonConfig = FileConfig
          .builder(Paths.get(getenv("XDG_CONFIG_HOME"), "home-ops", "inventory.json"))
          .autoreload()
          .onFileNotFound(FileNotFoundAction.CREATE_EMPTY)
          .preserveInsertionOrder()
          .sync()
          .build();
    }

    public FileConfig getTomlConfig() {
      tomlConfig.set("colors.base0", "#131313");
      tomlConfig.set("colors.base1", "#191919");
      tomlConfig.set("colors.base2", "#222222");
      tomlConfig.set("colors.base3", "#363537");
      tomlConfig.set("colors.base4", "#525053");
      tomlConfig.set("colors.base5", "#69676c");
      tomlConfig.set("colors.base6", "#8b888f");
      tomlConfig.set("colors.base7", "#bab6c0");
      tomlConfig.set("colors.base8", "#f7f1ff");
      tomlConfig.set("colors.base8x0c", "#2b2b2b");
      tomlConfig.set("colors.blue", "#5ad4e6");
      tomlConfig.set("colors.green", "#7bd88f");
      tomlConfig.set("colors.orange", "#fd9353");
      tomlConfig.set("colors.purple", "#948ae3");
      tomlConfig.set("colors.red", "#fc618d");
      tomlConfig.set("colors.yellow", "#fcd566");

      tomlConfig.set("zx.installGlobals", ImmutableList.of("tsx", "ts-node", "typescript", "nx"));
      tomlConfig.set("zx.nothrow", true);
      tomlConfig.set("zx.pkgManager", "npm");
      tomlConfig.set("zx.shell", "zsh");
      tomlConfig.set("zx.verbose", true);
      tomlConfig.set("zx.minimist.opts.stopEarly", true);

      return tomlConfig;
    }
  }

}
