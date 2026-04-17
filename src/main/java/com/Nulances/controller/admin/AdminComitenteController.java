package com.Nulances.controller.admin;

import com.Nulances.dto.request.ComitenteCreateRequest;
import com.Nulances.dto.request.ComitenteUpdateRequest;
import com.Nulances.dto.response.ComitenteDisponibilidadeResponse;
import com.Nulances.dto.response.ComitenteListResponse;
import com.Nulances.dto.response.ComitenteResponse;
import com.Nulances.dto.response.ComitenteStatsResponse;
import com.Nulances.service.ComitenteService;
import com.Nulances.service.disponibilidade.ComitenteDisponibilidadeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/admin/comitentes")
@RequiredArgsConstructor
public class AdminComitenteController {

    private final ComitenteService comitenteService;
    private final ComitenteDisponibilidadeService comitenteDisponibilidadeService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ComitenteResponse criar(@Valid @RequestBody ComitenteCreateRequest request) {
        return comitenteService.criar(request);
    }

    @GetMapping
    public List<ComitenteListResponse> listarTodos() {
        return comitenteService.listarTodos();
    }

    @GetMapping("/{id}")
    public ComitenteResponse buscarPorId(@PathVariable UUID id) {
        return comitenteService.buscarPorId(id);
    }

    @PatchMapping("/{id}")
    public ComitenteResponse editarParcial(
            @PathVariable UUID id,
            @RequestBody ComitenteUpdateRequest request
    ) {
        return comitenteService.editarParcial(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable UUID id) {
        comitenteService.excluir(id);
    }

    @GetMapping("/stats")
    public ComitenteStatsResponse buscarStats() {
        return comitenteService.buscarStats();
    }

    @GetMapping("/disponibilidade")
    public ComitenteDisponibilidadeResponse verificarDisponibilidade(
            @RequestParam(required = false) String documento
    ) {
        return comitenteDisponibilidadeService.verificarDisponibilidade(documento);
    }
}