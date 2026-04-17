package com.Nulances.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ContatoLoteResponse {
    private String nome;
    private String email;
    private String telefone;
}