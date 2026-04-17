package com.Nulances.dto.websocket;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class LanceAtualizadoEvent {

    private String leilaoLoteBemId;
    private String usuarioId;
    private BigDecimal valorAtual;
    private BigDecimal proximoLance;
}