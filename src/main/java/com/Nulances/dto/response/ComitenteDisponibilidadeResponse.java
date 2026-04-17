package com.Nulances.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ComitenteDisponibilidadeResponse {
    private boolean documentoDisponivel;
    private String mensagemDocumento;
}