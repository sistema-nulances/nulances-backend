package com.Nulances.dto.response;

import com.Nulances.domain.enums.FormatoLeilao;
import com.Nulances.domain.enums.StatusItemLeilao;
import com.Nulances.domain.enums.StatusLeilao;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class LeilaoPainelResponse {

    private UUID leilaoId;
    private String titulo;
    private String leiloeiro;
    private FormatoLeilao formato;
    private String cidade;
    private StatusLeilao status;
    private Instant encerramentoLeilao;

    private ItemEmPautaResponse itemEmPauta;

    private List<ItemPainelResponse> itens = new ArrayList<>();
    private List<AtividadeRecenteResponse> atividadesRecentes = new ArrayList<>();

    private StatsResponse stats;

    @Getter
    @Setter
    public static class ItemEmPautaResponse {
        private UUID leilaoLoteBemId;
        private UUID loteId;
        private String codigoLote;
        private UUID bemId;
        private String nomeBem;
        private BigDecimal valorAtual;
        private BigDecimal proximoLance;
        private BigDecimal valorInicial;
        private StatusItemLeilao status;
        private Instant aberturaDisputa;
        private Instant encerramentoDisputa;
    }

    @Getter
    @Setter
    public static class ItemPainelResponse {
        private UUID leilaoLoteBemId;
        private UUID loteId;
        private String codigoLote;
        private UUID bemId;
        private String nomeBem;
        private StatusItemLeilao status;
        private BigDecimal valorAtual;
        private BigDecimal proximoLance;
        private BigDecimal valorInicial;
        private Instant aberturaDisputa;
        private Instant encerramentoDisputa;
    }

    @Getter
    @Setter
    public static class AtividadeRecenteResponse {
        private String loteCodigo;
        private String nomeBem;
        private String acao;
        private Instant dataHora;
        private String usuarioNome;
        private BigDecimal valor;
    }

    @Getter
    @Setter
    public static class StatsResponse {
        private long totalLotesCatalogo;
        private long totalLances;
        private long totalUsuariosDistintos;
    }
}