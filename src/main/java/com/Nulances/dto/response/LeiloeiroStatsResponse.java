package com.Nulances.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LeiloeiroStatsResponse {

    private long totalLeiloeiros;
    private long totalLeiloeirosAtivosPlataforma;
    private long totalLeiloeirosInativosPlataforma;
    private long totalLeiloeirosComLeilaoVinculado;
}