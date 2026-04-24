package com.Nulances.dto.request;

import com.Nulances.domain.enums.CategoriaAnuncio;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ListarAnunciosPublicosRequest {

    private String busca;
    private CategoriaAnuncio categoria;
}