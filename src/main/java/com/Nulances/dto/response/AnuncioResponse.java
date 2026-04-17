package com.Nulances.dto.response;

import com.Nulances.domain.enums.StatusAnuncio;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class AnuncioResponse {

    private UUID id;
    private UUID vendedorId;
    private String vendedorNome;
    private UUID marcaId;
    private String marca;
    private String modelo;
    private BigDecimal preco;
    private String cidade;
    private String tipo;
    private String condicao;
    private Integer ano;
    private Long quilometragem;
    private String combustivel;
    private String cambio;
    private String finalChassi;
    private String cor;
    private Boolean blindado;
    private String placaVeiculo;
    private String descricao;
    private StatusAnuncio status;
    private Instant criadoEm;
    private List<AnuncioMidiaResponse> midias;
    private AnuncioDetalheTecnicoResponse detalheTecnico;
}