package com.Nulances.service;

import com.Nulances.domain.entity.BemMidia;
import com.Nulances.domain.entity.Lance;
import com.Nulances.domain.entity.Leilao;
import com.Nulances.domain.entity.LeilaoLoteBem;
import com.Nulances.domain.entity.Usuario;
import com.Nulances.domain.enums.FormatoLeilao;
import com.Nulances.domain.enums.StatusItemLeilao;
import com.Nulances.domain.enums.StatusLeilao;
import com.Nulances.dto.response.MeuLanceParticipacaoResponse;
import com.Nulances.dto.response.MeusLancesListaResponse;
import com.Nulances.dto.response.ResultadoParticipacaoUsuarioLeilao;
import com.Nulances.repository.LanceRepository;
import com.Nulances.repository.UsuarioRepository;
import com.Nulances.storage.R2Properties;
import com.Nulances.storage.R2Service;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MeusLancesQueryService {

    private static final long MIDIA_URL_EXPIRES_SECONDS = 900L;

    private final LanceRepository lanceRepository;
    private final UsuarioRepository usuarioRepository;
    private final R2Service r2Service;
    private final R2Properties r2Properties;

    @Transactional(readOnly = true)
    public MeusLancesListaResponse listarMeusLances(Authentication authentication) {
        Usuario usuario = buscarUsuarioAutenticado(authentication);

        List<Lance> lances = lanceRepository.findAllForMeusLancesParticipacao(usuario.getId());
        if (lances.isEmpty()) {
            MeusLancesListaResponse empty = new MeusLancesListaResponse();
            empty.setItens(List.of());
            empty.setTotalElements(0);
            return empty;
        }

        Map<UUID, List<Lance>> porItem = lances.stream()
                .collect(Collectors.groupingBy(l -> l.getLeilaoLoteBem().getId()));

        List<MeuLanceParticipacaoResponse> itens = porItem.values().stream()
                .map(grupo -> montarParticipacao(grupo, usuario.getId()))
                .sorted(Comparator.comparing(MeuLanceParticipacaoResponse::getMeuLanceEm).reversed())
                .toList();

        MeusLancesListaResponse out = new MeusLancesListaResponse();
        out.setItens(itens);
        out.setTotalElements(itens.size());
        return out;
    }

    private MeuLanceParticipacaoResponse montarParticipacao(List<Lance> grupo, UUID usuarioId) {
        Lance melhor = grupo.stream()
                .max(Comparator.comparing(Lance::getValor, Comparator.nullsLast(BigDecimal::compareTo))
                        .thenComparing(l -> l.getCreatedAt() != null ? l.getCreatedAt() : java.time.Instant.EPOCH))
                .orElseThrow();

        LeilaoLoteBem item = melhor.getLeilaoLoteBem();
        Leilao leilao = item.getLeilaoLote().getLeilao();

        MeuLanceParticipacaoResponse r = new MeuLanceParticipacaoResponse();
        r.setLeilaoLoteBemId(item.getId());
        r.setLanceId(melhor.getId());
        r.setMeuValor(melhor.getValor());
        r.setMeuLanceEm(melhor.getCreatedAt());

        r.setLeilaoId(leilao.getId());
        r.setTituloLeilao(leilao.getTitulo());
        r.setFormatoLeilao(leilao.getFormato() != null ? leilao.getFormato().name() : null);
        r.setStatusLeilao(leilao.getStatus() != null ? leilao.getStatus().name() : null);
        r.setCidade(resolveCidade(leilao));

        r.setCodigoLote(item.getLeilaoLote().getLote().getCodigo());
        r.setNomeBem(item.getBem().getModelo());
        r.setTipoVeiculo(item.getBem().getTipoVeiculo() != null ? item.getBem().getTipoVeiculo().name() : null);
        r.setStatusItem(item.getStatus() != null ? item.getStatus().name() : null);
        r.setValorAtual(item.getValorAtual());
        r.setAberturaDisputa(item.getAberturaDisputa());
        r.setEncerramentoDisputa(item.getEncerramentoDisputa());

        r.setMidiaCapaUrl(resolverCapaAssinada(item));
        r.setResultadoParticipacao(calcularResultado(item, usuarioId));
        r.setQuantidadeLancesMeuUsuario(grupo.size());

        return r;
    }

    private String resolveCidade(Leilao leilao) {
        if (leilao.getFormato() == FormatoLeilao.PRESENCIAL) {
            return leilao.getCidade();
        }
        return null;
    }

    private String resolverCapaAssinada(LeilaoLoteBem item) {
        List<BemMidia> midias = item.getBem().getMidias();
        if (midias == null || midias.isEmpty()) {
            return null;
        }
        BemMidia capa = midias.stream()
                .min(Comparator.comparing(BemMidia::getOrdem, Comparator.nullsLast(Integer::compareTo)))
                .orElse(midias.get(0));

        String key = capa.getArquivo();
        if (key == null || key.isBlank()) {
            return null;
        }
        return r2Service.gerarUrlDownload(
                r2Properties.bucket(),
                key.trim(),
                MIDIA_URL_EXPIRES_SECONDS
        );
    }

    private ResultadoParticipacaoUsuarioLeilao calcularResultado(LeilaoLoteBem item, UUID usuarioId) {
        StatusItemLeilao s = item.getStatus();
        if (s == StatusItemLeilao.ABERTO
                || s == StatusItemLeilao.AGUARDANDO_ABERTURA
                || s == StatusItemLeilao.PROCESSANDO_RESULTADO) {
            return ResultadoParticipacaoUsuarioLeilao.EM_DISPUTA;
        }

        Lance maior = item.getMaiorLance();
        if (maior != null && maior.getUsuario() != null
                && Objects.equals(maior.getUsuario().getId(), usuarioId)) {
            return ResultadoParticipacaoUsuarioLeilao.GANHADOR;
        }
        return ResultadoParticipacaoUsuarioLeilao.NAO_GANHADOR;
    }

    private Usuario buscarUsuarioAutenticado(Authentication authentication) {
        if (authentication == null || authentication.getName() == null || authentication.getName().isBlank()) {
            throw new IllegalStateException("Usuário não autenticado.");
        }
        return usuarioRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new EntityNotFoundException("Usuário autenticado não encontrado."));
    }
}