package com.Nulances.dto.request;

import com.Nulances.domain.enums.TipoMidiaBem;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GerarUploadBemMidiaRequest {

    private TipoMidiaBem tipo;
    private String nomeArquivo;
    private String contentType;
    private Integer ordem;
}