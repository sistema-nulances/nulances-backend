package com.Nulances.config;

import com.Nulances.storage.R2Properties;
import com.Nulances.payment.config.PaymentProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({
        ResendProperties.class,
        AppProperties.class,
        R2Properties.class,
        PaymentProperties.class
})
public class PropertiesConfig {
}