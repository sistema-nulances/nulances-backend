package com.Nulances.dto.response;

import com.Nulances.domain.enums.StatusLote;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class LoteListResponse {

    private UUID id;
    private String codigo;
    private String nome;
    private Integer totalBens;
    private String nomeLeilao;
    private StatusLote status;
}