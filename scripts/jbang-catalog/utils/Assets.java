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

    private FileConfig config;
    private ConfigSpec spec;

    Config() {
      config = FileConfig
          .builder(Paths.get(getenv("XDG_CONFIG_HOME"), "home-ops", "config.toml"))
          .autoreload()
          .onFileNotFound(FileNotFoundAction.CREATE_EMPTY)
          .preserveInsertionOrder()
          .sync()
          .build();
    }

    public FileConfig getConfig() {
      config.set("colors.base0", "#131313");
      config.set("colors.base1", "#191919");
      config.set("colors.base2", "#222222");
      config.set("colors.base3", "#363537");
      config.set("colors.base4", "#525053");
      config.set("colors.base5", "#69676c");
      config.set("colors.base6", "#8b888f");
      config.set("colors.base7", "#bab6c0");
      config.set("colors.base8", "#f7f1ff");
      config.set("colors.base8x0c", "#2b2b2b");
      config.set("colors.blue", "#5ad4e6");
      config.set("colors.green", "#7bd88f");
      config.set("colors.orange", "#fd9353");
      config.set("colors.purple", "#948ae3");
      config.set("colors.red", "#fc618d");
      config.set("colors.yellow", "#fcd566");

      config.set("zx.installGlobals", ImmutableList.of("tsx", "ts-node", "typescript", "nx"));
      config.set("zx.nothrow", true);
      config.set("zx.pkgManager", "npm");
      config.set("zx.shell", "zsh");
      config.set("zx.verbose", true);
      config.set("zx.minimist.opts.stopEarly", true);

      return config;
    }
  }

}
