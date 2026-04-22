package com.Nulances.domain.entity;

import com.Nulances.domain.enums.StatusAssinaturaPlano;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "assinaturas_plano")
@Getter
@Setter
public class AssinaturaPlano extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vendedor_id", nullable = false)
    private Usuario vendedor;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "plano_id", nullable = false)
    private PlanoAnuncio plano;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private StatusAssinaturaPlano status = StatusAssinaturaPlano.PENDENTE_PAGAMENTO;

    @Column(name = "inicio_vigencia")
    private Instant inicioVigencia;

    @Column(name = "proxima_cobranca")
    private Instant proximaCobranca;

    @Column(name = "ultima_cobranca_em")
    private Instant ultimaCobrancaEm;
}
