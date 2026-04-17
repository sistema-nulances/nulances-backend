package com.Nulances.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "leilao_lotes",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_leilao_lote_lote", columnNames = "lote_id")
        }
)
@Getter
@Setter
public class LeilaoLote extends CreatedOnlyEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "leilao_id", nullable = false)
    private Leilao leilao;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "lote_id", nullable = false)
    private Lote lote;

    @OneToMany(mappedBy = "leilaoLote", fetch = FetchType.LAZY)
    private List<LeilaoLoteBem> bens = new ArrayList<>();
}
