package com.Nulances.dto.request;

import com.Nulances.domain.enums.CambioVeiculo;
import com.Nulances.domain.enums.CategoriaAnuncio;
import com.Nulances.domain.enums.CombustivelVeiculo;
import com.Nulances.domain.enums.CondicaoAnuncioVeiculo;
import com.Nulances.domain.enums.MarcaVeiculo;
import com.Nulances.domain.enums.TipoVeiculo;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class EditarAnuncioRequest {

    private CategoriaAnuncio categoria;
    private MarcaVeiculo marca;
    private String modelo;
    private BigDecimal preco;
    private String cidade;
    private TipoVeiculo tipo;
    private CondicaoAnuncioVeiculo condicao;
    private Integer ano;
    private Long quilometragem;
    private CombustivelVeiculo combustivel;
    private CambioVeiculo cambio;
    private String finalChassi;
    private String cor;
    private Boolean blindado;
    private String placaVeiculo;
    private String descricao;

    @Valid
    private EditarAnuncioDetalheTecnicoRequest detalheTecnico;

    /**
     * Novas mídias a anexar ao anúncio (upload já realizado; enviar {@code objectKey} retornado pelo endpoint de upload).
     * Itens são acrescentados às mídias existentes, com ordem sequencial após a última.
     */
    @Valid
    private List<AnuncioMidiaRequest> midiasAdicionar;
}