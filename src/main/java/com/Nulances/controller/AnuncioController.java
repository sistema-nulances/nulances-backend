package com.Nulances.controller;

import com.Nulances.config.security.CustomUserDetails;
import com.Nulances.dto.request.CriarAnuncioRequest;
import com.Nulances.dto.request.EditarAnuncioRequest;
import com.Nulances.dto.request.GerarUploadMidiaAnuncioRequest;
import com.Nulances.dto.request.ListarAnunciosPublicosRequest;
import com.Nulances.dto.request.ListarMeusAnunciosRequest;
import com.Nulances.dto.request.SuspenderAnuncioRequest;
import com.Nulances.dto.response.AnuncioPublicoDetalheResponse;
import com.Nulances.dto.response.AnuncioPublicoListResponse;
import com.Nulances.dto.response.AnuncioResponse;
import com.Nulances.dto.response.AnuncioStatusResponse;
import com.Nulances.dto.response.AnuncioVendedorListResponse;
import com.Nulances.dto.response.UploadMidiaAnuncioResponse;
import com.Nulances.service.AnuncioMidiaUploadService;
import com.Nulances.service.AnuncioModerarService;
import com.Nulances.service.AnuncioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/marketplace/anuncios")
@RequiredArgsConstructor
public class AnuncioController {

    private final AnuncioService anuncioService;
    private final AnuncioMidiaUploadService anuncioMidiaUploadService;
    private final AnuncioModerarService anuncioModerarService;

    @GetMapping
    public Page<AnuncioPublicoListResponse> listarPublicos(
            @ModelAttribute ListarAnunciosPublicosRequest request,
            Pageable pageable
    ) {
        return anuncioService.listarPublicos(request, pageable);
    }

    @GetMapping("/{id}")
    public AnuncioPublicoDetalheResponse buscarPublicadoPorId(@PathVariable UUID id) {
        return anuncioService.buscarPublicadoPorId(id);
    }

    @PostMapping("/midias/upload-url")
    public UploadMidiaAnuncioResponse gerarUploadUrl(
            @Valid @RequestBody GerarUploadMidiaAnuncioRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return anuncioMidiaUploadService.gerarUploadUrl(request, userDetails);
    }

    @PostMapping
    public AnuncioResponse criar(
            @Valid @RequestBody CriarAnuncioRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return anuncioService.criar(request, userDetails);
    }

    @GetMapping("/meus")
    public Page<AnuncioVendedorListResponse> listarMeusAnuncios(
            @ModelAttribute ListarMeusAnunciosRequest request,
            Pageable pageable,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return anuncioService.listarMeusAnuncios(request, pageable, userDetails);
    }

    @GetMapping("/meus/{id}")
    public AnuncioResponse buscarMeuAnuncioPorId(
            @PathVariable UUID id,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return anuncioService.buscarMeuAnuncioPorId(id, userDetails);
    }

    @PatchMapping("/meus/{id}")
    public AnuncioResponse editarParcial(
            @PathVariable UUID id,
            @RequestBody EditarAnuncioRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return anuncioService.editarParcial(id, request, userDetails);
    }

    @PatchMapping("/meus/{id}/suspender")
    public AnuncioStatusResponse suspenderMeuAnuncio(
            @PathVariable UUID id,
            @RequestBody(required = false) SuspenderAnuncioRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return anuncioModerarService.suspenderMeuAnuncio(id, request, userDetails);
    }

    @DeleteMapping("/meus/{id}")
    public void excluirMeuAnuncio(
            @PathVariable UUID id,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        anuncioService.excluirMeuAnuncio(id, userDetails);
    }
}