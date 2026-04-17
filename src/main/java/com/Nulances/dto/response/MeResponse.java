package com.Nulances.dto.response;

import com.Nulances.domain.enums.UserRole;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record MeResponse(
        UUID id,
        String nomeCompleto,
        String email,
        String telefone,
        String fotoPerfil,
        String cpf,
        String cep,
        String logradouro,
        String cidade,
        String estado,
        Boolean emailVerificado,
        Instant emailVerificadoEm,
        UserRole role,
        Instant createdAt,
        Instant updatedAt,
        List<DocumentoValidacaoResponse> documentosValidacao,
        List<DocumentoVendedorResponse> documentosVendedor
) {
}