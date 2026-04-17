package com.Nulances.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ComitenteResumoResponse {
    private String id;
    private String nome;
    private String tipo; // BANCO, SEGURADORA, EMPRESA, PESSOA_FISICA
}