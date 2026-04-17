package com.Nulances.controller;

import com.Nulances.dto.request.ConfirmarUploadDocumentoValidacaoRequest;
import com.Nulances.dto.request.GerarUploadDocumentoValidacaoRequest;
import com.Nulances.dto.response.DocumentoValidacaoResponse;
import com.Nulances.dto.response.UploadDocumentoValidacaoResponse;
import com.Nulances.service.DocumentoValidacaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/documentos-validacao")
@RequiredArgsConstructor
public class DocumentoValidacaoController {

    private final DocumentoValidacaoService documentoValidacaoService;

    @PostMapping("/upload-url")
    public UploadDocumentoValidacaoResponse gerarUploadUrl(Authentication authentication,
                                                           @RequestBody GerarUploadDocumentoValidacaoRequest request) {
        return documentoValidacaoService.gerarUrlUpload(authentication, request);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DocumentoValidacaoResponse confirmarUpload(Authentication authentication,
                                                      @RequestBody ConfirmarUploadDocumentoValidacaoRequest request) {
        return documentoValidacaoService.confirmarUpload(authentication, request);
    }

    @GetMapping("/me")
    public List<DocumentoValidacaoResponse> listarMeusDocumentos(Authentication authentication) {
        return documentoValidacaoService.listarMeusDocumentos(authentication);
    }
}