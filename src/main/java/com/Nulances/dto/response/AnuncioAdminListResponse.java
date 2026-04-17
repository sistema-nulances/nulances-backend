package com.Nulances.dto.response;

import com.Nulances.domain.enums.MarcaVeiculo;
import com.Nulances.domain.enums.StatusAnuncio;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class AnuncioAdminListResponse {

    private UUID id;
    private String modelo;
    private MarcaVeiculo marcaVeiculo;
    private OffsetDateTime quandoFoiPostado;
    private BigDecimal valor;
    private StatusAnuncio status;
    private UUID vendedorId;
    private String vendedorNome;
    private List<AnuncioMidiaListResponse> midias;
}