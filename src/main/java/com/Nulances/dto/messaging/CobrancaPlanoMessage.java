package com.Nulances.dto.messaging;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record CobrancaPlanoMessage(
        UUID pagamentoId,
        UUID vendedorId,
        String email,
        String nomeVendedor,
        String nomePlano,
        BigDecimal valor,
        Instant vencimento,
        String checkoutUrl
) implements Serializable {
}
