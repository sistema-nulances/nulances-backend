package com.Nulances.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class LeilaoLoteCreateRequest {

    @NotNull(message = "Lote é obrigatório.")
    private UUID loteId;

    @Valid
    @NotEmpty(message = "Informe ao menos um bem para o lote.")
    private List<LeilaoLoteBemCreateRequest> bens;
}