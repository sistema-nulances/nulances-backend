package com.Nulances.dto.request;

import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EditarLeiloeiroRequest {

    private String nome;
    private String registroProfissional;
    private String cpf;

    @Email(message = "Email inválido.")
    private String email;

    private String telefone;
    private String local;
    private Boolean ativoPlataforma;
}