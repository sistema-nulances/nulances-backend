package com.Nulances.dto.response;

import com.Nulances.domain.enums.CambioVeiculo;
import com.Nulances.domain.enums.CategoriaAnuncio;
import com.Nulances.domain.enums.CombustivelVeiculo;
import com.Nulances.domain.enums.CondicaoAnuncioVeiculo;
import com.Nulances.domain.enums.MarcaVeiculo;
import com.Nulances.domain.enums.TipoVeiculo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class AnuncioPublicoListResponse {

    private UUID id;
    private CategoriaAnuncio categoria;
    private String modelo;
    private String descricao;
    private MarcaVeiculo marcaVeiculo;
    private BigDecimal preco;
    private String cidade;
    private TipoVeiculo tipoVeiculo;
    private CondicaoAnuncioVeiculo condicao;
    private Integer ano;
    private Long quilometragem;
    private CombustivelVeiculo combustivel;
    private CambioVeiculo cambio;
    private List<AnuncioPublicoMidiaResponse> imagens;
}