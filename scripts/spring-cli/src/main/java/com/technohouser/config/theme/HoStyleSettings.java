package com.technohouser.config.theme;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.style.StyleSettings;
import org.springframework.shell.style.ThemeResolver;
import org.springframework.stereotype.Component;

@Component
public class HoStyleSettings extends StyleSettings {

  @Override
  public String title() {
    return "bold,fg-rgb-rgb:#a9dc76"; // Monokai Pro Filter Spectrum - Green
  }

  @Override
  public String value() {
    return "fg-rgb:#78dce8"; // Monokai Pro Filter Spectrum - Cyan
  }

  @Override
  public String listKey() {
    return "fg-rgb:#ff6188"; // Monokai Pro Filter Spectrum - Red
  }

  @Override
  public String listValue() {
    return "bold,fg-rgb:#ffd866"; // Monokai Pro Filter Spectrum - Yellow
  }

  @Override
  public String listLevelInfo() {
    return "fg-rgb:#78dce8"; // Monokai Pro Filter Spectrum - Cyan
  }

  @Override
  public String listLevelWarn() {
    return "fg-rgb:#ffd866"; // Monokai Pro Filter Spectrum - Yellow
  }

  @Override
  public String listLevelError() {
    return "fg-rgb:#ff6188"; // Monokai Pro Filter Spectrum - Red
  }

  @Override
  public String itemEnabled() {
    return "bold,fg-rgb:#a9dc76"; // Monokai Pro Filter Spectrum - Green
  }

  @Override
  public String itemDisabled() {
    return "faint,fg-rgb:#727072"; // Monokai Pro Filter Spectrum - Gray
  }

  @Override
  public String itemSelected() {
    return "fg-rgb:#78dce8"; // Monokai Pro Filter Spectrum - Cyan
  }

  @Override
  public String itemUnselected() {
    return "bold,fg-rgb:#727072"; // Monokai Pro Filter Spectrum - Gray
  }

  @Override
  public String itemSelector() {
    return "bold,fg-rgb:#ffd866"; // Monokai Pro Filter Spectrum - Yellow
  }

  @Override
  public String highlight() {
    return "bold,fg-rgb:#a9dc76"; // Monokai Pro Filter Spectrum - Green
  }

  @Override
  public String background() {
    return "bg-rgb:#2d2a2e"; // Monokai Pro Filter Spectrum - Background
  }

  @Override
  public String dialogBackground() {
    return "bg-rgb:#403e41"; // Monokai Pro Filter Spectrum - Dialog Background
  }

  @Override
  public String buttonBackground() {
    return "bg-rgb:#727072"; // Monokai Pro Filter Spectrum - Button Background
  }

  @Override
  public String menubarBackground() {
    return "bg-rgb:#2d2a2e"; // Monokai Pro Filter Spectrum - Menubar Background
  }

  @Override
  public String statusbarBackground() {
    return "bg-rgb:#2d2a2e"; // Monokai Pro Filter Spectrum - Statusbar Background
  }
}
