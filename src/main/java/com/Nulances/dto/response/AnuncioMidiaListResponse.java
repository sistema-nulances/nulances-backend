package com.Nulances.dto.response;

import com.Nulances.domain.enums.TipoMidiaAnuncio;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class AnuncioMidiaListResponse {

    private UUID id;
    private TipoMidiaAnuncio tipo;
    private String arquivo;
    private String url;
    private Integer ordem;
}