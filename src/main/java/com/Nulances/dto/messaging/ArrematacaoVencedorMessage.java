package com.Nulances.dto.messaging;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

public record ArrematacaoVencedorMessage(
        UUID arrematacaoId,
        UUID usuarioId,
        String email,
        String nomeUsuario,
        String tituloLeilao,
        String codigoLote,
        String nomeBem,
        BigDecimal valorFinal
) implements Serializable {
}
