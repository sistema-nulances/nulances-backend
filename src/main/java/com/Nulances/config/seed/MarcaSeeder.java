package com.Nulances.config.seed;

import com.Nulances.domain.entity.Marca;
import com.Nulances.domain.enums.MarcaVeiculo;
import com.Nulances.repository.MarcaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

@Slf4j
@Component
@RequiredArgsConstructor
public class MarcaSeeder implements ApplicationRunner {

    private final MarcaRepository marcaRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        int inseridas = 0;

        for (MarcaVeiculo nome : MarcaVeiculo.values()) {
            if (marcaRepository.existsByNome(nome)) {
                continue;
            }

            Marca marca = new Marca();
            marca.setNome(nome);
            marcaRepository.save(marca);
            inseridas++;
        }

        log.info("Seeder de marcas finalizado. {} novas marcas inseridas.", inseridas);
    }
}