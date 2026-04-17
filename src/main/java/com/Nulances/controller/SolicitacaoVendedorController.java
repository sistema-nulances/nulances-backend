package com.Nulances.controller;

import com.Nulances.config.security.CustomUserDetails;
import com.Nulances.domain.entity.Usuario;
import com.Nulances.dto.request.GerarUploadDocumentoVendedorRequest;
import com.Nulances.dto.request.SolicitarAcessoVendedorRequest;
import com.Nulances.dto.response.SolicitacaoVendedorResponse;
import com.Nulances.dto.response.UploadDocumentoVendedorResponse;
import com.Nulances.repository.UsuarioRepository;
import com.Nulances.service.MarketplaceDocumentoUploadService;
import com.Nulances.service.SolicitacaoVendedorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/marketplace/vendedor")
@RequiredArgsConstructor
public class SolicitacaoVendedorController {

    private final SolicitacaoVendedorService solicitacaoVendedorService;
    private final MarketplaceDocumentoUploadService marketplaceDocumentoUploadService;
    private final UsuarioRepository usuarioRepository;

    @PostMapping("/documentos/upload-url")
    public UploadDocumentoVendedorResponse gerarUploadUrl(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody GerarUploadDocumentoVendedorRequest request
    ) {
        Usuario usuario = buscarUsuarioAutenticado(userDetails);
        return marketplaceDocumentoUploadService.gerarUpload(usuario, request);
    }

    @PostMapping("/solicitar")
    public SolicitacaoVendedorResponse solicitar(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody SolicitarAcessoVendedorRequest request
    ) {
        Usuario usuario = buscarUsuarioAutenticado(userDetails);
        return solicitacaoVendedorService.solicitar(usuario, request);
    }

    @GetMapping("/minha-solicitacao")
    public SolicitacaoVendedorResponse buscarMinhaSolicitacao(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Usuario usuario = buscarUsuarioAutenticado(userDetails);
        return solicitacaoVendedorService.buscarMinhaSolicitacao(usuario);
    }

    private Usuario buscarUsuarioAutenticado(CustomUserDetails userDetails) {
        return usuarioRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Usuário autenticado não encontrado."));
    }
}