package com.Nulances.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(
        name = "arrematacoes",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_arrematacao_item", columnNames = "leilao_lote_bem_id")
        }
)
@Getter
@Setter
public class Arrematacao extends AuditableEntity {

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "leilao_lote_bem_id", nullable = false)
    private LeilaoLoteBem leilaoLoteBem;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "lance_vencedor_id", nullable = false)
    private Lance lanceVencedor;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(name = "valor_final", nullable = false, precision = 15, scale = 2)
    private BigDecimal valorFinal;

    @Column(name = "processado_em", nullable = false)
    private Instant processadoEm;
}
