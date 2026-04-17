package com.Nulances.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GerarUploadFotoPerfilRequest {
    @NotBlank
    private String contentType;

    @NotBlank
    private String nomeArquivo;
}