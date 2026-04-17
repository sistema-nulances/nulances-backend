package com.Nulances.dto.response;

import com.Nulances.domain.enums.StatusAnuncio;
import lombok.Builder;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Builder
public class AnuncioStatusResponse {

    private UUID id;
    private StatusAnuncio status;
    private String mensagem;
    private String motivo;
    private OffsetDateTime atualizadoEm;
}