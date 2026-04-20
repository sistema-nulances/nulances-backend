package com.Nulances.dto.response;

import com.Nulances.domain.enums.UserRole;

import java.util.UUID;

public record AdminUsuarioListResponse(
        UUID id,
        String nomeCompleto,
        String email,
        String cidade,
        UserRole role,
        String telefone
) {
}