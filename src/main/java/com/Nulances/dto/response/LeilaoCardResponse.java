package com.Nulances.dto.response;

import com.Nulances.domain.enums.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class LeilaoCardResponse {

    private UUID leilaoId;
    private UUID leilaoLoteId;
    private UUID leilaoLoteBemId;
    private MarcaVeiculo marcaVeiculo;
    private UUID loteId;
    private UUID bemId;

    private String codigoLote;
    private StatusLeilao statusLeilao;

    private TipoVeiculo tipoVeiculo;
    private String modelo;
    private String descricao;
    private Integer ano;
    private Long quilometragem;
    private CambioVeiculo cambio;
    private CombustivelVeiculo combustivel;
    private CondicaoVeiculo condicao;

    private String cidade;
    private String endereco;

    private Instant aberturaLeilao;
    private Instant encerramentoLeilao;

    private BigDecimal valorInicial;
    private BigDecimal lanceAtual;
    private BigDecimal proximoLance;
    private BigDecimal incrementoMinimo;

    private List<MidiaResponse> midias = new ArrayList<>();

    @Getter
    @Setter
    public static class MidiaResponse {
        private UUID id;
        private TipoMidiaBem tipo;
        private String arquivo;
        private Integer ordem;
    }
}