package com.Nulances.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RecusarSolicitacaoVendedorRequest {

    @NotBlank(message = "A observação da recusa é obrigatória.")
    private String observacao;
}