package com.technohouser.config;

import com.technohouser.config.theme.HoFigureSettings;
import com.technohouser.config.theme.HoSpinnerSettings;
import com.technohouser.config.theme.HoStyleSettings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.shell.style.*;

@Slf4j
@Configuration
public class MonokaiFilterSpectrumThemeConfig {

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
