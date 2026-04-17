package com.Nulances.config;

import com.Nulances.storage.R2Properties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({ResendProperties.class, AppProperties.class, R2Properties.class})
public class PropertiesConfig {
}