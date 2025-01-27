package com.technohouser.config;

import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.shell.jline.InteractiveShellRunner;
import org.springframework.shell.jline.PromptProvider;
import org.springframework.shell.style.ThemeResolver;

@Configuration
@Order(InteractiveShellRunner.PRECEDENCE - 1)
public class ShellConfig implements PromptProvider {

  private final ThemeResolver themeResolver;

  public ShellConfig(ThemeResolver themeResolver) {
    this.themeResolver = themeResolver;
  }

  @Override
  public AttributedString getPrompt() {
    return new AttributedString("homeops:> ", new AttributedStyle().foreground(AttributedStyle.YELLOW));
  }

  void resolve() {
    themeResolver.resolveStyle("bold,fg:green");
  }

}


