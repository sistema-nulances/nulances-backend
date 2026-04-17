package com.Nulances.domain.entity;

import com.Nulances.domain.enums.*;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "bens")
@Getter
@Setter
public class Bem extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lote_id")
    private Lote lote;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "marca_id", nullable = false)
    private Marca marca;

    @Column(nullable = false)
    private String modelo;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_veiculo", nullable = false)
    private TipoVeiculo tipoVeiculo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CondicaoVeiculo condicao;

    @Column(nullable = false)
    private Integer ano;

    private Long quilometragem;

    @Column(name = "final_chassi", length = 10)
    private String finalChassi;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CombustivelVeiculo combustivel;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CambioVeiculo cambio;

    @Column(nullable = false)
    private Boolean blindado = false;

    @Column(length = 50)
    private String cor;

    @Column(name = "placa_veiculo", length = 10)
    private String placaVeiculo;

    @Column(columnDefinition = "text")
    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusBem status = StatusBem.DISPONIVEL;

    @OneToMany(mappedBy = "bem", fetch = FetchType.LAZY)
    private List<BemMidia> midias = new ArrayList<>();

    @OneToMany(mappedBy = "bem", fetch = FetchType.LAZY)
    private List<LeilaoLoteBem> itensLeilao = new ArrayList<>();
}