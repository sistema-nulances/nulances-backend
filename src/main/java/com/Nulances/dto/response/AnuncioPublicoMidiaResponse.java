package com.Nulances.dto.response;

import com.Nulances.domain.enums.TipoMidiaAnuncio;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AnuncioPublicoMidiaResponse {

    private TipoMidiaAnuncio tipo;
    private String arquivo;
    private String url;
    private Integer ordem;
}