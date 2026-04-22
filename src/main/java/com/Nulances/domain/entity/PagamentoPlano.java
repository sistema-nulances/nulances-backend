package com.Nulances.domain.entity;

import com.Nulances.domain.enums.StatusPagamentoPlano;
import com.Nulances.domain.enums.TipoPagamentoPlano;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "pagamentos_plano")
@Getter
@Setter
public class PagamentoPlano extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "assinatura_id", nullable = false)
    private AssinaturaPlano assinatura;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal valor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private StatusPagamentoPlano status = StatusPagamentoPlano.GERADO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoPagamentoPlano tipo = TipoPagamentoPlano.ADESAO;

    @Column(nullable = false, unique = true, length = 80)
    private String referencia;

    @Column(name = "mercado_pago_preference_id", length = 120)
    private String mercadoPagoPreferenceId;

    @Column(name = "mercado_pago_payment_id", length = 120)
    private String mercadoPagoPaymentId;

    @Column(name = "checkout_url", length = 500)
    private String checkoutUrl;

    @Column(name = "data_vencimento")
    private Instant dataVencimento;

    @Column(name = "pago_em")
    private Instant pagoEm;

    @Column(name = "raw_webhook_payload", columnDefinition = "text")
    private String rawWebhookPayload;
}
