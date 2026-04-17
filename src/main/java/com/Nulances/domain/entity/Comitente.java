package com.Nulances.domain.entity;

import com.Nulances.domain.enums.TipoComitente;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "comitentes")
@Getter
@Setter
public class Comitente extends AuditableEntity {

    @Column(nullable = false)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoComitente tipo;

    @Column(nullable = false, unique = true, length = 18)
    private String documento;

    @Column(name = "ativo_plataforma", nullable = false)
    private Boolean ativoPlataforma = true;

    private String sede;

    @OneToMany(mappedBy = "comitente", fetch = FetchType.LAZY)
    private List<Leilao> leiloes = new ArrayList<>();
}
