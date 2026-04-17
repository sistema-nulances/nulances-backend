package com.Nulances.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
public class LeilaoLoteBemCreateRequest {

    @NotNull(message = "Bem é obrigatório.")
    private UUID bemId;

    @NotNull(message = "Valor inicial é obrigatório.")
    @DecimalMin(value = "0.01", message = "Valor inicial deve ser maior que zero.")
    private BigDecimal valorInicial;

    @NotNull(message = "Incremento mínimo é obrigatório.")
    @DecimalMin(value = "0.01", message = "Incremento mínimo deve ser maior que zero.")
    private BigDecimal incrementoMinimo;

    @NotNull(message = "Abertura da disputa é obrigatória.")
    private Instant aberturaDisputa;

    @NotNull(message = "Encerramento da disputa é obrigatório.")
    private Instant encerramentoDisputa;
}