package com.technohouser.commands;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.BufferedReader;

@Component
public class HostInfo {
  @Value("${assets.host.path}")
  private String outputPath;

  public void saveHostInfo() throws IOException, InterruptedException {
    ProcessBuilder processBuilder = new ProcessBuilder("npx", "systeminformation");
    processBuilder.redirectErrorStream(true);
    Process process = processBuilder.start();

    StringBuilder output = new StringBuilder();
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
      String line;
      while ((line = reader.readLine()) != null) {
        output.append(line).append("\n");
      }
    }

    int exitCode = process.waitFor();
    if (exitCode == 0) {
      try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath))) {
        writer.write(output.toString());
      }
    } else {
      throw new IOException("Command execution failed with exit code: " + exitCode);
    }
  }
}
