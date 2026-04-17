package com.Nulances.controller.admin;

import com.Nulances.domain.enums.StatusDocumentoValidacao;
import com.Nulances.dto.request.AtualizarStatusDocumentoValidacaoRequest;
import com.Nulances.dto.response.DocumentoValidacaoAdminResponse;
import com.Nulances.service.DocumentoValidacaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/admin/documentos-validacao")
@RequiredArgsConstructor
public class AdminDocumentoValidacaoController {

    private final DocumentoValidacaoService documentoValidacaoService;

    @GetMapping
    public List<DocumentoValidacaoAdminResponse> listarPorStatus(
            @RequestParam(defaultValue = "PENDENTE") StatusDocumentoValidacao status
    ) {
        return documentoValidacaoService.listarPorStatus(status);
    }

    @PatchMapping("/{id}/status")
    public DocumentoValidacaoAdminResponse atualizarStatus(
            @PathVariable UUID id,
            @RequestBody AtualizarStatusDocumentoValidacaoRequest request
    ) {
        return documentoValidacaoService.atualizarStatus(id, request);
    }
}