package commands;

import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;

import picocli.CommandLine;
import picocli.CommandLine.Command;

import java.util.List;
import java.util.concurrent.Callable;

@Component
@Command(name = "gomplate", description = "Run gomplate templating")
public class GomplateCommand implements Callable<Integer> {

  @CommandLine.Option(names = { "-f", "--file" }, description = "Input template file")
  private String inputFile;

  @CommandLine.Option(names = { "-o", "--output" }, description = "Output file")
  private String outputFile;

  @Override
  public Integer call() throws Exception {
    ProcessBuilder pb = new ProcessBuilder();
    List<String> command = Lists.newArrayList("gomplate");

    if (inputFile != null) {
      command.add("-f");
      command.add(inputFile);
    }

    if (outputFile != null) {
      command.add("-o");
      command.add(outputFile);
    }

    if (dataSources != null) {
      for (String ds : dataSources) {
        command.add("-d");
        command.add(ds);
      }
    }

    pb.command(command);
    Process process = pb.start();
    return process.waitFor();
  }
}
