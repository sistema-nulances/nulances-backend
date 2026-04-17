package com.Nulances.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class LeiloeiroListResponse {

    private UUID id;
    private String nome;
    private Boolean ativoPlataforma;
    private String registroProfissional;
    private String cpf;
    private String email;
    private String telefone;
    private String local;
    private Long totalLeiloes;

    public LeiloeiroListResponse(
            UUID id,
            String nome,
            Boolean ativoPlataforma,
            String registroProfissional,
            String cpf,
            String email,
            String telefone,
            String local,
            Long totalLeiloes
    ) {
        this.id = id;
        this.nome = nome;
        this.ativoPlataforma = ativoPlataforma;
        this.registroProfissional = registroProfissional;
        this.cpf = cpf;
        this.email = email;
        this.telefone = telefone;
        this.local = local;
        this.totalLeiloes = totalLeiloes;
    }
}