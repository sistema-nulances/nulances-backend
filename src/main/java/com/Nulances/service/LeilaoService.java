package com.Nulances.service;

import com.Nulances.domain.entity.Bem;
import com.Nulances.domain.entity.Comitente;
import com.Nulances.domain.entity.Lance;
import com.Nulances.domain.entity.Leilao;
import com.Nulances.domain.entity.LeilaoLote;
import com.Nulances.domain.entity.LeilaoLoteBem;
import com.Nulances.domain.entity.Leiloeiro;
import com.Nulances.domain.entity.Lote;
import com.Nulances.domain.entity.Usuario;
import com.Nulances.domain.enums.FormatoLeilao;
import com.Nulances.domain.enums.StatusItemLeilao;
import com.Nulances.domain.enums.StatusLeilao;
import com.Nulances.domain.enums.StatusLote;
import com.Nulances.dto.request.LeilaoCreateRequest;
import com.Nulances.dto.response.LeilaoCardResponse;
import com.Nulances.dto.response.LeilaoItemDetalheResponse;
import com.Nulances.dto.response.LeilaoPainelResponse;
import com.Nulances.dto.response.LeilaoResponse;
import com.Nulances.mapper.LeilaoMapper;
import com.Nulances.repository.BemRepository;
import com.Nulances.repository.ComitenteRepository;
import com.Nulances.repository.LanceRepository;
import com.Nulances.repository.LeilaoLoteBemRepository;
import com.Nulances.repository.LeilaoLoteRepository;
import com.Nulances.repository.LeilaoRepository;
import com.Nulances.repository.LeiloeiroRepository;
import com.Nulances.repository.LoteRepository;
import com.Nulances.storage.R2Properties;
import com.Nulances.storage.R2Service;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.net.URI;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LeilaoService {

    private final LeilaoRepository leilaoRepository;
    private final LeilaoLoteRepository leilaoLoteRepository;
    private final LeilaoLoteBemRepository leilaoLoteBemRepository;
    private final LeiloeiroRepository leiloeiroRepository;
    private final ComitenteRepository comitenteRepository;
    private final LoteRepository loteRepository;
    private final BemRepository bemRepository;
    private final LanceRepository lanceRepository;
    private final LeilaoMapper leilaoMapper;
    private final SimpMessagingTemplate messagingTemplate;
    private static final long DOWNLOAD_EXPIRES_IN_SECONDS = 900L;
    private final R2Service r2Service;
    private final R2Properties r2Properties;

    public void publicarPainelAtualizado(UUID leilaoId) {
        LeilaoPainelResponse painel = buscarPainel(leilaoId);

        messagingTemplate.convertAndSend(
                "/topic/leiloes/" + leilaoId + "/painel",
                new com.Nulances.dto.websocket.LeilaoPainelAtualizadoEvent(
                        leilaoId.toString(),
                        painel
                )
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public LeilaoResponse criar(LeilaoCreateRequest request) {
        validarRequest(request);

        Leiloeiro leiloeiro = leiloeiroRepository.findById(request.getLeiloeiroId())
                .orElseThrow(() -> new EntityNotFoundException("Leiloeiro não encontrado."));

        Comitente comitente = comitenteRepository.findById(request.getComitenteId())
                .orElseThrow(() -> new EntityNotFoundException("Comitente não encontrado."));

        List<LeilaoCreateRequest.BemRequest> todosOsItens = request.getLotes().stream()
                .flatMap(lote -> lote.getBens().stream())
                .sorted(Comparator.comparing(LeilaoCreateRequest.BemRequest::getAberturaDisputa))
                .toList();

        validarCronologia(todosOsItens);

        Leilao leilao = new Leilao();
        leilao.setTitulo(request.getTitulo());
        leilao.setLinkLive(normalizarLinkLive(request.getLinkLive()));
        leilao.setFormato(request.getFormato());
        leilao.setCidade(request.getCidade());
        leilao.setEndereco(request.getEndereco());
        leilao.setLeiloeiro(leiloeiro);
        leilao.setComitente(comitente);
        leilao.setInicioLeilao(todosOsItens.get(0).getAberturaDisputa());
        leilao.setFimLeilao(todosOsItens.get(todosOsItens.size() - 1).getEncerramentoDisputa());
        leilao.setStatus(StatusLeilao.EM_BREVE);

        Leilao leilaoSalvo = leilaoRepository.save(leilao);

        List<LeilaoLoteBem> leilaoLoteBens = new ArrayList<>();
        List<Lote> lotesAtualizadosStatus = new ArrayList<>();

        for (LeilaoCreateRequest.LoteRequest loteRequest : request.getLotes()) {
            Lote lote = loteRepository.findById(loteRequest.getLoteId())
                    .orElseThrow(() -> new EntityNotFoundException("Lote não encontrado: " + loteRequest.getLoteId()));

            if (leilaoLoteRepository.existsByLoteId(lote.getId())) {
                throw new IllegalArgumentException("O lote " + lote.getCodigo() + " já está vinculado a outro leilão.");
            }

            if (lote.getStatus() != StatusLote.DISPONIVEL) {
                throw new IllegalArgumentException("O lote " + lote.getCodigo() + " não está disponível para leilão.");
            }

            LeilaoLote leilaoLote = new LeilaoLote();
            leilaoLote.setLeilao(leilaoSalvo);
            leilaoLote.setLote(lote);

            LeilaoLote leilaoLoteSalvo = leilaoLoteRepository.save(leilaoLote);

            lote.setStatus(StatusLote.EM_LEILAO);
            lotesAtualizadosStatus.add(lote);

            for (LeilaoCreateRequest.BemRequest bemRequest : loteRequest.getBens()) {
                Bem bem = bemRepository.findById(bemRequest.getBemId())
                        .orElseThrow(() -> new EntityNotFoundException("Bem não encontrado: " + bemRequest.getBemId()));

                if (leilaoLoteBemRepository.existsByBemId(bem.getId())) {
                    throw new IllegalArgumentException("O bem " + bem.getModelo() + " já está vinculado a outro leilão.");
                }

                if (bem.getLote() == null || !bem.getLote().getId().equals(lote.getId())) {
                    throw new IllegalArgumentException("O bem " + bem.getId() + " não pertence ao lote informado.");
                }

                LeilaoLoteBem item = new LeilaoLoteBem();
                item.setLeilaoLote(leilaoLoteSalvo);
                item.setBem(bem);
                item.setValorInicial(bemRequest.getValorInicial());
                item.setIncrementoMinimo(bemRequest.getIncrementoMinimo());
                item.setAberturaDisputa(bemRequest.getAberturaDisputa());
                item.setEncerramentoDisputa(bemRequest.getEncerramentoDisputa());
                item.setValorAtual(null);
                item.setProximoLance(null);
                item.setMaiorLance(null);

                leilaoLoteBens.add(item);
            }
        }

        leilaoLoteBemRepository.saveAll(leilaoLoteBens);

        if (!lotesAtualizadosStatus.isEmpty()) {
            loteRepository.saveAll(lotesAtualizadosStatus);
        }

        Leilao leilaoCompleto = leilaoRepository.findDetailedById(leilaoSalvo.getId())
                .orElseThrow(() -> new EntityNotFoundException("Leilão recém-criado não encontrado."));

        return leilaoMapper.toResponse(leilaoCompleto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(readOnly = true)
    public List<LeilaoResponse> listarTodosAdmin() {
        return leilaoRepository.findAllDetailed().stream()
                .map(leilaoMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<LeilaoResponse> listarTodosPublico() {
        return leilaoRepository.findAllDetailed().stream()
                .map(leilaoMapper::toResponse)
                .map(this::assinarMidiasLeilaoResponse)
                .toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(readOnly = true)
    public LeilaoResponse buscarPorIdAdmin(UUID id) {
        Leilao leilao = leilaoRepository.findDetailedById(id)
                .orElseThrow(() -> new EntityNotFoundException("Leilão não encontrado."));
        return leilaoMapper.toResponse(leilao);
    }

    @Transactional(readOnly = true)
    public LeilaoResponse buscarPorIdPublico(UUID id) {
        Leilao leilao = leilaoRepository.findDetailedById(id)
                .orElseThrow(() -> new EntityNotFoundException("Leilão não encontrado."));
        return assinarMidiasLeilaoResponse(leilaoMapper.toResponse(leilao));
    }

    @Transactional(readOnly = true)
    public LeilaoItemDetalheResponse buscarItemPorIdPublico(UUID leilaoLoteBemId) {
        LeilaoLoteBem item = leilaoLoteBemRepository.findDetailedById(leilaoLoteBemId)
                .orElseThrow(() -> new EntityNotFoundException("Item do leilão não encontrado."));

        LeilaoItemDetalheResponse response = leilaoMapper.toItemDetalheResponse(item);

        // Regras de incremento: 1x, 2x, 3x, 4x do incremento mínimo
        response.setIncrementosSugeridos(gerarIncrementosSugeridos(response.getIncrementoMinimo()));

        // Histórico de lances (se você já criou no repository, usa esse método)
        List<Lance> lances = lanceRepository.findTop20ByLeilaoLoteBemIdOrderByCreatedAtDesc(leilaoLoteBemId);
        response.setHistoricoLances(
                lances.stream()
                        .map(leilaoMapper::toHistoricoLanceResponse)
                        .toList()
        );

        return assinarMidiasLeilaoItemDetalheResponse(response);
    }

    @Transactional(readOnly = true)
    public LeilaoPainelResponse buscarPainel(UUID leilaoId) {
        Leilao leilao = leilaoRepository.findPainelById(leilaoId)
                .orElseThrow(() -> new EntityNotFoundException("Leilão não encontrado."));

        List<LeilaoLote> lotes = leilaoLoteRepository.findByLeilaoIdForPainel(leilaoId);

        List<LeilaoLoteBem> itensOrdenados = lotes.stream()
                .sorted(Comparator.comparing(ll -> ll.getLote().getCodigo()))
                .flatMap(ll -> ll.getBens().stream())
                .sorted(Comparator.comparing(LeilaoLoteBem::getAberturaDisputa))
                .toList();

        List<UUID> itemIds = itensOrdenados.stream()
                .map(LeilaoLoteBem::getId)
                .toList();

        Map<UUID, List<Lance>> lancesPorItem = itemIds.isEmpty()
                ? Map.of()
                : lanceRepository.findForPainelByItemIds(itemIds).stream()
                .collect(Collectors.groupingBy(l -> l.getLeilaoLoteBem().getId()));

        LeilaoPainelResponse response = new LeilaoPainelResponse();
        response.setLeilaoId(leilao.getId());
        response.setTitulo(leilao.getTitulo());
        response.setLinkLive(leilao.getLinkLive());
        response.setLeiloeiro(leilao.getLeiloeiro().getNome());
        response.setFormato(leilao.getFormato());
        response.setCidade(leilao.getFormato() == FormatoLeilao.PRESENCIAL ? leilao.getCidade() : null);
        response.setStatus(leilao.getStatus());
        response.setEncerramentoLeilao(leilao.getFimLeilao());

        response.setItemEmPauta(mapearItemEmPauta(selecionarItemEmPauta(itensOrdenados)));

        response.setItens(itensOrdenados.stream()
                .map(this::mapearItemPainel)
                .toList());

        response.setAtividadesRecentes(montarAtividadesRecentes(itensOrdenados, lancesPorItem));

        LeilaoPainelResponse.StatsResponse stats = new LeilaoPainelResponse.StatsResponse();
        stats.setTotalLotesCatalogo(lotes.size());
        stats.setTotalLances(lancesPorItem.values().stream()
                .mapToLong(List::size)
                .sum());
        stats.setTotalUsuariosDistintos(lancesPorItem.values().stream()
                .flatMap(List::stream)
                .map(Lance::getUsuario)
                .map(Usuario::getId)
                .distinct()
                .count());

        response.setStats(stats);

        return response;
    }

    private LeilaoPainelResponse.ItemEmPautaResponse mapearItemEmPauta(LeilaoLoteBem item) {
        if (item == null) {
            return null;
        }

        LeilaoPainelResponse.ItemEmPautaResponse response = new LeilaoPainelResponse.ItemEmPautaResponse();
        response.setLeilaoLoteBemId(item.getId());
        response.setLoteId(item.getLeilaoLote().getLote().getId());
        response.setCodigoLote(item.getLeilaoLote().getLote().getCodigo());
        response.setBemId(item.getBem().getId());
        response.setNomeBem(item.getBem().getModelo());
        response.setValorInicial(item.getValorInicial());
        response.setValorAtual(item.getValorAtual());
        response.setProximoLance(item.getProximoLance());
        response.setStatus(item.getStatus());
        response.setAberturaDisputa(item.getAberturaDisputa());
        response.setEncerramentoDisputa(item.getEncerramentoDisputa());
        return response;
    }

    private LeilaoPainelResponse.ItemPainelResponse mapearItemPainel(LeilaoLoteBem item) {
        LeilaoPainelResponse.ItemPainelResponse response = new LeilaoPainelResponse.ItemPainelResponse();
        response.setLeilaoLoteBemId(item.getId());
        response.setLoteId(item.getLeilaoLote().getLote().getId());
        response.setCodigoLote(item.getLeilaoLote().getLote().getCodigo());
        response.setBemId(item.getBem().getId());
        response.setNomeBem(item.getBem().getModelo());
        response.setStatus(item.getStatus());
        response.setValorInicial(item.getValorInicial());
        response.setValorAtual(item.getValorAtual());
        response.setProximoLance(item.getProximoLance());
        response.setAberturaDisputa(item.getAberturaDisputa());
        response.setEncerramentoDisputa(item.getEncerramentoDisputa());
        return response;
    }

    private LeilaoLoteBem selecionarItemEmPauta(List<LeilaoLoteBem> itens) {
        if (itens == null || itens.isEmpty()) {
            return null;
        }

        Instant agora = Instant.now();

        LeilaoLoteBem aberto = itens.stream()
                .filter(item -> item.getStatus() == StatusItemLeilao.ABERTO)
                .min(Comparator.comparing(LeilaoLoteBem::getAberturaDisputa))
                .orElse(null);

        if (aberto != null) {
            return aberto;
        }

        LeilaoLoteBem processando = itens.stream()
                .filter(item -> item.getStatus() == StatusItemLeilao.PROCESSANDO_RESULTADO)
                .max(Comparator.comparing(LeilaoLoteBem::getEncerramentoDisputa))
                .orElse(null);

        if (processando != null) {
            return processando;
        }

        LeilaoLoteBem proximo = itens.stream()
                .filter(item -> item.getAberturaDisputa().isAfter(agora))
                .min(Comparator.comparing(LeilaoLoteBem::getAberturaDisputa))
                .orElse(null);

        if (proximo != null) {
            return proximo;
        }

        return itens.stream()
                .max(Comparator.comparing(LeilaoLoteBem::getEncerramentoDisputa))
                .orElse(null);
    }

    private List<LeilaoPainelResponse.AtividadeRecenteResponse> montarAtividadesRecentes(
            List<LeilaoLoteBem> itens,
            Map<UUID, List<Lance>> lancesPorItem
    ) {
        List<LeilaoPainelResponse.AtividadeRecenteResponse> atividades = new ArrayList<>();
        Instant agora = Instant.now();

        for (LeilaoLoteBem item : itens) {
            String codigoLote = item.getLeilaoLote().getLote().getCodigo();
            String nomeBem = item.getBem().getModelo();

            if (!item.getAberturaDisputa().isAfter(agora)) {
                LeilaoPainelResponse.AtividadeRecenteResponse atividadeAbertura =
                        new LeilaoPainelResponse.AtividadeRecenteResponse();
                atividadeAbertura.setLoteCodigo(codigoLote);
                atividadeAbertura.setNomeBem(nomeBem);
                atividadeAbertura.setAcao("DISPUTA_ATIVA");
                atividadeAbertura.setDataHora(item.getAberturaDisputa());
                atividades.add(atividadeAbertura);
            }

            if (item.getStatus() == StatusItemLeilao.ENCERRADO
                    || item.getStatus() == StatusItemLeilao.PROCESSANDO_RESULTADO
                    || item.getStatus() == StatusItemLeilao.ARREMATADO
                    || item.getStatus() == StatusItemLeilao.SEM_LANCES) {

                LeilaoPainelResponse.AtividadeRecenteResponse atividadeFechamento =
                        new LeilaoPainelResponse.AtividadeRecenteResponse();
                atividadeFechamento.setLoteCodigo(codigoLote);
                atividadeFechamento.setNomeBem(nomeBem);
                atividadeFechamento.setAcao("DISPUTA_FECHADA");
                atividadeFechamento.setDataHora(item.getEncerramentoDisputa());
                atividades.add(atividadeFechamento);

                LeilaoPainelResponse.AtividadeRecenteResponse calculando =
                        new LeilaoPainelResponse.AtividadeRecenteResponse();
                calculando.setLoteCodigo(codigoLote);
                calculando.setNomeBem(nomeBem);
                calculando.setAcao("CALCULANDO_GANHADOR");
                calculando.setDataHora(item.getEncerramentoDisputa());
                atividades.add(calculando);
            }

            for (Lance lance : lancesPorItem.getOrDefault(item.getId(), List.of())) {
                LeilaoPainelResponse.AtividadeRecenteResponse atividadeLance =
                        new LeilaoPainelResponse.AtividadeRecenteResponse();
                atividadeLance.setLoteCodigo(codigoLote);
                atividadeLance.setNomeBem(nomeBem);
                atividadeLance.setAcao("NOVO_LANCE");
                atividadeLance.setDataHora(lance.getCreatedAt());
                atividadeLance.setUsuarioNome(lance.getUsuario().getNomeCompleto());
                atividadeLance.setValor(lance.getValor());
                atividades.add(atividadeLance);
            }
        }

        return atividades.stream()
                .sorted(Comparator.comparing(LeilaoPainelResponse.AtividadeRecenteResponse::getDataHora).reversed())
                .limit(30)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<LeilaoCardResponse> listarCardsPlataforma() {
        return leilaoLoteBemRepository.findAllForCards().stream()
                .map(leilaoMapper::toCardResponse)
                .sorted(Comparator.comparing(LeilaoCardResponse::getAberturaLeilao))
                .toList();
    }

    private void validarRequest(LeilaoCreateRequest request) {
        if (request.getLotes() == null || request.getLotes().isEmpty()) {
            throw new IllegalArgumentException("Informe ao menos um lote.");
        }

        Set<UUID> loteIds = new HashSet<>();
        Set<UUID> bemIds = new HashSet<>();

        for (LeilaoCreateRequest.LoteRequest loteRequest : request.getLotes()) {
            if (!loteIds.add(loteRequest.getLoteId())) {
                throw new IllegalArgumentException("Lote duplicado na requisição: " + loteRequest.getLoteId());
            }

            if (loteRequest.getBens() == null || loteRequest.getBens().isEmpty()) {
                throw new IllegalArgumentException("Todo lote deve possuir ao menos um bem.");
            }

            for (LeilaoCreateRequest.BemRequest bemRequest : loteRequest.getBens()) {
                if (!bemIds.add(bemRequest.getBemId())) {
                    throw new IllegalArgumentException("O mesmo bem não pode ser enviado mais de uma vez no leilão.");
                }
            }
        }
    }

    private void validarCronologia(List<LeilaoCreateRequest.BemRequest> itens) {
        Instant agora = Instant.now();

        for (LeilaoCreateRequest.BemRequest item : itens) {
            if (item.getAberturaDisputa() == null || item.getEncerramentoDisputa() == null) {
                throw new IllegalArgumentException("Abertura e encerramento são obrigatórios para todos os bens.");
            }

            if (!item.getEncerramentoDisputa().isAfter(item.getAberturaDisputa())) {
                throw new IllegalArgumentException("O encerramento do bem deve ser após a abertura.");
            }

            if (!item.getAberturaDisputa().isAfter(agora)) {
                throw new IllegalArgumentException("A abertura de todos os bens deve ser futura.");
            }
        }

        for (int i = 1; i < itens.size(); i++) {
            LeilaoCreateRequest.BemRequest anterior = itens.get(i - 1);
            LeilaoCreateRequest.BemRequest atual = itens.get(i);

            if (atual.getAberturaDisputa().isBefore(anterior.getEncerramentoDisputa())) {
                throw new IllegalArgumentException("Um bem só pode começar após o término do bem anterior.");
            }
        }
    }

    private LeilaoResponse assinarMidiasLeilaoResponse(LeilaoResponse response) {
        if (response == null || response.getLotes() == null) {
            return response;
        }

        for (LeilaoResponse.LoteResponse lote : response.getLotes()) {
            if (lote == null || lote.getBens() == null) continue;

            for (LeilaoResponse.ItemResponse item : lote.getBens()) {
                if (item == null || item.getMidias() == null) continue;

                for (LeilaoResponse.MidiaResponse midia : item.getMidias()) {
                    if (midia == null) continue;

                    String objectKey = midia.getArquivo();
                    if (objectKey == null || objectKey.isBlank()) continue;

                    String signedUrl = r2Service.gerarUrlDownload(
                            r2Properties.bucket(),
                            objectKey.trim(),
                            DOWNLOAD_EXPIRES_IN_SECONDS
                    );

                    midia.setArquivo(signedUrl);
                }
            }
        }

        return response;
    }

    private LeilaoItemDetalheResponse assinarMidiasLeilaoItemDetalheResponse(LeilaoItemDetalheResponse response) {
        if (response == null || response.getMidias() == null) {
            return response;
        }

        for (LeilaoItemDetalheResponse.MidiaResponse midia : response.getMidias()) {
            if (midia == null) continue;

            String objectKey = midia.getArquivo();
            if (objectKey == null || objectKey.isBlank()) continue;

            String signedUrl = r2Service.gerarUrlDownload(
                    r2Properties.bucket(),
                    objectKey.trim(),
                    DOWNLOAD_EXPIRES_IN_SECONDS
            );

            midia.setArquivo(signedUrl);
        }

        return response;
    }

    private List<BigDecimal> gerarIncrementosSugeridos(BigDecimal incrementoMinimo) {
        if (incrementoMinimo == null || incrementoMinimo.signum() <= 0) {
            return List.of();
        }

        return List.of(
                incrementoMinimo,
                incrementoMinimo.multiply(BigDecimal.valueOf(2)),
                incrementoMinimo.multiply(BigDecimal.valueOf(3)),
                incrementoMinimo.multiply(BigDecimal.valueOf(4))
        );
    }

    private String normalizarLinkLive(String linkLive) {
        if (linkLive == null || linkLive.isBlank()) {
            return null;
        }

        String valor = linkLive.trim();
        URI uri;
        try {
            uri = URI.create(valor);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Link da live inválido. Informe uma URL válida.");
        }

        String scheme = uri.getScheme();
        if (scheme == null || (!"http".equalsIgnoreCase(scheme) && !"https".equalsIgnoreCase(scheme))) {
            throw new IllegalArgumentException("Link da live inválido. Use URL http ou https.");
        }

        return valor;
    }
}