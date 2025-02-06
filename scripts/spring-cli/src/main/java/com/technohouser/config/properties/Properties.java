package com.technohouser.config.properties;

import com.technohouser.config.properties.json.HostProperties;
import com.technohouser.config.properties.json.JsonPropertySourceLoader;
import com.technohouser.config.properties.toml.AptProperties;
import com.technohouser.config.properties.toml.BrewProperties;
import com.technohouser.config.properties.toml.TomlPropertySourceLoader;
import org.springframework.context.annotation.Import;

@Import({JsonPropertySourceLoader.class, HostProperties.class, AptProperties.class, BrewProperties.class, TomlPropertySourceLoader.class})
public class Properties {}
