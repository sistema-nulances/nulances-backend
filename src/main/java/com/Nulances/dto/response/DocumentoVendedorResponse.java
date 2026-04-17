package com.Nulances.dto.response;

import com.Nulances.domain.enums.TipoDocumentoVendedor;

import java.time.Instant;
import java.util.UUID;

public record DocumentoVendedorResponse(
        UUID id,
        TipoDocumentoVendedor tipo,
        String arquivo,
        Instant createdAt,
        Instant updatedAt
) {
}
