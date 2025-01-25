package commands;

import org.springframework.stereotype.Component;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import java.util.concurrent.Callable;
import services.GreetingService;

@Component
@Command(name="greetingCommand")
public class GreetingCommand implements Callable<Integer> {

    @CommandLine.Parameters(index = "0", description = "The greeting to print", defaultValue = "World!")
    String name;

    private final GreetingService greetingService;

    public GreetingCommand(GreetingService greetingService) {
        this.greetingService = greetingService;
    }

    @Override
    public Integer call() throws Exception {
        greetingService.sayHello(name);
        return 0;
    }

}
