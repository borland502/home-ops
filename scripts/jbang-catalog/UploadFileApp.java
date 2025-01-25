///usr/bin/env jbang --quiet "$0" "$@" ; exit $?
//JAVA 21+
//DEPS org.projectlombok:lombok:1.18.36
//DEPS ch.qos.logback:logback-classic:1.5.16
//DEPS com.electronwill.night-config:toml:3.8.1
//DEPS com.google.guava:guava:30.1-jre
//DEPS org.eclipse.jgit:org.eclipse.jgit:7.1.0.202411261347-r
//DEPS info.picocli:picocli:4.6.3
//SOURCES utils/Exec.java
//SOURCES utils/Assets.java
//SOURCES utils/DefaultPaths.java
//JAVAC_OPTIONS -proc:full

import com.electronwill.nightconfig.core.file.FileConfig;
import com.sun.net.httpserver.HttpServer;

import lombok.extern.slf4j.Slf4j;
import utils.Assets;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.net.InetSocketAddress;

@Slf4j
public class UploadFileApp {

  private static FileConfig config;

  static {
    try {
      config = Assets.Config.HOME_OPS.getTomlConfig();
      config.load();
    } catch (IOException | InterruptedException e) {
      log.error("Error initializing configuration", e);
      System.exit(2);
    }
  }

  private static final Path trapperKeeperDbFile = Path.of(System.getenv().getOrDefault("XDG_DATA_HOME",
      System.getProperty("user.home") + "/.local/share"))
      .resolve(config.get("bootstrap.trapper_keeper.db").toString());
  private static final Path trapperKeeperTokenFile = Path.of(System.getenv().getOrDefault("XDG_DATA_HOME",
      System.getProperty("user.home") + "/.config"))
      .resolve(config.get("bootstrap.trapper_keeper.token").toString());

  static {
    try {
      Files.createDirectories(trapperKeeperDbFile.getParent());
      Files.createDirectories(trapperKeeperTokenFile.getParent());
    } catch (IOException e) {
      log.error("Error creating directories", e);
      System.exit(2);
    }
  }

  public static void main(String[] args) throws IOException {
    HttpServer server = HttpServer.create(new InetSocketAddress(7080), 0);

    server.createContext("/", exchange -> {
      if ("GET".equals(exchange.getRequestMethod())) {
        var response = """
            <html><body>
            <form method='POST' enctype='multipart/form-data' action='/upload'>
            <label for="dbFile">Database File:</label>
            <input type='file' id='dbFile' name='dbFile'/><br>
            <label for="tokenFile">Token File:</label>
            <input type='file' id='tokenFile' name='tokenFile'/><br>
            <input type='submit' value='Upload'/>
            </form>
            </body></html>
            """;
        exchange.sendResponseHeaders(200, response.getBytes().length);
        exchange.getResponseBody().write(response.getBytes());
        exchange.close();
      }
    });

    server.createContext("/upload", exchange -> {
      if ("POST".equals(exchange.getRequestMethod())) {
        try {
          var tempDbFile = Files.createTempFile("bootstrap-db", ".db");
          var tempTokenFile = Files.createTempFile("bootstrap-token", ".token");
          var contentType = exchange.getRequestHeaders().getFirst("Content-Type");

          if (contentType != null && contentType.contains("multipart/form-data")) {
            var boundary = "--" + contentType.split("boundary=")[1];
            try (var in = exchange.getRequestBody()) {
              var bytes = in.readAllBytes();
              var content = new String(bytes);
              var parts = content.split(boundary);

              for (var part : parts) {
                if (part.contains("name=\"dbFile\"")) {
                  var fileContent = part.substring(part.indexOf("\r\n\r\n") + 4, part.lastIndexOf("\r\n"));
                  Files.write(tempDbFile, fileContent.getBytes());
                  Files.move(tempDbFile, trapperKeeperDbFile, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                } else if (part.contains("name=\"tokenFile\"")) {
                  var fileContent = part.substring(part.indexOf("\r\n\r\n") + 4, part.lastIndexOf("\r\n"));
                  Files.write(tempTokenFile, fileContent.getBytes(Charset.forName("utf-8")));
                  Files.move(tempTokenFile, trapperKeeperTokenFile, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                }
              }

              var response = "Files uploaded successfully";
              exchange.getResponseHeaders().set("Content-Type", "text/plain");
              exchange.sendResponseHeaders(200, response.length());
              try (var os = exchange.getResponseBody()) {
                os.write(response.getBytes());
              }
            }
          } else {
            var response = "Invalid content type";
            exchange.getResponseHeaders().set("Content-Type", "text/plain");
            exchange.sendResponseHeaders(400, response.length());
            try (var os = exchange.getResponseBody()) {
              os.write(response.getBytes());
            }
          }
        } catch (Exception e) {
          log.error("Error processing upload", e);
          var response = "Error processing upload: " + e.getMessage();
          exchange.getResponseHeaders().set("Content-Type", "text/plain");
          exchange.sendResponseHeaders(500, response.length());
          try (var os = exchange.getResponseBody()) {
            os.write(response.getBytes());
          }
        }
      }
      exchange.close();
    });

    server.start();
    config.save();
    config.close();
  }
}
