package com.Nulances.helpers;

import com.Nulances.repository.LoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LoteCodigoGenerator {

    private final LoteRepository loteRepository;

    public String gerarCodigoUnico() {
        String codigo;

        do {
            codigo = CodigoHelper.gerarCodigo4Digitos();
        } while (loteRepository.existsByCodigo(codigo));

        return codigo;
    }
}