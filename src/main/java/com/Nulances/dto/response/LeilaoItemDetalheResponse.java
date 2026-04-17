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
public class LeilaoItemDetalheResponse {

    private UUID leilaoId;
    private UUID leilaoLoteId;
    private UUID leilaoLoteBemId;
    private UUID loteId;
    private UUID bemId;

    private String tituloLeilao;
    private String codigoLote;
    private String modelo;
    private String descricao;
    private MarcaVeiculo marcaVeiculo;
    private String cidade;
    private FormatoLeilao formatoLeilao;

    private TipoVeiculo tipoVeiculo;
    private Integer ano;
    private Long quilometragem;
    private CambioVeiculo cambio;
    private CombustivelVeiculo combustivel;
    private CondicaoVeiculo condicao;
    private Boolean blindado;
    private String cor;
    private String placaVeiculo;
    private String finalChassi;

    private StatusLeilao statusLeilao;
    private StatusItemLeilao statusItem;

    private BigDecimal valorInicial;
    private BigDecimal incrementoMinimo;
    private BigDecimal lanceAtual;
    private BigDecimal proximoLance;
    private Instant aberturaDisputa;
    private Instant encerramentoDisputa;

    private String leiloeiroNome;
    private String comitenteNome;

    private List<BigDecimal> incrementosSugeridos = new ArrayList<>();
    private List<MidiaResponse> midias = new ArrayList<>();
    private List<HistoricoLanceResponse> historicoLances = new ArrayList<>();

    @Getter
    @Setter
    public static class MidiaResponse {
        private UUID id;
        private TipoMidiaBem tipo;
        private String arquivo;
        private Integer ordem;
    }

    @Getter
    @Setter
    public static class HistoricoLanceResponse {
        private UUID lanceId;
        private BigDecimal valor;
        private Instant dataHora;
        private String usuarioNome;
    }
}