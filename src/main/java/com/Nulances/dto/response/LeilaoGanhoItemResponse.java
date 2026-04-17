package com.Nulances.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Getter
@Builder(toBuilder = true)
public class LeilaoGanhoItemResponse {
    private String id;
    private String leilaoId;
    private String leilaoLoteId;
    private String leilaoLoteBemId;
    private String loteId;
    private String bemId;

    private String tituloLeilao;
    private String codigoLote;

    private String titulo;
    private String marcaVeiculo;
    private String modelo;
    private String tipoVeiculo;

    private String cidade;
    private String estado;

    private Integer anoFabricacao;
    private Integer anoModelo;
    private Long quilometragem;
    private String cambio;
    private String combustivel;
    private String placaVeiculo;

    private String midiaCapaUrl;
    private BigDecimal valorArrematado;
    private String statusPagamento;

    private Instant aberturaDisputa;
    private Instant encerramentoDisputa;

    private ComitenteResumoResponse comitente;
    private List<DocumentoLoteResponse> documentos;
    private ContatoLoteResponse contato;

    private Instant createdAt;
    private Instant updatedAt;
}