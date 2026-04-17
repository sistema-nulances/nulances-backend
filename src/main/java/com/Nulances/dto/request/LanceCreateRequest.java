package com.Nulances.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class LanceCreateRequest {

    @NotNull(message = "Item do leilão é obrigatório.")
    private UUID leilaoLoteBemId;

    @NotNull(message = "Valor do lance é obrigatório.")
    @DecimalMin(value = "0.01", message = "O valor do lance deve ser maior que zero.")
    private BigDecimal valor;

    @NotBlank(message = "clientRequestId é obrigatório.")
    private String clientRequestId;
}