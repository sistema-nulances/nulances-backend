package com.Nulances.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class AdminPlanoUpdateRequest {

    @DecimalMin(value = "0.01", message = "Valor mensal deve ser maior que zero.")
    private BigDecimal valorMensal;

    @Min(value = 1, message = "Total de anúncios deve ser no mínimo 1.")
    private Integer totalAnuncios;

    private Boolean ativo;
}
