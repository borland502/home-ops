package com.technohouser.config;

import com.technohouser.config.properties.Properties;
import com.technohouser.config.theme.HoFigureSettings;
import com.technohouser.config.theme.HoSpinnerSettings;
import com.technohouser.config.theme.HoStyleSettings;
import lombok.extern.slf4j.Slf4j;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;
import org.springframework.shell.component.message.ShellMessageBuilder;
import org.springframework.shell.component.view.TerminalUI;
import org.springframework.shell.component.view.TerminalUICustomizer;
import org.springframework.shell.component.view.event.KeyEvent.Key;
import org.springframework.shell.jline.InteractiveShellRunner;
import org.springframework.shell.jline.PromptProvider;
import org.springframework.shell.standard.AbstractShellComponent;
import org.springframework.shell.style.FigureSettings;
import org.springframework.shell.style.SpinnerSettings;
import org.springframework.shell.style.StyleSettings;
import org.springframework.shell.style.Theme;
import org.springframework.shell.style.ThemeSettings;

@Slf4j
@Configuration
@Order(InteractiveShellRunner.PRECEDENCE - 1)
@Import({Properties.class, JpaConfig.class, ResourcePathConfig.class,
    HoFigureSettings.class, HoSpinnerSettings.class, HoStyleSettings.class, JobsConfig.class})
public class ShellConfig extends AbstractShellComponent implements TerminalUICustomizer,
    PromptProvider {

  @Override
  public AttributedString getPrompt() {
    return new AttributedString("homeops:> ",
        new AttributedStyle().foreground(AttributedStyle.BLUE));
  }

  @Override
  public void customize(TerminalUI terminalUI) {
    terminalUI.getEventLoop().keyEvents().subscribe(keyEvent -> {
      if (keyEvent.getPlainKey() == Key.Q && keyEvent.hasCtrl()) {
        terminalUI.getEventLoop().dispatch(ShellMessageBuilder.ofInterrupt());
      }
    });

    terminalUI.run();
  }

  @Bean
  Theme ThemeSettings() {
    return new Theme() {
      @Override
      public String getName() {
        return "monokai-filter-spectrum";
      }

      @Override
      public ThemeSettings getSettings() {
        return new ThemeSettings() {
          @Override
          public StyleSettings styles() {
            return new HoStyleSettings();
          }

          @Override
          public FigureSettings figures() {
            return new HoFigureSettings();
          }

          @Override
          public SpinnerSettings spinners() {
            return new HoSpinnerSettings();
          }
        };
      }
    };
  }
}
