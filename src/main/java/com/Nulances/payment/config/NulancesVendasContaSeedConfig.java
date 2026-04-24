package com.Nulances.payment.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class NulancesVendasContaSeedConfig {

    private final NulancesVendasContaSeeder nulancesVendasContaSeeder;

    @Bean
    @Order(20)
    public CommandLineRunner seedContaNulancesVendas() {
        return args -> {
            try {
                nulancesVendasContaSeeder.executarSeNecessario();
            } catch (Exception e) {
                log.warn("Seed conta NuLances Vendas não aplicado: {}", e.getMessage());
            }
        };
    }
}
