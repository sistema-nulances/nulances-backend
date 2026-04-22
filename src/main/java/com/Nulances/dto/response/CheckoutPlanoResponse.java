package com.Nulances.dto.response;

import com.Nulances.domain.enums.StatusPagamentoPlano;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class CheckoutPlanoResponse {
    private UUID pagamentoId;
    private String referencia;
    private String checkoutUrl;
    private StatusPagamentoPlano status;
}
