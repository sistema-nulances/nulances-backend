package com.Nulances.dto.request;

import com.Nulances.domain.enums.StatusDocumentoValidacao;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AtualizarStatusDocumentoValidacaoRequest {
    private StatusDocumentoValidacao status;
}