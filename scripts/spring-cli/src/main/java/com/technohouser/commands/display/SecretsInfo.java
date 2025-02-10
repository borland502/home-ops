package com.technohouser.commands.display;

import com.technohouser.service.SecretsService;
import java.util.concurrent.Callable;
import org.springframework.shell.standard.ShellComponent;

@ShellComponent
public class SecretsInfo implements Callable<String> {

  private final SecretsService secretsService;

  public SecretsInfo(SecretsService secretsService) {
    this.secretsService = secretsService;
  }

  @Override
  public String call() throws Exception {
    return "";
  }
}
