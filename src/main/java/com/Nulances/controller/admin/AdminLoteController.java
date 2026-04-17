package com.Nulances.controller.admin;

import com.Nulances.dto.request.CriarLoteRequest;
import com.Nulances.dto.request.EditarLoteRequest;
import com.Nulances.dto.response.LoteListResponse;
import com.Nulances.dto.response.LoteResponse;
import com.Nulances.dto.response.LoteStatsResponse;
import com.Nulances.service.LoteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/admin/lotes")
@RequiredArgsConstructor
public class AdminLoteController {

    private final LoteService loteService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LoteResponse criar(@RequestBody @Valid CriarLoteRequest request) {
        return loteService.criar(request);
    }

    @GetMapping
    public List<LoteListResponse> listar() {
        return loteService.listarParaAdmin();
    }

    @GetMapping("/{id}")
    public LoteResponse buscarPorId(@PathVariable UUID id) {
        return loteService.buscarPorId(id);
    }

    @PutMapping("/{id}")
    public LoteResponse editar(@PathVariable UUID id, @RequestBody @Valid EditarLoteRequest request) {
        return loteService.editar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable UUID id) {
        loteService.excluir(id);
    }

    @GetMapping("/stats")
    public LoteStatsResponse buscarStats() {
        return loteService.buscarStats();
    }
}