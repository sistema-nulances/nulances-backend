package com.Nulances.controller;

import com.Nulances.dto.response.LeiloesGanhosResponse;
import com.Nulances.service.LeiloesGanhosService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/leiloes/ganhos")
@RequiredArgsConstructor
public class LeiloesGanhosController {

    private final LeiloesGanhosService leiloesGanhosService;

    @GetMapping("/me")
    public LeiloesGanhosResponse listarMeusLeiloesGanhos(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), 100);

        Pageable pageable = PageRequest.of(
                safePage,
                safeSize,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        return leiloesGanhosService.listarMeusGanhos(authentication, pageable);
    }
}