package com.Nulances.payment.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.payment")
public record PaymentProperties(
        MercadoPago mercadopago,
        Assinatura assinatura
) {
    public record MercadoPago(
            String apiBaseUrl,
            String accessToken,
            String publicKey,
            String webhookSecret,
            String webhookUrl,
            String successUrl,
            String pendingUrl,
            String failureUrl
    ) {}

    public record Assinatura(
            Integer diasVigencia,
            Integer diasToleranciaInadimplencia
    ) {}
}
