package com.Nulances.controller.admin;

import com.Nulances.dto.response.FaturaPlanoResponse;
import com.Nulances.payment.service.FaturamentoPlanoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/admin/marketplace/faturamento")
@RequiredArgsConstructor
public class AdminMarketplaceFaturamentoController {

    private final FaturamentoPlanoService faturamentoPlanoService;

    @GetMapping
    public List<FaturaPlanoResponse> listarFaturamento(
            @RequestParam(required = false) UUID vendedorId
    ) {
        return faturamentoPlanoService.listarFaturamentoAdmin(vendedorId);
    }
}
