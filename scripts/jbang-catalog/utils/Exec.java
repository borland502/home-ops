package utils;

import java.nio.file.Path;
import java.util.List;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

public class Exec {

  public static ProcessBuilder buildProcess(String command, String... args) {
    // Validate that the command is exists and is executable
    if (Strings.isNullOrEmpty(command)) {
      throw new IllegalArgumentException("Command cannot be null or empty");
    }

    List<String> statement = Lists.asList(command, args);
    // By default output to whatever the invoking process is output
    return new ProcessBuilder(statement).redirectOutput(ProcessBuilder.Redirect.INHERIT);
  }

  // public static ProcessBuilder addEnvVars(ProcessBuilder pb, EnvVar... envVars)
  // {
  // pb.environment()
  // .putAll(Arrays.stream(envVars).collect(Collectors.toMap(envVar ->
  // envVar.key().toString(), EnvVar::value)));
  // return pb;
  // }

  public static ProcessBuilder setWorkingDir(ProcessBuilder pb, Path workingDir) {
    if (workingDir == null || !workingDir.toFile().exists() || !workingDir.toFile().isDirectory()) {
      throw new IllegalArgumentException("Working directory is invalid: " + workingDir);
    }
    pb.directory(workingDir.toFile());
    return pb;
  }

  public static ProcessBuilder redirectOutput(ProcessBuilder pb, Path output) {
    if (output == null || !output.toFile().exists() || !output.toFile().isFile()) {
      throw new IllegalArgumentException("Output file is invalid: " + output);
    }
    pb.redirectOutput(output.toFile());
    return pb;
  }

  public static ProcessBuilder redirectError(ProcessBuilder pb, Path error) {
    if (error == null || !error.toFile().exists() || !error.toFile().isFile()) {
      throw new IllegalArgumentException("Error file is invalid: " + error);
    }
    pb.redirectError(error.toFile());
    return pb;
  }

  public static ProcessBuilder redirectInput(ProcessBuilder pb, Path input) {
    if (input == null || !input.toFile().exists() || !input.toFile().isFile()) {
      throw new IllegalArgumentException("Input file is invalid: " + input);
    }
    pb.redirectInput(input.toFile());
    return pb;
  }

  public static ProcessBuilder from(String command, String... args) {
    return buildProcess(command, args);
  }

  public static ProcessBuilder from(String command, String[] args, Path workingDir) {
    return setWorkingDir(from(command, args), workingDir);
  }

  // public static ProcessBuilder from(String command, String[] args, Path
  // workingDir, EnvVar... envVars) {
  // return addEnvVars(setWorkingDir(from(command, args), workingDir), envVars);
  // }

  // public static ProcessBuilder from(String command, String[] args, Path
  // workingDir, Path output, Path error, Path input,
  // EnvVar... envVars) {
  // return redirectInput(redirectError(
  // redirectOutput(addEnvVars(setWorkingDir(from(command, args), workingDir),
  // envVars), output), error), input);
  // }

}
