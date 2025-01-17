///usr/bin/env jbang --quiet "$0" "$@" ; exit $?
//JAVA 21+
//DEPS org.projectlombok:lombok:1.18.36
//DEPS ch.qos.logback:logback-classic:1.5.16
//DEPS com.electronwill.night-config:toml:3.8.1
//DEPS com.google.guava:guava:30.1-jre
//DEPS info.picocli:picocli:4.6.3
//DEPS dev.dirs:directories:26
//FILES resources/default.toml=../../config/default.toml
//SOURCES utils/Exec.java
//SOURCES utils/Assets.java
//SOURCES utils/DefaultPaths.java
//SOURCES utils/EnvVars.java
//JAVAC_OPTIONS --enable-preview -proc:full
//JAVA_OPTIONS --enable-preview

import static utils.Assets.Config.HOME_OPS;

import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.Callable;
import com.electronwill.nightconfig.core.ConfigSpec;
import com.electronwill.nightconfig.core.file.FileConfig;
import com.google.common.collect.Lists;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import utils.Exec;

/**
 *
 */
@Command(name = "EchoRunner", mixinStandardHelpOptions = true, version = "EchoRunner 0.1", description = "EchoRunner made with jbang")
class EchoRunner implements Callable<Integer> {

  private static final FileConfig config;
  static {
    config = HOME_OPS.getConfig();
    config.load();
  }

  public static void main(String... args) {
    int exitCode = new CommandLine(new EchoRunner()).execute(args);
    System.exit(exitCode);
  }

  @Override
  public Integer call() throws Exception {
    String tablePath = "commands.EchoRunner.echoHello";
    String cmdPath = tablePath + ".cmd";
    String argsPath = tablePath + ".args";
    String workingDirPath = tablePath + ".workingDir";

    ConfigSpec spec = new ConfigSpec();
    spec.define(cmdPath, "echo");
    spec.define(argsPath, Lists.newArrayList("Hello, World!"));
    spec.define(workingDirPath, System.getProperty("user.dir"));

    if (!spec.isCorrect(config)) {
      spec.correct(config);
    }

    String command = config.get(cmdPath);
    List<String> args = config.get(argsPath);
    String workingDir = config.get(workingDirPath);

    if (config != null) {
      config.save();
      config.close();
    }

    return Exec.from(command, args.toArray(new String[0]), Path.of(workingDir)).start().waitFor();
  }

}
