package com.Nulances.controller.admin;

import com.Nulances.dto.request.AdminUsuarioRoleUpdateRequest;
import com.Nulances.dto.request.AdminUsuarioUpdateRequest;
import com.Nulances.dto.response.AdminUsuarioListResponse;
import com.Nulances.dto.response.AdminUsuarioResponse;
import com.Nulances.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/admin/usuarios")
@RequiredArgsConstructor
public class AdminUsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<Page<AdminUsuarioListResponse>> listar(
            @RequestParam(required = false) String busca,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        return ResponseEntity.ok(usuarioService.listar(busca, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdminUsuarioResponse> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(usuarioService.buscarPorId(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<AdminUsuarioResponse> editarParcial(
            @PathVariable UUID id,
            @RequestBody AdminUsuarioUpdateRequest request
    ) {
        return ResponseEntity.ok(usuarioService.editarParcial(id, request));
    }

    @PatchMapping("/{id}/cargo")
    public ResponseEntity<AdminUsuarioResponse> alterarCargo(
            @PathVariable UUID id,
            @Valid @RequestBody AdminUsuarioRoleUpdateRequest request
    ) {
        return ResponseEntity.ok(usuarioService.alterarCargo(id, request));
    }
}