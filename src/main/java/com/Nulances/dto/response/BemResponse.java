package com.Nulances.dto.response;

import com.Nulances.domain.enums.CambioVeiculo;
import com.Nulances.domain.enums.CombustivelVeiculo;
import com.Nulances.domain.enums.CondicaoVeiculo;
import com.Nulances.domain.enums.MarcaVeiculo;
import com.Nulances.domain.enums.StatusBem;
import com.Nulances.domain.enums.TipoVeiculo;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class BemResponse {

    private UUID id;
    private UUID loteId;

    private UUID marcaId;
    private MarcaVeiculo marca;

    private String modelo;
    private TipoVeiculo tipoVeiculo;
    private CondicaoVeiculo condicao;
    private Integer ano;
    private Long quilometragem;
    private String finalChassi;
    private CombustivelVeiculo combustivel;
    private CambioVeiculo cambio;
    private Boolean blindado;
    private String cor;
    private String placaVeiculo;
    private String descricao;
    private StatusBem status;

    private Instant createdAt;
    private Instant updatedAt;

    private List<BemMidiaResponse> midias;
}