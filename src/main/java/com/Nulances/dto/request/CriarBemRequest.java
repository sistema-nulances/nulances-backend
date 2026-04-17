package com.Nulances.dto.request;

import com.Nulances.domain.enums.CambioVeiculo;
import com.Nulances.domain.enums.CombustivelVeiculo;
import com.Nulances.domain.enums.CondicaoVeiculo;
import com.Nulances.domain.enums.MarcaVeiculo;
import com.Nulances.domain.enums.TipoVeiculo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CriarBemRequest {

    @NotNull(message = "Marca é obrigatória")
    private MarcaVeiculo marca;

    @NotBlank(message = "Modelo é obrigatório")
    private String modelo;

    @NotNull(message = "Tipo do veículo é obrigatório")
    private TipoVeiculo tipoVeiculo;

    @NotNull(message = "Condição do veículo é obrigatória")
    private CondicaoVeiculo condicao;

    @NotNull(message = "Ano é obrigatório")
    private Integer ano;

    private Integer quilometragem;
    private String finalChassi;

    @NotNull(message = "Combustível é obrigatório")
    private CombustivelVeiculo combustivel;

    @NotNull(message = "Câmbio é obrigatório")
    private CambioVeiculo cambio;

    private Boolean blindado;
    private String cor;
    private String placaVeiculo;
    private String descricao;
}