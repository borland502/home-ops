package com.technohouser.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import java.nio.file.Path;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@TestPropertySource(properties = {
    "XDG_DATA_HOME=/home/test/.local/share",
    "XDG_CONFIG_HOME=/home/test/.config"
})
class ResourcePathConfigTest {

  @Autowired
  private ResourcePathConfig resourcePathConfig;

  @Test
  void getDataRoot_ShouldReturnExpectedPath() {
    Path expected = Path.of("/home/test/.local/share/automation/home-ops");
    assertEquals(expected, resourcePathConfig.getDataRoot());
  }

  @Test
  void getConfigRoot_ShouldReturnExpectedPath() {
    Path expected = Path.of("/home/test/.config/home-ops");
    assertEquals(expected, resourcePathConfig.getConfigRoot());
  }
}
