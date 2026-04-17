package com.Nulances.dto.websocket;

import com.Nulances.dto.response.LeilaoPainelResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LeilaoPainelAtualizadoEvent {
    private String leilaoId;
    private LeilaoPainelResponse painel;
}