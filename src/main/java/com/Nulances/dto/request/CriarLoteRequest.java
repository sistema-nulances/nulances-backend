package com.Nulances.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class CriarLoteRequest {

    @NotBlank(message = "Nome é obrigatório.")
    private String nome;

    private String observacoes;

    @NotEmpty(message = "Informe ao menos um bem para o lote.")
    private List<UUID> bemIds;
}