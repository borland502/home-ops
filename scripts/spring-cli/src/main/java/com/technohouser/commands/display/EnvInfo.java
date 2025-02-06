package com.technohouser.commands.display;

import java.util.Arrays;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.shell.boot.CommandRegistrationCustomizer;
import org.springframework.shell.command.CommandRegistration;
import org.springframework.shell.command.CommandRegistration.Builder;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

@ShellComponent
public class EnvInfo {

  private final Environment environment;

  public EnvInfo(Environment environment) {
    this.environment = environment;
  }

  @ShellMethod(value = "Print all environment properties", key = "printenv", group = "Info")
  public void printEnvironmentProperties() {
    MutablePropertySources propertySources = ((ConfigurableEnvironment) environment).getPropertySources();
    for (PropertySource<?> propertySource : propertySources) {
      System.out.println("\nProperty Source: " + propertySource.getName());
      if (propertySource instanceof EnumerablePropertySource<?> eps) {
        String[] propertyNames = eps.getPropertyNames();
        Arrays.sort(propertyNames);
        for (String propertyName : propertyNames) {
          System.out.println("\u001B[32m" + propertyName + "\u001B[0m=" + "\u001B[34m" + eps.getProperty(propertyName) + "\u001B[0m");
        }
      }
    }
  }

}
