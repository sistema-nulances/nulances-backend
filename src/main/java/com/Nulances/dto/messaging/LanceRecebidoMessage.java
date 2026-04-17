package com.Nulances.dto.messaging;

import java.math.BigDecimal;
import java.util.UUID;

public record LanceRecebidoMessage(
        UUID leilaoLoteBemId,
        UUID usuarioId,
        BigDecimal valor,
        String clientRequestId
) {
}