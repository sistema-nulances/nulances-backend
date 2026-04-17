package com.Nulances.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "leiloeiros")
@Getter
@Setter
public class Leiloeiro extends AuditableEntity {

    @Column(nullable = false)
    private String nome;

    @Column(name = "registro_profissional", nullable = false, unique = true)
    private String registroProfissional;

    @Column(nullable = false, unique = true, length = 14)
    private String cpf;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(length = 20)
    private String telefone;

    @Column(name = "ativo_plataforma", nullable = false)
    private Boolean ativoPlataforma = true;

    private String local;

    @OneToMany(mappedBy = "leiloeiro", fetch = FetchType.LAZY)
    private List<Leilao> leiloes = new ArrayList<>();
}
