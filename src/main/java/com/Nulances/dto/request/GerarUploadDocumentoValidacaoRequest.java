package com.Nulances.dto.request;

import com.Nulances.domain.enums.TipoDocumentoValidacao;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GerarUploadDocumentoValidacaoRequest {
    private TipoDocumentoValidacao tipo;
    private String nomeArquivo;
    private String contentType;
}