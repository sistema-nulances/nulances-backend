package com.Nulances.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "resend")
public record ResendProperties(
        String apiKey,
        From from
) {
    public record From(
            String email,
            String name
    ) {}
}