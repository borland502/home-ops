package com.technohouser.commands.display;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.technohouser.model.HostDto;
import jakarta.validation.constraints.AssertFalse.List;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.Callable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

@ShellComponent
public class HostInfo implements Callable<String> {

  @Value("${assets.host.path}")
  private Path inputPath;

  private final ObjectMapper objectMapper;

  public HostInfo(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  private Map<String, Object> readJsonAsMap() throws IOException {
    File inputFile = inputPath.toFile();
    if (!inputFile.exists()) {
      throw new IOException("Input path does not exist: " + inputPath);
    }

    try (BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {
      return objectMapper.readValue(reader, new TypeReference<Map<String, Object>>() {
      });
    }
  }

  private String convertToToml(Map<String, Object> jsonMap) {
    StringBuilder toml = new StringBuilder();
    buildTomlString(jsonMap, toml, "");
    return toml.toString();
  }

  @SuppressWarnings("unchecked")
  private void buildTomlString(Map<String, Object> map, StringBuilder toml, String prefix) {
    for (Map.Entry<String, Object> entry : map.entrySet()) {
      String key = entry.getKey();
      Object value = entry.getValue();

      if (value instanceof Map) {
        toml.append("\n[").append(prefix).append(key).append("]\n");
        buildTomlString((Map<String, Object>) value, toml, prefix + key + ".");
      } else if (value instanceof List) {
        toml.append(key).append(" = ").append(value).append("\n");
      } else {
        toml.append(key).append(" = ");
        if (value instanceof String) {
          toml.append("\"").append(value).append("\"");
        } else {
          toml.append(value);
        }
        toml.append("\n");
      }
    }
  }

  public HostDto getHost() throws IOException {
    Map<String, Object> jsonMap = readJsonAsMap();
    return objectMapper.convertValue(jsonMap, HostDto.class);
  }

  @ShellMethod(value = "Display host information", key = "hostinfo", group = "display")
  @Override
  public String call() throws Exception {
    Map<String, Object> jsonMap = readJsonAsMap();
    return convertToToml(jsonMap);
  }
}
