package com.Nulances.controller.admin;

import com.Nulances.dto.request.LeilaoCreateRequest;
import com.Nulances.dto.response.LeilaoCardResponse;
import com.Nulances.dto.response.LeilaoPainelResponse;
import com.Nulances.dto.response.LeilaoResponse;
import com.Nulances.service.LeilaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/admin/leiloes")
@RequiredArgsConstructor
public class AdminLeilaoController {

    private final LeilaoService leilaoService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LeilaoResponse criar(@Valid @RequestBody LeilaoCreateRequest request) {
        return leilaoService.criar(request);
    }

    @GetMapping
    public List<LeilaoResponse> listarTodos() {
        return leilaoService.listarTodosAdmin();
    }

    @GetMapping("/{id}")
    public LeilaoResponse buscarPorId(@PathVariable UUID id) {
        return leilaoService.buscarPorIdAdmin(id);
    }

    @GetMapping("/cards")
    public List<LeilaoCardResponse> listarCards() {
        return leilaoService.listarCardsPlataforma();
    }

    @GetMapping("/{id}/painel")
    public LeilaoPainelResponse buscarPainel(@PathVariable UUID id) {
        return leilaoService.buscarPainel(id);
    }
}