package com.Nulances.controller;

import com.Nulances.dto.response.MeusLancesListaResponse;
import com.Nulances.service.MeusLancesQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/lances")
@RequiredArgsConstructor
public class LanceUsuarioController {

    private final MeusLancesQueryService meusLancesQueryService;

    @GetMapping("/meus")
    @PreAuthorize("isAuthenticated()")
    public MeusLancesListaResponse meusLances(Authentication authentication) {
        return meusLancesQueryService.listarMeusLances(authentication);
    }
}