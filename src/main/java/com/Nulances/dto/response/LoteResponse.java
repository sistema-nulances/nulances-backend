package com.Nulances.dto.response;

import com.Nulances.domain.enums.StatusLote;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class LoteResponse {

    private UUID id;
    private String nome;
    private String codigo;
    private String observacoes;
    private StatusLote status;
    private List<UUID> bemIds;
    private Instant createdAt;
    private Instant updatedAt;
}