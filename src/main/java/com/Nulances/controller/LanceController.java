package com.Nulances.controller;

import com.Nulances.dto.request.LanceCreateRequest;
import com.Nulances.service.LanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/lances")
@RequiredArgsConstructor
public class LanceController {

    private final LanceService lanceService;

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void enviarLance(Authentication authentication,
                            @Valid @RequestBody LanceCreateRequest request) {
        lanceService.enviarLance(authentication, request);
    }
}