package com.technohouser.exceptions;

import org.springframework.shell.command.CommandExceptionResolver;
import org.springframework.shell.command.CommandHandlingResult;
import org.springframework.stereotype.Component;


@Component
public class CliExceptionResolver implements CommandExceptionResolver {

  @Override
  public CommandHandlingResult resolve(Exception ex) {
    return CommandHandlingResult.of(ex.getMessage()+'\n', 1);
  }
}
