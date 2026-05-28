package com.Nulances.controller.admin;

import com.Nulances.config.security.CustomUserDetails;
import com.Nulances.domain.enums.StatusContaMarketplaceAdmin;
import com.Nulances.dto.request.AdminAtribuirPlanoVendedorRequest;
import com.Nulances.dto.request.RecusarSolicitacaoVendedorRequest;
import com.Nulances.dto.response.AdminMarketplaceSolicitacaoPendenteDetalheResponse;
import com.Nulances.dto.response.AdminMarketplaceVendedorListItemResponse;
import com.Nulances.dto.response.MinhaAssinaturaPlanoResponse;
import com.Nulances.payment.service.AssinaturaPlanoService;
import com.Nulances.service.AdminMarketplaceVendedorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/admin/marketplace/vendedores")
@RequiredArgsConstructor
public class AdminMarketplaceVendedorController {

    private final AdminMarketplaceVendedorService adminMarketplaceVendedorService;
    private final AssinaturaPlanoService assinaturaPlanoService;

    @GetMapping
    public List<AdminMarketplaceVendedorListItemResponse> listar(
            @RequestParam(defaultValue = "TODOS") StatusContaMarketplaceAdmin status,
            @RequestParam(required = false) String search
    ) {
        return adminMarketplaceVendedorService.listar(status, search);
    }

    @GetMapping("/pendentes/{solicitacaoId}")
    public AdminMarketplaceSolicitacaoPendenteDetalheResponse buscarDetalhePendente(
            @PathVariable UUID solicitacaoId
    ) {
        return adminMarketplaceVendedorService.buscarDetalhePendente(solicitacaoId);
    }

    @PatchMapping("/pendentes/{solicitacaoId}/aprovar")
    public void aprovarSolicitacao(
            @PathVariable UUID solicitacaoId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        adminMarketplaceVendedorService.aprovarSolicitacao(solicitacaoId, userDetails);
    }

    @PatchMapping("/pendentes/{solicitacaoId}/recusar")
    public void recusarSolicitacao(
            @PathVariable UUID solicitacaoId,
            @Valid @RequestBody RecusarSolicitacaoVendedorRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        adminMarketplaceVendedorService.recusarSolicitacao(solicitacaoId, request, userDetails);
    }

    @PatchMapping("/{usuarioId}/revogar")
    public void revogarVendedor(
            @PathVariable UUID usuarioId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        adminMarketplaceVendedorService.revogarVendedor(usuarioId, userDetails);
    }

    @GetMapping("/{usuarioId}/assinatura")
    public MinhaAssinaturaPlanoResponse buscarAssinatura(@PathVariable UUID usuarioId) {
        return assinaturaPlanoService.buscarAssinaturaVendedorAdmin(usuarioId);
    }

    @PatchMapping("/{usuarioId}/assinatura")
    public MinhaAssinaturaPlanoResponse atribuirPlano(
            @PathVariable UUID usuarioId,
            @Valid @RequestBody AdminAtribuirPlanoVendedorRequest request
    ) {
        return assinaturaPlanoService.atribuirPlanoAdmin(usuarioId, request);
    }
}