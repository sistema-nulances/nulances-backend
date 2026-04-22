package com.Nulances.payment.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
@RequiredArgsConstructor
public class MercadoPagoConfig {

    private final PaymentProperties paymentProperties;

    @PostConstruct
    public void validarConfiguracao() {
        String apiBaseUrl = paymentProperties.mercadopago().apiBaseUrl();
        String accessToken = paymentProperties.mercadopago().accessToken();

        if (apiBaseUrl == null || apiBaseUrl.isBlank()) {
            throw new IllegalStateException("app.payment.mercadopago.api-base-url não configurado.");
        }

        if (accessToken == null || accessToken.isBlank() || accessToken.startsWith("SEU_")) {
            throw new IllegalStateException("app.payment.mercadopago.access-token não configurado.");
        }
    }

    @Bean
    public RestClient mercadoPagoRestClient() {
        return RestClient.builder()
                .baseUrl(paymentProperties.mercadopago().apiBaseUrl())
                .defaultHeader("Authorization", "Bearer " + paymentProperties.mercadopago().accessToken())
                .build();
    }
}
