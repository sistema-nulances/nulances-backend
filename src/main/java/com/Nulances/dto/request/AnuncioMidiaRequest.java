package com.Nulances.dto.request;

import com.Nulances.domain.enums.TipoMidiaAnuncio;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AnuncioMidiaRequest {

    @NotNull(message = "Tipo da mídia é obrigatório.")
    private TipoMidiaAnuncio tipo;

    @NotBlank(message = "Arquivo da mídia é obrigatório.")
    private String arquivo;

    @NotNull(message = "Ordem da mídia é obrigatória.")
    private Integer ordem;
}