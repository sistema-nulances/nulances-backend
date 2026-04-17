package com.Nulances.storage;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.r2")
public record R2Properties(
        String accountId,
        String accessKey,
        String secretKey,
        String bucket,
        String publicBaseUrl,
        long uploadExpiresInSeconds,
        long downloadExpiresInSeconds
) {
}