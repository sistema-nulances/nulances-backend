package com.Nulances.dto.request;

import java.time.LocalDate;

public record AdminUsuarioUpdateRequest(
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
        Boolean emailVerificado
) {
}