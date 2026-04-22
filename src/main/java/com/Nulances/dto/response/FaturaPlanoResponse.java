package com.Nulances.dto.response;

import com.Nulances.domain.enums.StatusPagamentoPlano;
import com.Nulances.domain.enums.TipoPagamentoPlano;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@Builder
public class FaturaPlanoResponse {
    private UUID pagamentoId;
    private String referencia;
    private String plano;
    private BigDecimal valor;
    private StatusPagamentoPlano status;
    private TipoPagamentoPlano tipo;
    private Instant dataVencimento;
    private Instant pagoEm;
    private String checkoutUrl;
}
