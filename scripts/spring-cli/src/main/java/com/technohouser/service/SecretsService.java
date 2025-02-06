package com.technohouser.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.Callable;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.linguafranca.pwdb.kdbx.KdbxCreds;
import org.linguafranca.pwdb.kdbx.jackson.JacksonDatabase;
import org.linguafranca.pwdb.kdbx.jackson.JacksonEntry;
import org.linguafranca.pwdb.kdbx.jackson.JacksonGroup;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.sql.init.dependency.DependsOnDatabaseInitialization;
import org.springframework.boot.web.server.GracefulShutdownCallback;
import org.springframework.boot.web.server.GracefulShutdownResult;
import org.springframework.context.Phased;
import org.springframework.context.SmartLifecycle;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;
import static org.aspectj.weaver.tools.cache.SimpleCacheFactory.path;

/**
 * Service to interact with KeepassXc database. This service keeps the vault open until and unless
 * the application is stopped or mutable operations are called. Neither KeepassXc nor the database
 * are really suited to live modification, so the service is designed to return immutable objects
 * and close the db instance itself.
 */
@Slf4j
@Transactional
@Service
@DependsOnDatabaseInitialization
public class SecretsService implements SmartLifecycle, Callable<Void>, Phased {

  @Value("${XDG_DATA_HOME}")
  private Path XDG_DATA_HOME;

  @Value("${XDG_CONFIG_HOME}")
  private Path XDG_CONFIG_HOME;

  @Value("${bootstrap.trapper_keeper.token}")
  private Path tokenPath;

  @Value("${bootstrap.trapper_keeper.db}")
  private Path dbPath;

  private transient JacksonDatabase db;

  private FileSystemResource getKdbxResourceFile() {
    return new FileSystemResource(Objects.requireNonNull(XDG_CONFIG_HOME.resolve(tokenPath)));
  }

  private static KdbxCreds getKdbxCreds(FileSystemResource tokenFile) throws IOException {
    return new KdbxCreds(tokenFile.getContentAsByteArray());
  }

  @Override
  public void start() {
    try {
      call();
    } catch (Exception e) {
      log.error("Error starting KeepassXc Service", e);
    }
  }

  @Override
  public void stop() {
    saveDatabase();
    db = null;
  }

  @Override
  public boolean isRunning() {
    return this.db != null;
  }


  @Override
  public int getPhase() {
    return Integer.MAX_VALUE;
  }

  public void saveDatabase() {
    if (db == null) {
      return;
    }
    try {
      FileSystemResource tokenFile = getKdbxResourceFile();
      KdbxCreds creds = getKdbxCreds(tokenFile);
      FileSystemResource targetFile = new FileSystemResource(dbPath);
      db.save(creds, targetFile.getOutputStream());
      log.info("Database saved to {}", path);
    } catch (IOException e) {
      log.error("Error saving database", e);
    }
  }

  public JacksonGroup getRootGroup() {
    return db.getRootGroup();
  }

  public JacksonEntry getEntryByUuid(UUID uuid) {
    return db.findEntry(uuid);
  }

  public boolean isDirty() {
    return this.isRunning() && db.isDirty();
  }

  @Override
  public Void call() throws Exception {
    log.info("Starting KeepassXc Service");

    tokenPath = XDG_CONFIG_HOME.resolve(tokenPath);
    Path dbResolvedPath = XDG_DATA_HOME.resolve(dbPath);

    if (!Files.exists(tokenPath)) {
      log.error("Token file does not exist at {}", tokenPath);
    }

    if (!Files.exists(dbResolvedPath)) {
      log.error("Database file does not exist at {}", dbResolvedPath);
    }

    KdbxCreds creds;
    try (var dbResource = new FileSystemResource(dbResolvedPath).getInputStream()) {
      creds = getKdbxCreds(getKdbxResourceFile());
      db = JacksonDatabase.load(creds, dbResource);
    } catch (IOException e) {
      log.error("Error reading token file or loading the database", e);
    }

    return null;
  }

}
