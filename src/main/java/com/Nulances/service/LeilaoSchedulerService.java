package com.Nulances.service;

import com.Nulances.domain.entity.Arrematacao;
import com.Nulances.domain.entity.Bem;
import com.Nulances.domain.entity.LeilaoLoteBem;
import com.Nulances.domain.enums.StatusBem;
import com.Nulances.domain.enums.StatusItemLeilao;
import com.Nulances.dto.messaging.ArrematacaoVencedorMessage;
import com.Nulances.messaging.publisher.ArrematacaoVencedorPublisher;
import com.Nulances.repository.ArrematacaoRepository;
import com.Nulances.repository.BemRepository;
import com.Nulances.repository.LeilaoLoteBemRepository;
import com.Nulances.repository.LeilaoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class LeilaoSchedulerService {

    private final LeilaoLoteBemRepository itemRepository;
    private final ArrematacaoRepository arrematacaoRepository;
    private final LeilaoRepository leilaoRepository;
    private final LeilaoService leilaoService;
    private final BemRepository bemRepository;
    private final ArrematacaoVencedorPublisher arrematacaoVencedorPublisher;

    @Scheduled(fixedDelay = 3000)
    @Transactional
    public void abrirItens() {
        List<LeilaoLoteBem> itens = itemRepository.findByStatusAndAberturaDisputaLessThanEqual(
                StatusItemLeilao.AGUARDANDO_ABERTURA,
                Instant.now()
        );

        if (itens.isEmpty()) {
            return;
        }

        Set<UUID> leiloesAtualizados = new HashSet<>();

        for (LeilaoLoteBem item : itens) {
            item.setStatus(StatusItemLeilao.ABERTO);
            item.setValorAtual(null);
            item.setProximoLance(item.getValorInicial());
            itemRepository.save(item);

            UUID leilaoId = item.getLeilaoLote().getLeilao().getId();
            leilaoRepository.atualizarLeilaoParaAoVivo(leilaoId);
            leiloesAtualizados.add(leilaoId);
        }

        for (UUID leilaoId : leiloesAtualizados) {
            leilaoService.publicarPainelAtualizado(leilaoId);
        }
    }

    @Scheduled(fixedDelay = 3000)
    @Transactional
    public void encerrarItens() {
        List<LeilaoLoteBem> itens = itemRepository.findByStatusAndEncerramentoDisputaLessThanEqual(
                StatusItemLeilao.ABERTO,
                Instant.now()
        );

        Set<UUID> leiloesAtualizados = new HashSet<>();

        for (LeilaoLoteBem item : itens) {
            UUID leilaoId = item.getLeilaoLote().getLeilao().getId();

            item.setStatus(StatusItemLeilao.ENCERRADO);
            itemRepository.save(item);
            leiloesAtualizados.add(leilaoId);

            item.setStatus(StatusItemLeilao.PROCESSANDO_RESULTADO);
            itemRepository.save(item);
            leiloesAtualizados.add(leilaoId);

            if (item.getMaiorLance() != null) {
                boolean jaExisteArrematacao = arrematacaoRepository.existsByLeilaoLoteBemId(item.getId());

                if (!jaExisteArrematacao) {
                    Arrematacao arrematacao = new Arrematacao();
                    arrematacao.setLeilaoLoteBem(item);
                    arrematacao.setLanceVencedor(item.getMaiorLance());
                    arrematacao.setUsuario(item.getMaiorLance().getUsuario());
                    arrematacao.setValorFinal(item.getMaiorLance().getValor());
                    arrematacao.setProcessadoEm(Instant.now());

                    arrematacao = arrematacaoRepository.save(arrematacao);

                    arrematacaoVencedorPublisher.publicar(new ArrematacaoVencedorMessage(
                            arrematacao.getId(),
                            arrematacao.getUsuario().getId(),
                            arrematacao.getUsuario().getEmail(),
                            arrematacao.getUsuario().getNomeCompleto(),
                            item.getLeilaoLote().getLeilao().getTitulo(),
                            item.getLeilaoLote().getLote().getCodigo(),
                            item.getBem().getModelo(),
                            arrematacao.getValorFinal()
                    ));
                }

                item.setStatus(StatusItemLeilao.ARREMATADO);

                Bem bem = item.getBem();
                if (bem != null && bem.getStatus() == StatusBem.EM_LOTE) {
                    bem.setStatus(StatusBem.ARREMATADO);
                    bemRepository.save(bem);
                }
            } else {
                item.setStatus(StatusItemLeilao.SEM_LANCES);
            }

            itemRepository.save(item);
            leiloesAtualizados.add(leilaoId);
        }

        int totalEncerrados = leilaoRepository.atualizarLeiloesEncerradosAutomaticamente();
        log.info("Total de leilões encerrados automaticamente: {}", totalEncerrados);

        for (UUID leilaoId : leiloesAtualizados) {
            leilaoService.publicarPainelAtualizado(leilaoId);
        }
    }
}