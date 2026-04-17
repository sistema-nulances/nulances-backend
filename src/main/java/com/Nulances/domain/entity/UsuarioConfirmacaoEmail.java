package com.Nulances.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "usuario_confirmacoes_email")
@Getter
@Setter
public class UsuarioConfirmacaoEmail extends CreatedOnlyEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(nullable = false, length = 6)
    private String codigo;

    @Column(name = "expira_em", nullable = false)
    private Instant expiraEm;

    @Column(nullable = false)
    private Boolean usado = false;

    @Column(name = "usado_em")
    private Instant usadoEm;

    @Column(nullable = false)
    private Integer tentativas = 0;
}