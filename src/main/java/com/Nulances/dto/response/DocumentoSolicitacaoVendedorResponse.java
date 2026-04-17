package com.Nulances.dto.response;

import com.Nulances.domain.enums.TipoDocumentoSolicitacaoVendedor;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class DocumentoSolicitacaoVendedorResponse {
    private UUID id;
    private TipoDocumentoSolicitacaoVendedor tipo;
    private String arquivo;
    private String urlVisualizacao;
}