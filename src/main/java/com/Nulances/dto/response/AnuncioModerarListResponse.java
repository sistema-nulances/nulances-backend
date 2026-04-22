package com.Nulances.dto.response;

import com.Nulances.domain.enums.TipoVeiculo;
import lombok.Builder;

import java.time.OffsetDateTime;
import java.util.UUID;

@Builder
public record AnuncioModerarListResponse(
        UUID id,
        String modelo,
        String nomeVendedor,
        OffsetDateTime enviadoEm,
        TipoVeiculo tipoVeiculo
) {
}