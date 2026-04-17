package com.Nulances.dto.request;

import com.Nulances.domain.enums.TipoComitente;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ComitenteUpdateRequest {

    private String nome;
    private TipoComitente tipo;
    private String documento;
    private Boolean ativoPlataforma;
    private String sede;
}