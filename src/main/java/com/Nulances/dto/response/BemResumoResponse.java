package com.Nulances.dto.response;

import com.Nulances.domain.enums.MarcaVeiculo;
import com.Nulances.domain.enums.StatusBem;
import com.Nulances.domain.enums.TipoVeiculo;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class BemResumoResponse {

    private UUID id;
    private MarcaVeiculo marca;
    private String modelo;
    private TipoVeiculo tipoVeiculo;
    private Integer ano;
    private String placaVeiculo;
    private StatusBem status;
    private UUID loteId;
    private Instant createdAt;
    private List<BemMidiaResponse> midias = new ArrayList<>();
}