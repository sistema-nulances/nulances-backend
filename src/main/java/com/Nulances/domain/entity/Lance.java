package com.Nulances.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(
        name = "lances",
        indexes = {
                @Index(name = "idx_lance_item_created_at", columnList = "leilao_lote_bem_id, created_at"),
                @Index(name = "idx_lance_item_valor", columnList = "leilao_lote_bem_id, valor"),
                @Index(name = "idx_lance_usuario", columnList = "usuario_id")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_lance_item_usuario_request", columnNames = {"leilao_lote_bem_id", "usuario_id", "client_request_id"})
        }
)
@Getter
@Setter
public class Lance extends CreatedOnlyEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "leilao_lote_bem_id", nullable = false)
    private LeilaoLoteBem leilaoLoteBem;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal valor;

    @Column(name = "client_request_id", nullable = false, length = 100)
    private String clientRequestId;
}