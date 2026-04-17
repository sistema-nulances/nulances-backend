package com.Nulances.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class AdminDashboardLeiloesResponse {

    private long totalLotesCadastrados;
    private long totalLeiloesAoVivo;
    private long totalLeiloesEmBreve;
    private List<LeilaoAoVivoItem> leiloesAoVivo = new ArrayList<>();

    @Getter
    @Setter
    public static class LeilaoAoVivoItem {
        private String tituloLeilao;
        private Instant encerraEm;
        private String lote;      // "Lote + código"
        private String local;     // cidade (presencial) ou "Online"
        private String status;
        private BigDecimal lanceAtual;
    }
}