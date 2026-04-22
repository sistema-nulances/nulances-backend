package com.Nulances.dto.response;

import com.Nulances.domain.enums.TipoVeiculo;

import java.time.Instant;
import java.util.UUID;

public record AnuncioModeracaoListResponse(
        UUID id,
        String modelo,
        String nomeVendedor,
        Instant enviadoEm,
        TipoVeiculo tipoVeiculo
) {
}