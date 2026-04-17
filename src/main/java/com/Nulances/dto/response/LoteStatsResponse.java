package com.Nulances.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoteStatsResponse {

    private Long totalLotes;
    private Long totalDisponiveis;
    private Long totalEmLeilao;
    private Long totalEncerrados;
}