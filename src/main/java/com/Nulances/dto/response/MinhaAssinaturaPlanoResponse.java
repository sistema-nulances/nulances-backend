package com.Nulances.dto.response;

import com.Nulances.domain.enums.StatusAssinaturaPlano;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Builder
public class MinhaAssinaturaPlanoResponse {
    private UUID assinaturaId;
    private StatusAssinaturaPlano status;
    private Instant inicioVigencia;
    private Instant proximaCobranca;
    private Integer anunciosDisponiveis;
    private PlanoAnuncioResponse plano;
}
