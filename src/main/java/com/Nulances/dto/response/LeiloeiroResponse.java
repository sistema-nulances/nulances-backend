package com.Nulances.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
public class LeiloeiroResponse {

    private UUID id;
    private String nome;
    private String registroProfissional;
    private String cpf;
    private String email;
    private String telefone;
    private Boolean ativoPlataforma;
    private String local;
    private Long totalLeiloes;
    private Instant createdAt;
    private Instant updatedAt;
}