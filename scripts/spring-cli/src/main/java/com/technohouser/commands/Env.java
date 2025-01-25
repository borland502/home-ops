package com.technohouser.commands;

import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import java.util.Arrays;

@ShellComponent
public class Env {

  private final Environment environment;

  public Env(Environment environment) {
    this.environment = environment;
  }

  @ShellMethod(value = "Print all environment properties", key = "printenvs")
  public void printEnvironmentProperties() {
    MutablePropertySources propertySources = ((ConfigurableEnvironment) environment).getPropertySources();
    for (PropertySource<?> propertySource : propertySources) {
      System.out.println("\nProperty Source: " + propertySource.getName());
      if (propertySource instanceof EnumerablePropertySource) {
        EnumerablePropertySource<?> eps = (EnumerablePropertySource<?>) propertySource;
        String[] propertyNames = eps.getPropertyNames();
        Arrays.sort(propertyNames);
        for (String propertyName : propertyNames) {
          System.out.println(propertyName + "=" + eps.getProperty(propertyName));
        }
      }
    }
  }
}
