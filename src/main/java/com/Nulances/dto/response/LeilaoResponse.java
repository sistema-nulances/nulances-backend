package com.Nulances.dto.response;

import com.Nulances.domain.enums.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class LeilaoResponse {

    private UUID id;
    private String titulo;
    private FormatoLeilao formato;
    private String cidade;
    private String endereco;
    private UUID leiloeiroId;
    private UUID comitenteId;
    private Instant inicioLeilao;
    private Instant fimLeilao;
    private StatusLeilao status;
    private Instant createdAt;
    private Instant updatedAt;
    private List<LoteResponse> lotes = new ArrayList<>();

    @Getter
    @Setter
    public static class LoteResponse {
        private UUID leilaoLoteId;
        private UUID loteId;
        private String codigoLote;
        private List<ItemResponse> bens = new ArrayList<>();
    }

    @Getter
    @Setter
    public static class ItemResponse {
        private UUID leilaoLoteBemId;
        private UUID bemId;
        private MarcaVeiculo marcaVeiculo;
        private TipoVeiculo tipoVeiculo;
        private String modelo;
        private String descricao;
        private Integer ano;
        private Long quilometragem;
        private CambioVeiculo cambio;
        private CombustivelVeiculo combustivel;
        private CondicaoVeiculo condicao;
        private BigDecimal valorInicial;
        private BigDecimal incrementoMinimo;
        private BigDecimal lanceAtual;
        private BigDecimal proximoLance;
        private Instant aberturaDisputa;
        private Instant encerramentoDisputa;
        private StatusItemLeilao status;
        private List<MidiaResponse> midias = new ArrayList<>();
    }

    @Getter
    @Setter
    public static class MidiaResponse {
        private UUID id;
        private TipoMidiaBem tipo;
        private String arquivo;
        private Integer ordem;
    }
}