package com.technohouser.config.theme;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.style.StyleSettings;
import org.springframework.shell.style.ThemeResolver;
import org.springframework.stereotype.Component;

@Component
public class HoStyleSettings extends StyleSettings {

  @Override
  public String title() {
    return "#a9dc76"; // Monokai Pro Filter Spectrum - Green
  }

  @Override
  public String value() {
    return "#78dce8"; // Monokai Pro Filter Spectrum - Cyan
  }

  @Override
  public String listKey() {
    return "#ff6188"; // Monokai Pro Filter Spectrum - Red
  }

  @Override
  public String listValue() {
    return "#ffd866"; // Monokai Pro Filter Spectrum - Yellow
  }

  @Override
  public String listLevelInfo() {
    return "#78dce8"; // Monokai Pro Filter Spectrum - Cyan
  }

  @Override
  public String listLevelWarn() {
    return "#ffd866"; // Monokai Pro Filter Spectrum - Yellow
  }

  @Override
  public String listLevelError() {
    return "#ff6188"; // Monokai Pro Filter Spectrum - Red
  }

  @Override
  public String itemEnabled() {
    return "#a9dc76"; // Monokai Pro Filter Spectrum - Green
  }

  @Override
  public String itemDisabled() {
    return "#727072"; // Monokai Pro Filter Spectrum - Gray
  }

  @Override
  public String itemSelected() {
    return "#78dce8"; // Monokai Pro Filter Spectrum - Cyan
  }

  @Override
  public String itemUnselected() {
    return "#727072"; // Monokai Pro Filter Spectrum - Gray
  }

  @Override
  public String itemSelector() {
    return "#ffd866"; // Monokai Pro Filter Spectrum - Yellow
  }

  @Override
  public String highlight() {
    return "#a9dc76"; // Monokai Pro Filter Spectrum - Green
  }

  @Override
  public String background() {
    return "#2d2a2e"; // Monokai Pro Filter Spectrum - Background
  }

  @Override
  public String dialogBackground() {
    return "#403e41"; // Monokai Pro Filter Spectrum - Dialog Background
  }

  @Override
  public String buttonBackground() {
    return "#727072"; // Monokai Pro Filter Spectrum - Button Background
  }

  @Override
  public String menubarBackground() {
    return "#2d2a2e"; // Monokai Pro Filter Spectrum - Menubar Background
  }

  @Override
  public String statusbarBackground() {
    return "#2d2a2e"; // Monokai Pro Filter Spectrum - Statusbar Background
  }
}
