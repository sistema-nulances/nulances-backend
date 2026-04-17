package com.Nulances.service;

import com.Nulances.domain.entity.Bem;
import com.Nulances.domain.entity.BemMidia;
import com.Nulances.domain.enums.StatusBem;
import com.Nulances.dto.request.ConfirmarUploadBemMidiaRequest;
import com.Nulances.dto.request.GerarUploadBemMidiaRequest;
import com.Nulances.dto.response.BemMidiaResponse;
import com.Nulances.dto.response.UploadBemMidiaResponse;
import com.Nulances.helpers.BemMidiaHelper;
import com.Nulances.mapper.BemMidiaMapper;
import com.Nulances.repository.BemMidiaRepository;
import com.Nulances.repository.BemRepository;
import com.Nulances.storage.R2Properties;
import com.Nulances.storage.R2Service;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BemMidiaService {

    private static final long URL_UPLOAD_EXPIRATION = 300L;

    private final BemRepository bemRepository;
    private final BemMidiaRepository bemMidiaRepository;
    private final R2Service r2Service;
    private final R2Properties r2Properties;
    private final BemMidiaHelper bemMidiaHelper;
    private final BemMidiaMapper bemMidiaMapper;

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(readOnly = true)
    public UploadBemMidiaResponse gerarUploadUrl(UUID bemId, GerarUploadBemMidiaRequest request) {
        Bem bem = buscarBem(bemId);
        validarPodeAlterarMidias(bem);

        bemMidiaHelper.validarArquivo(request.getContentType(), request.getNomeArquivo());

        String extensao = bemMidiaHelper.extrairExtensao(request.getNomeArquivo());
        String objectKey = bemMidiaHelper.montarObjectKey(
                bem.getId(),
                request.getTipo().name(),
                extensao
        );

        String uploadUrl = r2Service.gerarUrlUpload(
                r2Properties.bucket(),
                objectKey,
                request.getContentType(),
                URL_UPLOAD_EXPIRATION
        );

        UploadBemMidiaResponse response = new UploadBemMidiaResponse();
        response.setObjectKey(objectKey);
        response.setUploadUrl(uploadUrl);
        response.setExpiresInSeconds(URL_UPLOAD_EXPIRATION);
        return response;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public BemMidiaResponse confirmarUpload(UUID bemId, ConfirmarUploadBemMidiaRequest request) {
        Bem bem = buscarBem(bemId);
        validarPodeAlterarMidias(bem);

        if (request.getTipo() == null) {
            throw new IllegalArgumentException("Tipo da mídia é obrigatório");
        }

        if (request.getObjectKey() == null || request.getObjectKey().isBlank()) {
            throw new IllegalArgumentException("Object key é obrigatória");
        }

        BemMidia midia = new BemMidia();
        midia.setBem(bem);
        midia.setTipo(request.getTipo());
        midia.setArquivo(request.getObjectKey());
        midia.setOrdem(request.getOrdem() != null ? request.getOrdem() : 0);

        BemMidia salva = bemMidiaRepository.save(midia);
        return bemMidiaMapper.toResponse(salva);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void excluir(UUID bemId, UUID midiaId) {
        Bem bem = buscarBem(bemId);
        validarPodeAlterarMidias(bem);

        BemMidia midia = bemMidiaRepository.findById(midiaId)
                .orElseThrow(() -> new IllegalArgumentException("Mídia não encontrada"));

        if (!midia.getBem().getId().equals(bemId)) {
            throw new IllegalArgumentException("A mídia informada não pertence ao bem");
        }

        bemMidiaRepository.delete(midia);
    }

    private Bem buscarBem(UUID bemId) {
        return bemRepository.findById(bemId)
                .orElseThrow(() -> new IllegalArgumentException("Bem não encontrado"));
    }

    private void validarPodeAlterarMidias(Bem bem) {
        if (bem.getStatus() != StatusBem.DISPONIVEL) {
            throw new IllegalArgumentException("Só é possível alterar mídias quando o bem estiver DISPONIVEL");
        }
    }
}