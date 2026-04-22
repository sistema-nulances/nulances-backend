package com.Nulances.controller;

import com.Nulances.config.security.CustomUserDetails;
import com.Nulances.dto.request.AssinarPlanoRequest;
import com.Nulances.dto.response.CheckoutPlanoResponse;
import com.Nulances.dto.response.PainelPlanosVendedorResponse;
import com.Nulances.payment.service.AssinaturaPlanoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/vendedor/planos")
@RequiredArgsConstructor
public class VendedorPlanoController {

    private final AssinaturaPlanoService assinaturaPlanoService;

    @GetMapping
    public PainelPlanosVendedorResponse buscarPainel(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return assinaturaPlanoService.buscarPainelPlanos(userDetails);
    }

    @PostMapping("/assinar")
    public CheckoutPlanoResponse assinarPlano(
            @Valid @RequestBody AssinarPlanoRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return assinaturaPlanoService.assinarPlano(request, userDetails);
    }
}
