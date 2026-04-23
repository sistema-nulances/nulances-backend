package com.Nulances.domain.entity;

import com.Nulances.domain.enums.FormatoLeilao;
import com.Nulances.domain.enums.StatusLeilao;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "leiloes")
@Getter
@Setter
public class Leilao extends AuditableEntity {

    @Column(nullable = false)
    private String titulo;

    @Column(name = "link_live", length = 500)
    private String linkLive;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FormatoLeilao formato;

    @Column(length = 100)
    private String cidade;

    private String endereco;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "leiloeiro_id", nullable = false)
    private Leiloeiro leiloeiro;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "comitente_id", nullable = false)
    private Comitente comitente;

    @Column(name = "inicio_leilao", nullable = false)
    private Instant inicioLeilao;

    @Column(name = "fim_leilao", nullable = false)
    private Instant fimLeilao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusLeilao status = StatusLeilao.EM_BREVE;

    @OneToMany(mappedBy = "leilao", fetch = FetchType.LAZY)
    private List<LeilaoLote> lotes = new ArrayList<>();
}
