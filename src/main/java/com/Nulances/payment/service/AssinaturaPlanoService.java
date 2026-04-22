package com.Nulances.payment.service;

import com.Nulances.config.security.CustomUserDetails;
import com.Nulances.domain.entity.AssinaturaPlano;
import com.Nulances.domain.entity.PagamentoPlano;
import com.Nulances.domain.entity.PlanoAnuncio;
import com.Nulances.domain.entity.Usuario;
import com.Nulances.domain.enums.StatusAnuncio;
import com.Nulances.domain.enums.StatusAssinaturaPlano;
import com.Nulances.domain.enums.StatusPagamentoPlano;
import com.Nulances.domain.enums.TipoPagamentoPlano;
import com.Nulances.domain.enums.UserRole;
import com.Nulances.dto.request.AssinarPlanoRequest;
import com.Nulances.dto.response.CheckoutPlanoResponse;
import com.Nulances.dto.response.MinhaAssinaturaPlanoResponse;
import com.Nulances.dto.response.PainelPlanosVendedorResponse;
import com.Nulances.dto.response.PlanoAnuncioResponse;
import com.Nulances.payment.config.PaymentProperties;
import com.Nulances.repository.AnuncioRepository;
import com.Nulances.repository.AssinaturaPlanoRepository;
import com.Nulances.repository.PagamentoPlanoRepository;
import com.Nulances.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AssinaturaPlanoService {

    private final UsuarioRepository usuarioRepository;
    private final AssinaturaPlanoRepository assinaturaPlanoRepository;
    private final PagamentoPlanoRepository pagamentoPlanoRepository;
    private final AnuncioRepository anuncioRepository;
    private final PlanoMarketplaceService planoMarketplaceService;
    private final MercadoPagoCheckoutService mercadoPagoCheckoutService;
    private final PaymentProperties paymentProperties;

    @PreAuthorize("hasAnyRole('VENDEDOR','ADMIN')")
    @Transactional(readOnly = true)
    public PainelPlanosVendedorResponse buscarPainelPlanos(CustomUserDetails userDetails) {
        Usuario vendedor = buscarVendedorAutenticado(userDetails);
        List<PlanoAnuncioResponse> planos = planoMarketplaceService.listarAtivos();

        MinhaAssinaturaPlanoResponse assinaturaAtual = assinaturaPlanoRepository
                .findFirstByVendedorIdOrderByCreatedAtDesc(vendedor.getId())
                .map(this::toMinhaAssinaturaResponse)
                .orElse(null);

        return PainelPlanosVendedorResponse.builder()
                .planosDisponiveis(planos)
                .assinaturaAtual(assinaturaAtual)
                .build();
    }

    @PreAuthorize("hasAnyRole('VENDEDOR','ADMIN')")
    @Transactional
    public CheckoutPlanoResponse assinarPlano(AssinarPlanoRequest request, CustomUserDetails userDetails) {
        Usuario vendedor = buscarVendedorAutenticado(userDetails);
        PlanoAnuncio plano = planoMarketplaceService.buscarPlanoAtivo(request.getPlanoId());

        AssinaturaPlano assinatura = assinaturaPlanoRepository
                .findFirstByVendedorIdOrderByCreatedAtDesc(vendedor.getId())
                .orElseGet(AssinaturaPlano::new);

        assinatura.setVendedor(vendedor);
        assinatura.setPlano(plano);
        assinatura.setStatus(StatusAssinaturaPlano.PENDENTE_PAGAMENTO);
        assinatura.setInicioVigencia(null);
        assinatura.setProximaCobranca(null);
        assinatura.setUltimaCobrancaEm(null);
        assinatura = assinaturaPlanoRepository.save(assinatura);

        PagamentoPlano pagamento = criarPagamento(assinatura, TipoPagamentoPlano.ADESAO);

        return CheckoutPlanoResponse.builder()
                .pagamentoId(pagamento.getId())
                .referencia(pagamento.getReferencia())
                .checkoutUrl(pagamento.getCheckoutUrl())
                .status(pagamento.getStatus())
                .build();
    }

    @Transactional(readOnly = true)
    public void validarPodeCriarAnuncio(UUID vendedorId) {
        AssinaturaPlano assinatura = assinaturaPlanoRepository
                .findFirstByVendedorIdAndStatusOrderByCreatedAtDesc(vendedorId, StatusAssinaturaPlano.ATIVA)
                .orElseThrow(() -> new IllegalArgumentException("Você precisa assinar e pagar um plano para anunciar."));

        long totalUsado = anuncioRepository.countByVendedorIdAndStatusIn(
                vendedorId,
                Set.of(StatusAnuncio.PENDENTE, StatusAnuncio.PUBLICADO)
        );

        if (totalUsado >= assinatura.getPlano().getTotalAnuncios()) {
            throw new IllegalArgumentException("Limite de anúncios do plano atingido. Faça upgrade no painel de planos.");
        }
    }

    @Transactional
    public void ativarAssinaturaPorPagamento(PagamentoPlano pagamento) {
        AssinaturaPlano assinatura = pagamento.getAssinatura();
        Instant agora = Instant.now();
        int diasVigencia = obterDiasVigencia();

        assinatura.setStatus(StatusAssinaturaPlano.ATIVA);
        if (assinatura.getInicioVigencia() == null) {
            assinatura.setInicioVigencia(agora);
        }
        assinatura.setUltimaCobrancaEm(agora);
        assinatura.setProximaCobranca(agora.plus(diasVigencia, ChronoUnit.DAYS));
        assinaturaPlanoRepository.save(assinatura);
    }

    @Transactional(readOnly = true)
    public List<AssinaturaPlano> buscarAssinaturasParaRenovar() {
        return assinaturaPlanoRepository.findByStatusAndProximaCobrancaLessThanEqual(
                StatusAssinaturaPlano.ATIVA,
                Instant.now()
        );
    }

    @Transactional(readOnly = true)
    public List<AssinaturaPlano> buscarAssinaturasInadimplentes() {
        int diasTolerancia = obterDiasTolerancia();
        Instant limite = Instant.now().minus(diasTolerancia, ChronoUnit.DAYS);
        return assinaturaPlanoRepository.findByStatusAndProximaCobrancaLessThan(
                StatusAssinaturaPlano.PENDENTE_PAGAMENTO,
                limite
        );
    }

    @Transactional
    public PagamentoPlano criarPagamentoRenovacao(AssinaturaPlano assinatura) {
        assinatura.setStatus(StatusAssinaturaPlano.PENDENTE_PAGAMENTO);
        assinaturaPlanoRepository.save(assinatura);
        return criarPagamento(assinatura, TipoPagamentoPlano.RENOVACAO);
    }

    @Transactional
    public void marcarInadimplente(AssinaturaPlano assinatura) {
        assinatura.setStatus(StatusAssinaturaPlano.INADIMPLENTE);
        assinaturaPlanoRepository.save(assinatura);
    }

    private PagamentoPlano criarPagamento(AssinaturaPlano assinatura, TipoPagamentoPlano tipo) {
        String referencia = "PLANO-" + UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase();
        MercadoPagoCheckoutService.CheckoutPreferenceData checkout = mercadoPagoCheckoutService.criarPreferenciaCheckout(
                referencia,
                "Plano " + assinatura.getPlano().getNome() + " - Nulances",
                assinatura.getPlano().getValorMensal(),
                assinatura.getVendedor().getEmail()
        );

        PagamentoPlano pagamento = new PagamentoPlano();
        pagamento.setAssinatura(assinatura);
        pagamento.setTipo(tipo);
        pagamento.setStatus(StatusPagamentoPlano.GERADO);
        pagamento.setReferencia(referencia);
        pagamento.setValor(assinatura.getPlano().getValorMensal());
        pagamento.setMercadoPagoPreferenceId(checkout.preferenceId());
        pagamento.setCheckoutUrl(checkout.checkoutUrl());
        pagamento.setDataVencimento(Instant.now().plus(obterDiasVigencia(), ChronoUnit.DAYS));

        return pagamentoPlanoRepository.save(pagamento);
    }

    private MinhaAssinaturaPlanoResponse toMinhaAssinaturaResponse(AssinaturaPlano assinatura) {
        PlanoAnuncio plano = assinatura.getPlano();
        long totalUsado = anuncioRepository.countByVendedorIdAndStatusIn(
                assinatura.getVendedor().getId(),
                Set.of(StatusAnuncio.PENDENTE, StatusAnuncio.PUBLICADO)
        );
        int anunciosDisponiveis = Math.max(0, plano.getTotalAnuncios() - (int) totalUsado);

        return MinhaAssinaturaPlanoResponse.builder()
                .assinaturaId(assinatura.getId())
                .status(assinatura.getStatus())
                .inicioVigencia(assinatura.getInicioVigencia())
                .proximaCobranca(assinatura.getProximaCobranca())
                .anunciosDisponiveis(anunciosDisponiveis)
                .plano(PlanoAnuncioResponse.builder()
                        .id(plano.getId())
                        .nome(plano.getNome())
                        .descricao(plano.getDescricao())
                        .valorMensal(plano.getValorMensal())
                        .totalAnuncios(plano.getTotalAnuncios())
                        .ativo(plano.getAtivo())
                        .build())
                .build();
    }

    private Usuario buscarVendedorAutenticado(CustomUserDetails userDetails) {
        Usuario usuario = usuarioRepository.findById(userDetails.getId())
                .orElseThrow(() -> new IllegalArgumentException("Usuário autenticado não encontrado."));

        if (usuario.getRole() != UserRole.VENDEDOR && usuario.getRole() != UserRole.ADMIN) {
            throw new IllegalArgumentException("Somente vendedores aprovados podem assinar planos.");
        }

        return usuario;
    }

    private int obterDiasVigencia() {
        Integer dias = paymentProperties.assinatura().diasVigencia();
        return dias != null && dias > 0 ? dias : 30;
    }

    private int obterDiasTolerancia() {
        Integer dias = paymentProperties.assinatura().diasToleranciaInadimplencia();
        return dias != null && dias >= 0 ? dias : 3;
    }
}
