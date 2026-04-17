package com.Nulances.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
public record AppProperties(
        Email email,
        Jms jms
) {
    public record Email(Confirmacao confirmacao) {}
    public record Confirmacao(Integer expiracaoMinutos) {}
    public record Jms(
            String queueEmailConfirmacao,
            String queueLanceRecebido
    ) {}
}