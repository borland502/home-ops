package utils;

import java.nio.file.Path;
import java.nio.file.Paths;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DefaultPaths {

  public enum XdgPaths {
    XDG_BIN_HOME(Path.of(System.getProperty("user.home"), ".local", "bin")),
    XDG_LIB_HOME(Path.of(System.getProperty("user.home"), ".local", "lib")),
    XDG_CONFIG_HOME(Path.of(System.getProperty("user.home"), ".config")),
    XDG_DATA_HOME(Path.of(System.getProperty("user.home"), ".local", "share")),
    XDG_STATE_HOME(Path.of(System.getProperty("user.home"), ".local", "state")),
    XDG_CACHE_HOME(Path.of(System.getProperty("user.home"), ".cache")),
    XDG_RUNTIME_DIR(Path.of(System.getProperty("user.home"), ".run")),
    XDG_CONFIG_DIRS(Path.of("/etc", "xdg")),
    XDG_DATA_DIRS(Paths.get("/usr/share", "/usr/local/share"));

    private final Path path;

    XdgPaths(Path path) {
      this.path = path;
    }

    public Path getPath() {
      return path;
    }
  }

  public enum HomeOpsPaths {
    HOME_OPS_CONFIG_PATH(Path.of(System.getProperty("user.home"), ".home_ops", "config")),
    HOME_OPS_DATA_PATH(Path.of(System.getProperty("user.home"), ".home_ops", "data"));

    private final Path path;

    HomeOpsPaths(Path path) {
      this.path = path;
    }

    public Path getPath() {
      return path;
    }
  }

  public static boolean ensurePaths() {
    boolean success = true;
    for (XdgPaths path : XdgPaths.values()) {
      success &= ensurePath(path.getPath());
    }
    return success;
  }

  private static boolean ensurePath(Path path) {
    try {
      if (!path.toFile().exists()) {
        return path.toFile().mkdirs();
      }
      log.warn("Path exists: " + path);
      return true;
    } catch (Exception e) {
      log.error("Failed to create path: " + path, e);
      return false;
    }
  }

}
