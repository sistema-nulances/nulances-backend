package com.Nulances.payment.service;

import com.Nulances.domain.entity.Anuncio;
import com.Nulances.domain.entity.AssinaturaPlano;
import com.Nulances.domain.entity.PagamentoPlano;
import com.Nulances.domain.enums.StatusAnuncio;
import com.Nulances.domain.enums.StatusPagamentoPlano;
import com.Nulances.dto.messaging.CobrancaPlanoMessage;
import com.Nulances.messaging.publisher.CobrancaPlanoPublisher;
import com.Nulances.repository.AnuncioRepository;
import com.Nulances.repository.PagamentoPlanoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class CobrancaRecorrenteSchedulerService {

    private final AssinaturaPlanoService assinaturaPlanoService;
    private final PagamentoPlanoRepository pagamentoPlanoRepository;
    private final CobrancaPlanoPublisher cobrancaPlanoPublisher;
    private final AnuncioRepository anuncioRepository;

    @Scheduled(fixedDelay = 60000)
    @Transactional
    public void gerarCobrancasRecorrentes() {
        List<AssinaturaPlano> assinaturas = assinaturaPlanoService.buscarAssinaturasParaRenovar();

        for (AssinaturaPlano assinatura : assinaturas) {
            boolean jaPossuiCobrancaAberta = pagamentoPlanoRepository
                    .findFirstByAssinaturaIdAndStatusOrderByCreatedAtDesc(
                            assinatura.getId(),
                            StatusPagamentoPlano.GERADO
                    ).isPresent();

            if (jaPossuiCobrancaAberta) {
                continue;
            }

            PagamentoPlano pagamento = assinaturaPlanoService.criarPagamentoRenovacao(assinatura);

            cobrancaPlanoPublisher.publicar(new CobrancaPlanoMessage(
                    pagamento.getId(),
                    assinatura.getVendedor().getId(),
                    assinatura.getVendedor().getEmail(),
                    assinatura.getVendedor().getNomeCompleto(),
                    assinatura.getPlano().getNome(),
                    pagamento.getValor(),
                    pagamento.getDataVencimento(),
                    pagamento.getCheckoutUrl()
            ));
        }
    }

    @Scheduled(fixedDelay = 60000)
    @Transactional
    public void suspenderAnunciosPorInadimplencia() {
        List<AssinaturaPlano> inadimplentes = assinaturaPlanoService.buscarAssinaturasInadimplentes();

        for (AssinaturaPlano assinatura : inadimplentes) {
            assinaturaPlanoService.marcarInadimplente(assinatura);

            List<Anuncio> anuncios = anuncioRepository.findByVendedorIdAndStatusIn(
                    assinatura.getVendedor().getId(),
                    Set.of(StatusAnuncio.PENDENTE, StatusAnuncio.PUBLICADO)
            );

            for (Anuncio anuncio : anuncios) {
                anuncio.setStatus(StatusAnuncio.SUSPENSO);
            }

            if (!anuncios.isEmpty()) {
                anuncioRepository.saveAll(anuncios);
            }

            log.warn(
                    "Assinatura {} marcada como INADIMPLENTE. {} anúncios do vendedor {} foram suspensos.",
                    assinatura.getId(),
                    anuncios.size(),
                    assinatura.getVendedor().getId()
            );
        }
    }
}
