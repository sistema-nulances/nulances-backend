package com.Nulances.service;

import com.Nulances.domain.entity.Comitente;
import com.Nulances.dto.request.ComitenteCreateRequest;
import com.Nulances.dto.request.ComitenteUpdateRequest;
import com.Nulances.dto.response.ComitenteListResponse;
import com.Nulances.dto.response.ComitenteResponse;
import com.Nulances.dto.response.ComitenteStatsResponse;
import com.Nulances.mapper.ComitenteMapper;
import com.Nulances.repository.ComitenteRepository;
import com.Nulances.repository.LeilaoRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ComitenteService {

    private final ComitenteRepository comitenteRepository;
    private final LeilaoRepository leilaoRepository;
    private final ComitenteMapper comitenteMapper;

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public ComitenteResponse criar(ComitenteCreateRequest request) {
        validarDocumentoDuplicado(request.getDocumento(), null);

        Comitente comitente = comitenteMapper.toEntity(request);
        Comitente salvo = comitenteRepository.save(comitente);

        return comitenteMapper.toResponse(salvo);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<ComitenteListResponse> listarTodos() {
        return comitenteRepository.findAllForList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public ComitenteResponse buscarPorId(UUID id) {
        Comitente comitente = buscarEntidadePorId(id);
        return comitenteMapper.toResponse(comitente);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public ComitenteResponse editarParcial(UUID id, ComitenteUpdateRequest request) {
        Comitente comitente = buscarEntidadePorId(id);

        if (request.getDocumento() != null && !request.getDocumento().isBlank()) {
            validarDocumentoDuplicado(request.getDocumento(), id);
        }

        comitenteMapper.updateEntity(comitente, request);

        Comitente atualizado = comitenteRepository.save(comitente);
        return comitenteMapper.toResponse(atualizado);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void excluir(UUID id) {
        Comitente comitente = buscarEntidadePorId(id);

        leilaoRepository.desvincularComitenteDosLeiloes(id);
        comitenteRepository.delete(comitente);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public ComitenteStatsResponse buscarStats() {
        return comitenteRepository.getStats();
    }

    private Comitente buscarEntidadePorId(UUID id) {
        return comitenteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Comitente não encontrado."));
    }

    private void validarDocumentoDuplicado(String documento, UUID idIgnorado) {
        if (documento == null || documento.isBlank()) {
            throw new IllegalArgumentException("Documento é obrigatório.");
        }

        if (idIgnorado == null) {
            if (comitenteRepository.existsByDocumento(documento)) {
                throw new IllegalArgumentException("Já existe um comitente com este documento.");
            }
            return;
        }

        if (comitenteRepository.existsByDocumentoAndIdNot(documento, idIgnorado)) {
            throw new IllegalArgumentException("Já existe outro comitente com este documento.");
        }
    }
}