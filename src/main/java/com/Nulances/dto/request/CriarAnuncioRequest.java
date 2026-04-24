package com.Nulances.dto.request;

import com.Nulances.domain.enums.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class CriarAnuncioRequest {

    @NotNull(message = "Categoria é obrigatória.")
    private CategoriaAnuncio categoria;

    private MarcaVeiculo marca;

    @NotBlank(message = "Modelo é obrigatório.")
    private String modelo;

    @NotNull(message = "Preço é obrigatório.")
    @DecimalMin(value = "0.01", message = "Preço deve ser maior que zero.")
    private BigDecimal preco;

    @NotBlank(message = "Cidade é obrigatória.")
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

    @NotBlank(message = "Descrição é obrigatória.")
    private String descricao;

    @Valid
    private AnuncioDetalheTecnicoRequest detalheTecnico;

    @Valid
    @NotEmpty(message = "Informe ao menos uma mídia.")
    private List<AnuncioMidiaRequest> midias;
}