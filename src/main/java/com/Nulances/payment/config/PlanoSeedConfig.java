package com.Nulances.payment.config;

import com.Nulances.domain.entity.PlanoAnuncio;
import com.Nulances.repository.PlanoAnuncioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.math.BigDecimal;

@Configuration
@RequiredArgsConstructor
public class PlanoSeedConfig {

    private final PlanoAnuncioRepository planoAnuncioRepository;

    @Bean
    @Order(10)
    public CommandLineRunner seedPlanosPadrao() {
        return args -> {
            criarSeNaoExistir("BASICO", "Plano básico para começar", new BigDecimal("59.90"), 3);
            criarSeNaoExistir("PRO", "Plano intermediário para vendedores ativos", new BigDecimal("99.90"), 10);
            criarSeNaoExistir("PREMIUM", "Plano completo com maior volume de anúncios", new BigDecimal("199.90"), 30);
        };
    }

    private void criarSeNaoExistir(String nome, String descricao, BigDecimal valor, int totalAnuncios) {
        if (planoAnuncioRepository.findByNomeIgnoreCase(nome).isPresent()) {
            return;
        }

        PlanoAnuncio plano = new PlanoAnuncio();
        plano.setNome(nome);
        plano.setDescricao(descricao);
        plano.setValorMensal(valor);
        plano.setTotalAnuncios(totalAnuncios);
        plano.setAtivo(true);
        plano.setIlimitado(false);
        planoAnuncioRepository.save(plano);
    }
}
