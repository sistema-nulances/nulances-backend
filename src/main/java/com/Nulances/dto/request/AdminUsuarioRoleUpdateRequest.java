package com.Nulances.dto.request;

import com.Nulances.domain.enums.UserRole;
import jakarta.validation.constraints.NotNull;

public record AdminUsuarioRoleUpdateRequest(
        @NotNull(message = "O cargo é obrigatório.")
        UserRole role
) {
}