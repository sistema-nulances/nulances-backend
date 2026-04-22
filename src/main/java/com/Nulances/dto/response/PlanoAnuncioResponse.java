package com.Nulances.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Builder
public class PlanoAnuncioResponse {
    private UUID id;
    private String nome;
    private String descricao;
    private BigDecimal valorMensal;
    private Integer totalAnuncios;
    private Boolean ativo;
}
