package com.Nulances.service;

import com.Nulances.domain.entity.LeilaoLoteBem;
import com.Nulances.domain.enums.FormatoLeilao;
import com.Nulances.dto.response.AdminDashboardLeiloesResponse;
import com.Nulances.repository.LeilaoLoteBemRepository;
import com.Nulances.repository.LeilaoRepository;
import com.Nulances.repository.LoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardLeilaoService {

    private final LoteRepository loteRepository;
    private final LeilaoRepository leilaoRepository;
    private final LeilaoLoteBemRepository leilaoLoteBemRepository;

    @Transactional(readOnly = true)
    public AdminDashboardLeiloesResponse obterResumo(int limit) {
        Instant agora = Instant.now();

        long totalLotesCadastrados = loteRepository.count();
        long totalLeiloesAoVivo = leilaoRepository.countAoVivo(agora);
        long totalLeiloesEmBreve = leilaoRepository.countEmBreve(agora);

        List<AdminDashboardLeiloesResponse.LeilaoAoVivoItem> aoVivo =
                leilaoLoteBemRepository.buscarItensLeiloesAoVivo(agora, PageRequest.of(0, limit))
                        .stream()
                        .map(this::mapearItemAoVivo)
                        .toList();

        AdminDashboardLeiloesResponse out = new AdminDashboardLeiloesResponse();
        out.setTotalLotesCadastrados(totalLotesCadastrados);
        out.setTotalLeiloesAoVivo(totalLeiloesAoVivo);
        out.setTotalLeiloesEmBreve(totalLeiloesEmBreve);
        out.setLeiloesAoVivo(aoVivo);
        return out;
    }

    private AdminDashboardLeiloesResponse.LeilaoAoVivoItem mapearItemAoVivo(LeilaoLoteBem row) {
        AdminDashboardLeiloesResponse.LeilaoAoVivoItem dto = new AdminDashboardLeiloesResponse.LeilaoAoVivoItem();

        var leilaoLote = row.getLeilaoLote();
        var leilao = leilaoLote.getLeilao();
        var lote = leilaoLote.getLote();

        dto.setTituloLeilao(leilao.getTitulo());
        dto.setEncerraEm(leilao.getFimLeilao());
        dto.setLote("Lote " + lote.getCodigo());
        dto.setLocal(resolverLocal(leilao.getFormato(), leilao.getCidade()));
        dto.setStatus(row.getStatus() != null ? row.getStatus().name() : null);

        BigDecimal lanceAtual = row.getValorAtual() != null ? row.getValorAtual() : row.getValorInicial();
        dto.setLanceAtual(lanceAtual);

        return dto;
    }

    private String resolverLocal(FormatoLeilao formato, String cidade) {
        if (formato == FormatoLeilao.PRESENCIAL && cidade != null && !cidade.isBlank()) {
            return cidade.trim();
        }
        return "Online";
    }
}