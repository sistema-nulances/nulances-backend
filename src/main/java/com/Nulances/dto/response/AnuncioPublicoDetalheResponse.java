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
public class AnuncioPublicoDetalheResponse {

    private UUID id;
    private CategoriaAnuncio categoria;
    private MarcaVeiculo marcaVeiculo;
    private String modelo;
    private BigDecimal preco;
    private String cidade;
    private TipoVeiculo tipoVeiculo;
    private Boolean blindado;
    private Long quilometragem;
    private Integer ano;
    private String cor;
    private CombustivelVeiculo combustivel;
    private CambioVeiculo cambio;
    private String descricao;
    private CondicaoAnuncioVeiculo condicao;
    private AnuncioPublicoDetalheTecnicoResponse detalheTecnico;
    private AnuncioPublicoVendedorResponse vendedor;
    private List<AnuncioPublicoMidiaResponse> imagens;
}