package com.Nulances.dto.request;

import com.Nulances.domain.enums.TipoDocumentoSolicitacaoVendedor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GerarUploadDocumentoVendedorRequest {

    @NotNull(message = "Tipo do documento é obrigatório.")
    private TipoDocumentoSolicitacaoVendedor tipoDocumento;

    @NotBlank(message = "Nome do arquivo é obrigatório.")
    private String nomeArquivo;

    @NotBlank(message = "Content-Type é obrigatório.")
    private String contentType;
}