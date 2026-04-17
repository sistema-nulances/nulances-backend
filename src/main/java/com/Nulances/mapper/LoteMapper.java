package com.Nulances.mapper;

import com.Nulances.domain.entity.Bem;
import com.Nulances.domain.entity.LeilaoLote;
import com.Nulances.domain.entity.Lote;
import com.Nulances.dto.response.LoteListResponse;
import com.Nulances.dto.response.LoteResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class LoteMapper {

    public LoteResponse toResponse(Lote lote) {
        LoteResponse dto = new LoteResponse();
        dto.setId(lote.getId());
        dto.setNome(lote.getNome());
        dto.setCodigo(lote.getCodigo());
        dto.setObservacoes(lote.getObservacoes());
        dto.setStatus(lote.getStatus());
        dto.setCreatedAt(lote.getCreatedAt());
        dto.setUpdatedAt(lote.getUpdatedAt());

        List<UUID> bemIds = lote.getBens()
                .stream()
                .map(Bem::getId)
                .toList();

        dto.setBemIds(bemIds);

        return dto;
    }

    public LoteListResponse toListResponse(Lote lote) {
        LoteListResponse dto = new LoteListResponse();
        dto.setId(lote.getId());
        dto.setCodigo(lote.getCodigo());
        dto.setNome(lote.getNome());
        dto.setStatus(lote.getStatus());
        dto.setTotalBens(lote.getBens() != null ? lote.getBens().size() : 0);
        dto.setNomeLeilao(extrairNomeLeilao(lote));
        return dto;
    }

    private String extrairNomeLeilao(Lote lote) {
        if (lote.getLeilaoLotes() == null || lote.getLeilaoLotes().isEmpty()) {
            return "-";
        }

        LeilaoLote leilaoLote = lote.getLeilaoLotes().get(0);

        if (leilaoLote.getLeilao() == null || leilaoLote.getLeilao().getTitulo() == null) {
            return "-";
        }

        return leilaoLote.getLeilao().getTitulo();
    }
}