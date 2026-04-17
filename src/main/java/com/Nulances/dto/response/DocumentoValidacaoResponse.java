package com.Nulances.dto.response;

import com.Nulances.domain.enums.StatusDocumentoValidacao;
import com.Nulances.domain.enums.TipoDocumentoValidacao;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
public class DocumentoValidacaoResponse {
    private UUID id;
    private TipoDocumentoValidacao tipo;
    private String arquivo;
    private String arquivoUrl;
    private StatusDocumentoValidacao status;
    private Instant createdAt;
    private Instant updatedAt;
}