package com.Nulances.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AtualizarPerfilRequest {
    private String telefone;
    private String fotoPerfil;
    private String cep;
    private String logradouro;
    private String cidade;
    private String estado;
}