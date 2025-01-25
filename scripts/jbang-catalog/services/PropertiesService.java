package services;

@Slf4j
public class PropertiesService {

  import java.util.Map;
  import java.util.logging.Logger;
      private static final Logger logger = Logger.getLogger(PropertiesService.class.getName());

      public void logEnvironmentVariables() {
          Map<String, String> env = System.getenv();
          for (Map.Entry<String, String> entry : env.entrySet()) {
              logger.info(entry.getKey() + "=" + entry.getValue());
          }
      }
}
