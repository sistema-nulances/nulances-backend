package com.Nulances.dto.request;

import com.Nulances.domain.enums.TipoComitente;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ComitenteCreateRequest {

    @NotBlank(message = "Nome é obrigatório.")
    private String nome;

    @NotNull(message = "Tipo é obrigatório.")
    private TipoComitente tipo;

    @NotBlank(message = "Documento é obrigatório.")
    private String documento;

    private Boolean ativoPlataforma;

    private String sede;
}