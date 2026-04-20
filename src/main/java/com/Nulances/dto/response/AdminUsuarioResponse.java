package com.Nulances.dto.response;

import com.Nulances.domain.enums.UserRole;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record AdminUsuarioResponse(
        UUID id,
        String nomeCompleto,
        LocalDate dataNascimento,
        String email,
        String cpf,
        String telefone,
        String fotoPerfil,
        String cep,
        String logradouro,
        String cidade,
        String estado,
        Boolean emailVerificado,
        Instant emailVerificadoEm,
        UserRole role,
        Instant createdAt,
        Instant updatedAt
) {
}