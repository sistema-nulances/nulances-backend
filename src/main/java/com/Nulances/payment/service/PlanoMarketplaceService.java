package com.Nulances.payment.service;

import com.Nulances.domain.entity.PlanoAnuncio;
import com.Nulances.dto.request.AdminPlanoUpdateRequest;
import com.Nulances.dto.response.PlanoAnuncioResponse;
import com.Nulances.repository.PlanoAnuncioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PlanoMarketplaceService {

    private final PlanoAnuncioRepository planoAnuncioRepository;

    @Transactional(readOnly = true)
    public List<PlanoAnuncioResponse> listarAtivos() {
        return planoAnuncioRepository.findAllByAtivoTrueOrderByValorMensalAsc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(readOnly = true)
    public List<PlanoAnuncioResponse> listarTodosParaAdmin() {
        return planoAnuncioRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public PlanoAnuncioResponse atualizarPlano(UUID planoId, AdminPlanoUpdateRequest request) {
        PlanoAnuncio plano = planoAnuncioRepository.findById(planoId)
                .orElseThrow(() -> new IllegalArgumentException("Plano não encontrado."));

        if (request.getValorMensal() != null) {
            plano.setValorMensal(request.getValorMensal());
        }

        if (request.getTotalAnuncios() != null) {
            plano.setTotalAnuncios(request.getTotalAnuncios());
        }

        if (request.getAtivo() != null) {
            plano.setAtivo(request.getAtivo());
        }

        return toResponse(planoAnuncioRepository.save(plano));
    }

    public PlanoAnuncio buscarPlanoAtivo(UUID planoId) {
        PlanoAnuncio plano = buscarPlanoPorId(planoId);
        if (!Boolean.TRUE.equals(plano.getAtivo())) {
            throw new IllegalArgumentException("Plano selecionado está inativo.");
        }
        return plano;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(readOnly = true)
    public PlanoAnuncio buscarPlanoPorId(UUID planoId) {
        return planoAnuncioRepository.findById(planoId)
                .orElseThrow(() -> new IllegalArgumentException("Plano não encontrado."));
    }

    private PlanoAnuncioResponse toResponse(PlanoAnuncio plano) {
        return PlanoAnuncioResponse.builder()
                .id(plano.getId())
                .nome(plano.getNome())
                .descricao(plano.getDescricao())
                .valorMensal(plano.getValorMensal())
                .totalAnuncios(plano.getTotalAnuncios())
                .ilimitado(plano.getIlimitado())
                .ativo(plano.getAtivo())
                .build();
    }
}
