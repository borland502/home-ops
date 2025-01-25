package com.technohouser.config.properties.toml;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;
import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.CommentedConfig.Entry;
import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.toml.TomlParser;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import org.springframework.boot.env.PropertySourceLoader;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.Resource;

public class TomlPropertySourceLoader implements PropertySourceLoader {

  @Override
  public String[] getFileExtensions() {
    return new String[] { "toml" };
  }

  @Override
  public List<PropertySource<?>> load(String name, Resource resource) throws IOException {
    try (InputStream in = resource.getInputStream()) {

      TomlParser parser = new TomlParser();
      CommentedConfig config = parser.parse(in);

      if (config.entrySet().isEmpty()) {
        return emptyList();
      }

      Map<String, Object> source = config.entrySet().stream()
          .collect(Collectors.toMap(Entry::getKey, entry -> (CommentedConfig) entry.getValue()));

      Map<String, Object> result = new LinkedHashMap<>();
      buildFlattenedMap(result, source, null);
      return singletonList(new MapPropertySource(name, result));
    }
  }

  private void buildFlattenedMap(Map<String, Object> result, Map<String, Object> source,
      String root) {
    boolean rootHasText = (null != root && !root.trim().isEmpty());

    source.forEach((key, value) -> {
      String path;

      if (rootHasText) {
        if (key.startsWith("[")) {
          path = root + key;
        } else {
          path = root + "." + key;
        }
      } else {
        path = key;
      }

      switch (value) {
        case Config ignored -> {
          Map<String, Object> nestedMap = ((CommentedConfig) value).entrySet().stream()
              .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
          buildFlattenedMap(result, nestedMap, path);
        }
        case Collection<?> collection -> {
          AtomicInteger count = new AtomicInteger();
          for (Object object : collection) {
            buildFlattenedMap(result, singletonMap("[" + (count.getAndIncrement()) + "]", object),
                path);
          }
        }
        case null, default -> result.put(path, value);
      }

    });
  }
}
