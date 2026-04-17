package com.Nulances.domain.entity;

import com.Nulances.domain.enums.StatusItemLeilao;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "leilao_lote_bens",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_leilao_lote_bem_bem", columnNames = "bem_id")
        },
        indexes = {
                @Index(name = "idx_item_status_abertura", columnList = "status, abertura_disputa"),
                @Index(name = "idx_item_status_encerramento", columnList = "status, encerramento_disputa")
        }
)
@Getter
@Setter
public class LeilaoLoteBem extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "leilao_lote_id", nullable = false)
    private LeilaoLote leilaoLote;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bem_id", nullable = false)
    private Bem bem;

    @Column(name = "valor_inicial", nullable = false, precision = 15, scale = 2)
    private BigDecimal valorInicial;

    @Column(name = "incremento_minimo", nullable = false, precision = 15, scale = 2)
    private BigDecimal incrementoMinimo;

    @Column(name = "valor_atual", precision = 15, scale = 2)
    private BigDecimal valorAtual;

    @Column(name = "proximo_lance", precision = 15, scale = 2)
    private BigDecimal proximoLance;

    @Column(name = "abertura_disputa", nullable = false)
    private Instant aberturaDisputa;

    @Column(name = "encerramento_disputa", nullable = false)
    private Instant encerramentoDisputa;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusItemLeilao status = StatusItemLeilao.AGUARDANDO_ABERTURA;

    @OneToMany(mappedBy = "leilaoLoteBem", fetch = FetchType.LAZY)
    private List<Lance> lances = new ArrayList<>();

    @OneToOne(mappedBy = "leilaoLoteBem", fetch = FetchType.LAZY)
    private Arrematacao arrematacao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maior_lance_id")
    private Lance maiorLance;

    @Version
    private Long version;
}