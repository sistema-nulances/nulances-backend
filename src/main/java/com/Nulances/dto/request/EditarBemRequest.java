package com.Nulances.dto.request;

import com.Nulances.domain.enums.CambioVeiculo;
import com.Nulances.domain.enums.CombustivelVeiculo;
import com.Nulances.domain.enums.CondicaoVeiculo;
import com.Nulances.domain.enums.MarcaVeiculo;
import com.Nulances.domain.enums.TipoVeiculo;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EditarBemRequest {

    private MarcaVeiculo marca;
    private String modelo;
    private TipoVeiculo tipoVeiculo;
    private CondicaoVeiculo condicao;
    private Integer ano;
    private Integer quilometragem;
    private String finalChassi;
    private CombustivelVeiculo combustivel;
    private CambioVeiculo cambio;
    private Boolean blindado;
    private String cor;
    private String placaVeiculo;
    private String descricao;
}