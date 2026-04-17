package com.Nulances.dto.response;

import com.Nulances.domain.enums.TipoDocumentoSolicitacaoVendedor;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AdminMarketplaceDocumentoPendenteResponse {

    private TipoDocumentoSolicitacaoVendedor tipo;
    private String arquivo;
    private String urlAssinada;
}