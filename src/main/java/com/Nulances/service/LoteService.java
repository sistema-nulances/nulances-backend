package com.Nulances.service;

import com.Nulances.domain.entity.Bem;
import com.Nulances.domain.entity.Lote;
import com.Nulances.domain.enums.StatusBem;
import com.Nulances.domain.enums.StatusLote;
import com.Nulances.dto.request.CriarLoteRequest;
import com.Nulances.dto.request.EditarLoteRequest;
import com.Nulances.dto.response.LoteListResponse;
import com.Nulances.dto.response.LoteResponse;
import com.Nulances.dto.response.LoteStatsResponse;
import com.Nulances.helpers.LoteCodigoGenerator;
import com.Nulances.mapper.LoteMapper;
import com.Nulances.repository.BemRepository;
import com.Nulances.repository.LoteRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LoteService {

    private final LoteRepository loteRepository;
    private final BemRepository bemRepository;
    private final LoteCodigoGenerator loteCodigoGenerator;
    private final LoteMapper loteMapper;

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public LoteResponse criar(CriarLoteRequest dto) {
        List<Bem> bens = buscarEValidarBens(dto.getBemIds());

        Lote lote = new Lote();
        lote.setNome(dto.getNome().trim());
        lote.setObservacoes(normalizarObservacoes(dto.getObservacoes()));
        lote.setCodigo(loteCodigoGenerator.gerarCodigoUnico());
        lote.setStatus(StatusLote.DISPONIVEL);

        lote = loteRepository.save(lote);

        for (Bem bem : bens) {
            bem.setLote(lote);
            bem.setStatus(StatusBem.EM_LOTE);
        }

        bemRepository.saveAll(bens);
        lote.setBens(new ArrayList<>(bens));

        return loteMapper.toResponse(lote);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(readOnly = true)
    public List<LoteListResponse> listarParaAdmin() {
        return loteRepository.findAll()
                .stream()
                .map(loteMapper::toListResponse)
                .toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(readOnly = true)
    public LoteResponse buscarPorId(UUID id) {
        return loteMapper.toResponse(buscarLotePorId(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public LoteResponse editar(UUID id, EditarLoteRequest dto) {
        Lote lote = buscarLotePorId(id);

        validarPodeEditarOuExcluir(lote);

        if (dto.getNome() != null) {
            if (dto.getNome().isBlank()) {
                throw new IllegalArgumentException("O nome do lote não pode ser vazio.");
            }
            lote.setNome(dto.getNome().trim());
        }

        if (dto.getObservacoes() != null) {
            lote.setObservacoes(normalizarObservacoes(dto.getObservacoes()));
        }

        if (dto.getBemIds() != null) {
            atualizarBensDoLote(lote, dto.getBemIds());
        }

        lote = loteRepository.save(lote);
        return loteMapper.toResponse(lote);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void excluir(UUID id) {
        Lote lote = buscarLotePorId(id);

        validarPodeEditarOuExcluir(lote);

        List<Bem> bens = lote.getBens() == null ? List.of() : new ArrayList<>(lote.getBens());

        for (Bem bem : bens) {
            bem.setLote(null);
            bem.setStatus(StatusBem.DISPONIVEL);
        }

        if (!bens.isEmpty()) {
            bemRepository.saveAll(bens);
        }

        loteRepository.delete(lote);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(readOnly = true)
    public LoteStatsResponse buscarStats() {
        LoteStatsResponse dto = new LoteStatsResponse();
        dto.setTotalLotes(loteRepository.count());
        dto.setTotalDisponiveis(loteRepository.countByStatus(StatusLote.DISPONIVEL));
        dto.setTotalEmLeilao(loteRepository.countByStatus(StatusLote.EM_LEILAO));
        dto.setTotalEncerrados(loteRepository.countByStatus(StatusLote.ENCERRADO));
        return dto;
    }

    private Lote buscarLotePorId(UUID id) {
        return loteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Lote não encontrado."));
    }

    private List<Bem> buscarEValidarBens(List<UUID> bemIds) {
        List<Bem> bens = bemRepository.findAllByIdIn(bemIds);

        if (bens.isEmpty()) {
            throw new EntityNotFoundException("Nenhum bem encontrado para os IDs informados.");
        }

        if (bens.size() != bemIds.size()) {
            throw new IllegalArgumentException("Um ou mais bens informados não existem.");
        }

        for (Bem bem : bens) {
            if (bem.getLote() != null) {
                throw new IllegalStateException("O bem de ID " + bem.getId() + " já está vinculado a um lote.");
            }

            if (bem.getStatus() != StatusBem.DISPONIVEL) {
                throw new IllegalStateException(
                        "O bem de ID " + bem.getId() + " só pode ser adicionado ao lote se estiver com status DISPONIVEL."
                );
            }
        }

        return bens;
    }

    private List<Bem> buscarEValidarBensParaEdicao(List<UUID> bemIds, UUID loteId) {
        if (bemIds.isEmpty()) {
            return new ArrayList<>();
        }

        List<Bem> bens = bemRepository.findAllByIdIn(bemIds);

        if (bens.size() != bemIds.size()) {
            throw new IllegalArgumentException("Um ou mais bens informados não existem.");
        }

        for (Bem bem : bens) {
            boolean pertenceAoMesmoLote = bem.getLote() != null && bem.getLote().getId().equals(loteId);

            if (bem.getLote() != null && !pertenceAoMesmoLote) {
                throw new IllegalStateException("O bem de ID " + bem.getId() + " já está vinculado a outro lote.");
            }

            if (!pertenceAoMesmoLote && bem.getStatus() != StatusBem.DISPONIVEL) {
                throw new IllegalStateException(
                        "O bem de ID " + bem.getId() + " só pode ser adicionado ao lote se estiver com status DISPONIVEL."
                );
            }
        }

        return bens;
    }

    private void atualizarBensDoLote(Lote lote, List<UUID> bemIds) {
        List<Bem> bensAtuais = lote.getBens() == null ? List.of() : new ArrayList<>(lote.getBens());
        List<Bem> novosBens = buscarEValidarBensParaEdicao(bemIds, lote.getId());

        for (Bem bem : bensAtuais) {
            bem.setLote(null);
            bem.setStatus(StatusBem.DISPONIVEL);
        }

        if (!bensAtuais.isEmpty()) {
            bemRepository.saveAll(bensAtuais);
        }

        for (Bem bem : novosBens) {
            bem.setLote(lote);
            bem.setStatus(StatusBem.EM_LOTE);
        }

        if (!novosBens.isEmpty()) {
            bemRepository.saveAll(novosBens);
        }

        lote.setBens(new ArrayList<>(novosBens));
    }

    private void validarPodeEditarOuExcluir(Lote lote) {
        if (lote.getStatus() != StatusLote.DISPONIVEL) {
            throw new IllegalStateException("Somente lotes com status DISPONIVEL podem ser editados ou excluídos.");
        }
    }

    private String normalizarObservacoes(String observacoes) {
        if (observacoes == null || observacoes.isBlank()) {
            return null;
        }
        return observacoes.trim();
    }
}