package com.Nulances.dto.response;

import com.Nulances.domain.enums.StatusDocumentoValidacao;
import com.Nulances.domain.enums.TipoDocumentoValidacao;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
public class DocumentoValidacaoAdminResponse {
    private UUID id;
    private UUID usuarioId;
    private String nomeCompleto;
    private String email;
    private String cpf;
    private TipoDocumentoValidacao tipo;
    private String arquivo;
    private String arquivoUrl;
    private StatusDocumentoValidacao status;
    private Instant createdAt;
    private Instant updatedAt;
}