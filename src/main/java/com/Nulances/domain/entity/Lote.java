package com.Nulances.domain.entity;

import com.Nulances.domain.enums.StatusLote;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "lotes")
@Getter
@Setter
public class Lote extends AuditableEntity {

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true, length = 20)
    private String codigo;

    @Column(columnDefinition = "text")
    private String observacoes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusLote status = StatusLote.DISPONIVEL;

    @OneToMany(mappedBy = "lote", fetch = FetchType.LAZY)
    private List<Bem> bens = new ArrayList<>();

    @OneToMany(mappedBy = "lote", fetch = FetchType.LAZY)
    private List<LeilaoLote> leilaoLotes = new ArrayList<>();
}
