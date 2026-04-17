package com.Nulances.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AnuncioPublicoVendedorResponse {

    private String nome;
    private String cidade;
    private String fotoPerfil;
    private String fotoPerfilUrl;
    private String sobre;
    private String telefoneContato;
}