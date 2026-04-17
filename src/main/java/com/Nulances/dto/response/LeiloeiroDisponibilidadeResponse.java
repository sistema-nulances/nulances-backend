package com.Nulances.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LeiloeiroDisponibilidadeResponse {
    private boolean registroProfissionalDisponivel;
    private boolean cpfDisponivel;
    private boolean emailDisponivel;
    private String mensagemRegistroProfissional;
    private String mensagemCpf;
    private String mensagemEmail;
}