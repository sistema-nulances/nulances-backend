package com.Nulances.service;

import com.Nulances.domain.entity.Bem;
import com.Nulances.domain.entity.BemMidia;
import com.Nulances.domain.entity.Marca;
import com.Nulances.domain.enums.MarcaVeiculo;
import com.Nulances.domain.enums.StatusBem;
import com.Nulances.dto.request.CriarBemRequest;
import com.Nulances.dto.request.EditarBemRequest;
import com.Nulances.dto.response.BemMidiaResponse;
import com.Nulances.dto.response.BemResponse;
import com.Nulances.dto.response.BemResumoResponse;
import com.Nulances.mapper.BemMapper;
import com.Nulances.mapper.BemMidiaMapper;
import com.Nulances.repository.BemMidiaRepository;
import com.Nulances.repository.BemRepository;
import com.Nulances.repository.MarcaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BemService {

    private final BemRepository bemRepository;
    private final BemMidiaRepository bemMidiaRepository;
    private final MarcaRepository marcaRepository;
    private final BemMapper bemMapper;
    private final BemMidiaMapper bemMidiaMapper;

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public BemResponse criar(CriarBemRequest request) {
        Marca marca = buscarMarca(request.getMarca());

        Bem bem = new Bem();
        aplicarCamposCriacao(request, bem, marca);
        bem.setLote(null);
        bem.setStatus(StatusBem.DISPONIVEL);

        Bem salvo = bemRepository.save(bem);
        return montarResponse(salvo);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(readOnly = true)
    public Page<BemResumoResponse> listar(String busca, StatusBem status, Pageable pageable) {
        Page<Bem> page;

        boolean temBusca = busca != null && !busca.isBlank();
        boolean temStatus = status != null;

        if (temBusca && temStatus) {
            page = bemRepository.findByStatusAndModeloContainingIgnoreCase(status, busca.trim(), pageable);
        } else if (temBusca) {
            page = bemRepository.findByModeloContainingIgnoreCase(busca.trim(), pageable);
        } else if (temStatus) {
            page = bemRepository.findByStatus(status, pageable);
        } else {
            page = bemRepository.findAll(pageable);
        }

        Map<UUID, List<BemMidia>> midiasPorBem = buscarMidiasAgrupadasPorBem(page.getContent());

        return page.map(bem -> {
            BemResumoResponse resumo = bemMapper.toResumoResponse(bem);
            List<BemMidia> lista = midiasPorBem.getOrDefault(bem.getId(), Collections.emptyList());
            List<BemMidiaResponse> dtos = lista.stream().map(bemMidiaMapper::toResponse).toList();
            resumo.setMidias(dtos);
            return resumo;
        });
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(readOnly = true)
    public BemResponse buscarPorId(UUID id) {
        return montarResponse(buscarBem(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public BemResponse editar(UUID id, EditarBemRequest request) {
        Bem bem = buscarBem(id);

        validarPodeEditarOuExcluir(bem);

        aplicarCamposEdicao(request, bem);

        Bem salvo = bemRepository.save(bem);
        return montarResponse(salvo);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void excluir(UUID id) {
        Bem bem = buscarBem(id);
        bemMidiaRepository.deleteByBemId(bem.getId());
        bemRepository.delete(bem);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public BemResponse retirarDoLote(UUID id) {
        Bem bem = buscarBem(id);

        validarPodeRetirarDoLote(bem);

        bem.setLote(null);
        bem.setStatus(StatusBem.DISPONIVEL);

        Bem salvo = bemRepository.save(bem);
        return montarResponse(salvo);
    }

    private Map<UUID, List<BemMidia>> buscarMidiasAgrupadasPorBem(List<Bem> bens) {
        if (bens == null || bens.isEmpty()) {
            return Map.of();
        }
        List<UUID> ids = bens.stream().map(Bem::getId).toList();
        List<BemMidia> todas = bemMidiaRepository.findAllByBemIdIn(ids);

        Map<UUID, List<BemMidia>> mapa = new LinkedHashMap<>();
        for (BemMidia m : todas) {
            UUID bemId = m.getBem().getId();
            mapa.computeIfAbsent(bemId, k -> new ArrayList<>()).add(m);
        }
        return mapa;
    }

    private Bem buscarBem(UUID id) {
        return bemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Bem não encontrado"));
    }

    private Marca buscarMarca(MarcaVeiculo marcaVeiculo) {
        if (marcaVeiculo == null) {
            throw new IllegalArgumentException("Marca é obrigatória");
        }

        return marcaRepository.findByNome(marcaVeiculo)
                .orElseThrow(() -> new IllegalArgumentException("Marca não encontrada"));
    }

    private void validarPodeEditarOuExcluir(Bem bem) {
        if (bem.getStatus() != StatusBem.DISPONIVEL) {
            throw new IllegalArgumentException("Só é possível editar ou excluir o bem quando o status for DISPONIVEL");
        }
    }

    private void validarPodeRetirarDoLote(Bem bem) {
        if (bem.getStatus() != StatusBem.EM_LOTE) {
            throw new IllegalArgumentException("Só é possível retirar o bem do lote quando o status for EM_LOTE");
        }
    }

    private void aplicarCamposCriacao(CriarBemRequest request, Bem bem, Marca marca) {
        bem.setMarca(marca);
        bem.setModelo(request.getModelo().trim());
        bem.setTipoVeiculo(request.getTipoVeiculo());
        bem.setCondicao(request.getCondicao());
        bem.setAno(request.getAno());
        bem.setQuilometragem(Long.valueOf(request.getQuilometragem()));
        bem.setFinalChassi(normalizarTexto(request.getFinalChassi()));
        bem.setCombustivel(request.getCombustivel());
        bem.setCambio(request.getCambio());
        bem.setBlindado(request.getBlindado() != null ? request.getBlindado() : false);
        bem.setCor(normalizarTexto(request.getCor()));
        bem.setPlacaVeiculo(normalizarTexto(request.getPlacaVeiculo()));
        bem.setDescricao(normalizarTexto(request.getDescricao()));
    }

    private void aplicarCamposEdicao(EditarBemRequest request, Bem bem) {
        if (request.getMarca() != null) {
            Marca marca = buscarMarca(request.getMarca());
            bem.setMarca(marca);
        }

        if (request.getModelo() != null) {
            if (request.getModelo().isBlank()) {
                throw new IllegalArgumentException("Modelo não pode ser vazio");
            }
            bem.setModelo(request.getModelo().trim());
        }

        if (request.getTipoVeiculo() != null) {
            bem.setTipoVeiculo(request.getTipoVeiculo());
        }

        if (request.getCondicao() != null) {
            bem.setCondicao(request.getCondicao());
        }

        if (request.getAno() != null) {
            bem.setAno(request.getAno());
        }

        if (request.getQuilometragem() != null) {
            bem.setQuilometragem(Long.valueOf(request.getQuilometragem()));
        }

        if (request.getFinalChassi() != null) {
            bem.setFinalChassi(normalizarTexto(request.getFinalChassi()));
        }

        if (request.getCombustivel() != null) {
            bem.setCombustivel(request.getCombustivel());
        }

        if (request.getCambio() != null) {
            bem.setCambio(request.getCambio());
        }

        if (request.getBlindado() != null) {
            bem.setBlindado(request.getBlindado());
        }

        if (request.getCor() != null) {
            bem.setCor(normalizarTexto(request.getCor()));
        }

        if (request.getPlacaVeiculo() != null) {
            bem.setPlacaVeiculo(normalizarTexto(request.getPlacaVeiculo()));
        }

        if (request.getDescricao() != null) {
            bem.setDescricao(normalizarTexto(request.getDescricao()));
        }
    }

    private String normalizarTexto(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        return valor.trim();
    }

    private BemResponse montarResponse(Bem bem) {
        List<BemMidia> midias = bemMidiaRepository.findByBemIdOrderByOrdemAscCreatedAtAsc(bem.getId());
        return bemMapper.toResponse(bem, midias);
    }
}