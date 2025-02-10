package com.technohouser.config.theme;

import org.springframework.shell.style.FigureSettings;
import org.springframework.stereotype.Component;

@Component
public class HoFigureSettings extends FigureSettings {

  @Override
  public String error() {
    return "#ff6188"; // Monokai Pro Filter Spectrum - Red
  }

  @Override
  public String warning() {
    return "#ffd866"; // Monokai Pro Filter Spectrum - Yellow
  }

  @Override
  public String info() {
    return "#78dce8"; // Monokai Pro Filter Spectrum - Cyan
  }

  public HoFigureSettings() {}


}
