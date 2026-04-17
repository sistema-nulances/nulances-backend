package com.Nulances.domain.entity;

import com.Nulances.domain.enums.ExibicaoAviso;
import com.Nulances.domain.enums.TipoAviso;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "avisos")
@Getter
@Setter
public class Aviso extends AuditableEntity {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoAviso tipo;

    @Column(nullable = false)
    private String titulo;

    @Column(nullable = false, columnDefinition = "text")
    private String mensagem;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExibicaoAviso exibicao;

    @Column(name = "ordem_exibicao", nullable = false)
    private Integer ordemExibicao = 0;

    @Column(nullable = false)
    private Boolean ativo = true;

    @OneToMany(mappedBy = "aviso", fetch = FetchType.LAZY)
    private List<UsuarioAvisoAceito> usuariosAceites = new ArrayList<>();
}
