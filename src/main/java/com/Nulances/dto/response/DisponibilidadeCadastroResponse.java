package com.Nulances.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DisponibilidadeCadastroResponse {

    private boolean emailDisponivel;
    private boolean cpfDisponivel;
    private boolean telefoneDisponivel;

    private String mensagemEmail;
    private String mensagemCpf;
    private String mensagemTelefone;
}