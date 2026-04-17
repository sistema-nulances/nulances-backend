package com.Nulances.controller.admin;

import com.Nulances.dto.request.CriarLeiloeiroRequest;
import com.Nulances.dto.request.EditarLeiloeiroRequest;
import com.Nulances.dto.response.LeiloeiroDisponibilidadeResponse;
import com.Nulances.dto.response.LeiloeiroListResponse;
import com.Nulances.dto.response.LeiloeiroResponse;
import com.Nulances.dto.response.LeiloeiroStatsResponse;
import com.Nulances.service.LeiloeiroService;
import com.Nulances.service.disponibilidade.LeiloeiroDisponibilidadeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/admin/leiloeiros")
@RequiredArgsConstructor
public class AdminLeiloeiroController {

    private final LeiloeiroService leiloeiroService;
    private final LeiloeiroDisponibilidadeService leiloeiroDisponibilidadeService;

    @PostMapping
    public LeiloeiroResponse criar(@RequestBody @Valid CriarLeiloeiroRequest request) {
        return leiloeiroService.criar(request);
    }

    @GetMapping
    public List<LeiloeiroListResponse> listar() {
        return leiloeiroService.listar();
    }

    @GetMapping("/stats")
    public LeiloeiroStatsResponse listarStatsLeiloeiro() {
        return leiloeiroService.listarStatsLeiloeiro();
    }

    @GetMapping("/{id}")
    public LeiloeiroResponse buscarPorId(@PathVariable UUID id) {
        return leiloeiroService.buscarPorId(id);
    }

    @PutMapping("/{id}")
    public LeiloeiroResponse editar(@PathVariable UUID id, @RequestBody @Valid EditarLeiloeiroRequest request) {
        return leiloeiroService.editar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable UUID id) {
        leiloeiroService.excluir(id);
    }

    @GetMapping("/disponibilidade")
    public LeiloeiroDisponibilidadeResponse verificarDisponibilidade(
            @RequestParam(required = false) String registroProfissional,
            @RequestParam(required = false) String cpf,
            @RequestParam(required = false) String email
    ) {
        return leiloeiroDisponibilidadeService.verificarDisponibilidade(
                registroProfissional,
                cpf,
                email
        );
    }
}