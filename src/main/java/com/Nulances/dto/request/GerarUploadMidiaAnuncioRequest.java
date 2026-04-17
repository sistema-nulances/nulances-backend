package com.Nulances.dto.request;

import com.Nulances.domain.enums.TipoMidiaAnuncio;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GerarUploadMidiaAnuncioRequest {

    @NotNull(message = "Tipo da mídia é obrigatório.")
    private TipoMidiaAnuncio tipo;

    @NotBlank(message = "Nome do arquivo é obrigatório.")
    private String nomeArquivo;

    @NotBlank(message = "Content-Type é obrigatório.")
    private String contentType;
}