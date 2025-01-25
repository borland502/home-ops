package services;

import org.springframework.stereotype.Service;

@Service
public class GreetingService {

  public void sayHello(String name) {
    System.out.println("Hello, " + name + "!");
  }

}
