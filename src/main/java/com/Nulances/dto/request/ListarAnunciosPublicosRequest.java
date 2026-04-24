package com.Nulances.dto.request;

import com.Nulances.domain.enums.CambioVeiculo;
import com.Nulances.domain.enums.CategoriaAnuncio;
import com.Nulances.domain.enums.CombustivelVeiculo;
import com.Nulances.domain.enums.CondicaoAnuncioVeiculo;
import com.Nulances.domain.enums.TipoVeiculo;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ListarAnunciosPublicosRequest {

    private String busca;
    private CategoriaAnuncio categoria;
    private TipoVeiculo tipo;
    private CondicaoAnuncioVeiculo condicao;
    private CombustivelVeiculo combustivel;
    private CambioVeiculo cambio;
}
