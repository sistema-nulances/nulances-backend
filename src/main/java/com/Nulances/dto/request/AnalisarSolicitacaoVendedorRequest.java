package com.Nulances.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AnalisarSolicitacaoVendedorRequest {

    @NotBlank(message = "Observação é obrigatória.")
    private String observacao;
}