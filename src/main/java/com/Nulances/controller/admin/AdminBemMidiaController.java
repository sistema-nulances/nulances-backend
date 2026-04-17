package com.Nulances.controller.admin;

import com.Nulances.dto.request.ConfirmarUploadBemMidiaRequest;
import com.Nulances.dto.request.GerarUploadBemMidiaRequest;
import com.Nulances.dto.response.BemMidiaResponse;
import com.Nulances.dto.response.UploadBemMidiaResponse;
import com.Nulances.service.BemMidiaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/admin/bens/{bemId}/midias")
@RequiredArgsConstructor
public class AdminBemMidiaController {

    private final BemMidiaService bemMidiaService;

    @PostMapping("/upload-url")
    public UploadBemMidiaResponse gerarUploadUrl(
            @PathVariable UUID bemId,
            @RequestBody GerarUploadBemMidiaRequest request
    ) {
        return bemMidiaService.gerarUploadUrl(bemId, request);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BemMidiaResponse confirmarUpload(
            @PathVariable UUID bemId,
            @RequestBody ConfirmarUploadBemMidiaRequest request
    ) {
        return bemMidiaService.confirmarUpload(bemId, request);
    }

    @DeleteMapping("/{midiaId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(
            @PathVariable UUID bemId,
            @PathVariable UUID midiaId
    ) {
        bemMidiaService.excluir(bemId, midiaId);
    }
}