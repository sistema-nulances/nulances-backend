package com.Nulances.dto.messaging;

import java.io.Serializable;
import java.util.UUID;

public record EmailConfirmacaoMessage(
        UUID usuarioId,
        String email,
        String nome,
        String codigo
) implements Serializable {
}
