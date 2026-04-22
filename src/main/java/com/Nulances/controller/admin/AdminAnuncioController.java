package com.Nulances.controller.admin;

import com.Nulances.config.security.CustomUserDetails;
import com.Nulances.dto.request.EditarAnuncioRequest;
import com.Nulances.dto.request.ListarAdminAnunciosRequest;
import com.Nulances.dto.request.SuspenderAnuncioRequest;
import com.Nulances.dto.response.AnuncioAdminListResponse;
import com.Nulances.dto.response.AnuncioModerarListResponse;
import com.Nulances.dto.response.AnuncioResponse;
import com.Nulances.dto.response.AnuncioStatusResponse;
import com.Nulances.service.AnuncioModerarService;
import com.Nulances.service.AnuncioService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/admin/anuncios")
@RequiredArgsConstructor
public class AdminAnuncioController {

    private final AnuncioModerarService anuncioModerarService;
    private final AnuncioService anuncioService;

    @GetMapping("/moderar/dashboard")
    public Page<AnuncioModerarListResponse> listarFilaModeracaoDashboard(
            Pageable pageable,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return anuncioModerarService.listarFilaModeracaoDashboard(pageable, userDetails);
    }

    @GetMapping
    public Page<AnuncioAdminListResponse> listar(
            @ModelAttribute ListarAdminAnunciosRequest request,
            Pageable pageable,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return anuncioModerarService.listarParaAdmin(request, pageable, userDetails);
    }

    @GetMapping("/{id}")
    public AnuncioResponse buscarPorId(
            @PathVariable UUID id,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return anuncioService.buscarAnuncioPorIdParaAdmin(id, userDetails);
    }

    @PatchMapping("/{id}")
    public AnuncioResponse editarParcial(
            @PathVariable UUID id,
            @RequestBody EditarAnuncioRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return anuncioService.editarParcialParaAdmin(id, request, userDetails);
    }

    @PatchMapping("/{id}/aprovar")
    public AnuncioStatusResponse aprovar(
            @PathVariable UUID id,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return anuncioModerarService.aprovar(id, userDetails);
    }

    @PatchMapping("/{id}/suspender")
    public AnuncioStatusResponse suspender(
            @PathVariable UUID id,
            @RequestBody(required = false) SuspenderAnuncioRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return anuncioModerarService.suspender(id, request, userDetails);
    }

    @PatchMapping("/{id}/reativar")
    public AnuncioStatusResponse reativar(
            @PathVariable UUID id,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return anuncioModerarService.reativar(id, userDetails);
    }
}