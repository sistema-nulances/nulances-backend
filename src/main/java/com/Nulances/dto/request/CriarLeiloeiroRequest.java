package com.Nulances.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CriarLeiloeiroRequest {

    @NotBlank(message = "Nome é obrigatório.")
    private String nome;

    @NotBlank(message = "Registro profissional é obrigatório.")
    private String registroProfissional;

    @NotBlank(message = "CPF é obrigatório.")
    private String cpf;

    @NotBlank(message = "Email é obrigatório.")
    @Email(message = "Email inválido.")
    private String email;

    private String telefone;
    private String local;
}