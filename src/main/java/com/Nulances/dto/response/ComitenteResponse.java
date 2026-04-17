package com.Nulances.dto.response;

import com.Nulances.domain.enums.TipoComitente;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
public class ComitenteResponse {

    private UUID id;
    private String nome;
    private TipoComitente tipo;
    private String documento;
    private Boolean ativoPlataforma;
    private String sede;
    private Instant createdAt;
    private Instant updatedAt;
}