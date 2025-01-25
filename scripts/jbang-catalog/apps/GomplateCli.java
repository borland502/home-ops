///usr/bin/env jbang "$0" "$@" ; exit $?
//JAVA 21+
//DEPS org.springframework.boot:spring-boot-dependencies:3.4.1@pom
//DEPS ch.qos.logback:logback-classic:1.5.16
//DEPS org.springframework.boot:spring-boot-starter:3.4.1
//DEPS org.springframework.boot:spring-boot-starter-validation:3.4.1
//DEPS io.r2dbc:r2dbc-spi:0.9.0.RELEASE
//DEPS org.springframework.boot:spring-boot-actuator:3.4.1
//DEPS org.springframework.boot:spring-boot-starter-data-jpa:3.4.1
//DEPS org.springframework.boot:spring-boot-starter-data-redis:3.4.1
//DEPS org.springframework.boot:spring-boot-configuration-processor:3.4.1
//DEPS org.springframework.boot:spring-boot-autoconfigure:3.4.1
//DEPS org.hibernate.orm:hibernate-community-dialects:6.6.5.Final
//DEPS info.picocli:picocli-spring-boot-starter:4.7.6
//DEPS org.projectlombok:lombok:1.18.36
//DEPS com.electronwill.night-config:toml:3.8.1
//DEPS com.google.guava:guava:30.1-jre
//DEPS org.xerial:sqlite-jdbc:3.46.1.3
//DEPS org.fusesource.jansi:jansi:2.3.4
//SOURCES ../services/GreetingService.java
//SOURCES ../commands/GreetingCommand.java
//FILES application.yaml=../resources/spring-greeting.yaml
//FILES .gomplate.yaml=../resources/application.yaml
//FILES logback-spring.xml=../resources/logback-spring.xml

package apps;

import commands.GreetingCommand;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import picocli.CommandLine;
import picocli.CommandLine.IFactory;

@SpringBootApplication
@ComponentScan(basePackages = { "commands", "services" })
public class GomplateCli implements CommandLineRunner, ExitCodeGenerator {

  private int exitCode;

  private final GreetingCommand greetingCommand;
  private final IFactory factory;

  public GomplateCli(IFactory factory, GreetingCommand greetingCommand) {
    this.greetingCommand = greetingCommand;
    this.factory = factory;
  }

  @Override
  public void run(String... args) {
    // let picocli parse command line args and run the business logic
    exitCode = new CommandLine(greetingCommand, factory).execute(args);
  }

  @Override
  public int getExitCode() {
    return exitCode;
  }

  public static void main(String[] args) {
    // let Spring instantiate and inject dependencies
    System.exit(SpringApplication.exit(SpringApplication.run(GomplateCli.class, args)));
  }

}
