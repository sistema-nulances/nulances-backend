package com.Nulances.controller;

import com.Nulances.dto.response.LeilaoItemDetalheResponse;
import com.Nulances.dto.response.LeilaoPainelResponse;
import com.Nulances.dto.response.LeilaoResponse;
import com.Nulances.service.LeilaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/leiloes")
@RequiredArgsConstructor
public class LeilaoController {

    private final LeilaoService leilaoService;

    @GetMapping
    public List<LeilaoResponse> listarTodos() {
        return leilaoService.listarTodosPublico();
    }

    @GetMapping("/{id}")
    public LeilaoResponse buscarPorId(@PathVariable UUID id) {
        return leilaoService.buscarPorIdPublico(id);
    }

    @GetMapping("/itens/{itemId}")
    public LeilaoItemDetalheResponse buscarItemPorId(@PathVariable UUID itemId) {
        return leilaoService.buscarItemPorIdPublico(itemId);
    }

    @GetMapping("/{id}/painel")
    public LeilaoPainelResponse buscarPainel(@PathVariable UUID id) {
        return leilaoService.buscarPainel(id);
    }
}