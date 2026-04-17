package com.Nulances.controller.admin;

import com.Nulances.dto.response.AdminDashboardLeiloesResponse;
import com.Nulances.service.DashboardLeilaoService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/admin/dashboard")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final DashboardLeilaoService dashboardLeilaoService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/leiloes")
    public AdminDashboardLeiloesResponse obterDashboardLeiloes(
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int limit
    ) {
        return dashboardLeilaoService.obterResumo(limit);
    }
}