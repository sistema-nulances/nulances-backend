package com.Nulances.controller.admin;

import com.Nulances.dto.request.AdminPlanoUpdateRequest;
import com.Nulances.dto.response.PlanoAnuncioResponse;
import com.Nulances.payment.service.PlanoMarketplaceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/admin/marketplace/planos")
@RequiredArgsConstructor
public class AdminMarketplacePlanoController {

    private final PlanoMarketplaceService planoMarketplaceService;

    @GetMapping
    public List<PlanoAnuncioResponse> listarPlanos() {
        return planoMarketplaceService.listarTodosParaAdmin();
    }

    @PatchMapping("/{planoId}")
    public PlanoAnuncioResponse atualizarPlano(
            @PathVariable UUID planoId,
            @Valid @RequestBody AdminPlanoUpdateRequest request
    ) {
        return planoMarketplaceService.atualizarPlano(planoId, request);
    }
}
