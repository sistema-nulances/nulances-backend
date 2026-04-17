package com.Nulances.controller.admin;

import com.Nulances.domain.enums.StatusBem;
import com.Nulances.dto.request.CriarBemRequest;
import com.Nulances.dto.request.EditarBemRequest;
import com.Nulances.dto.response.BemResponse;
import com.Nulances.dto.response.BemResumoResponse;
import com.Nulances.service.BemService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/admin/bens")
@RequiredArgsConstructor
public class AdminBemController {

    private final BemService bemService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BemResponse criar(@RequestBody CriarBemRequest request) {
        return bemService.criar(request);
    }

    @GetMapping
    public Page<BemResumoResponse> listar(
            @RequestParam(required = false) String busca,
            @RequestParam(required = false) StatusBem status,
            Pageable pageable
    ) {
        return bemService.listar(busca, status, pageable);
    }

    @GetMapping("/{id}")
    public BemResponse buscarPorId(@PathVariable UUID id) {
        return bemService.buscarPorId(id);
    }

    @PutMapping("/{id}")
    public BemResponse editar(@PathVariable UUID id, @RequestBody EditarBemRequest request) {
        return bemService.editar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable UUID id) {
        bemService.excluir(id);
    }

    @PatchMapping("/{id}/retirar-lote")
    public BemResponse retirarDoLote(@PathVariable UUID id) {
        return bemService.retirarDoLote(id);
    }
}