package com.Nulances.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(
        name = "usuario_avisos_aceitos",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_usuario_aviso_aceito", columnNames = {"usuario_id", "aviso_id"})
        }
)
@Getter
@Setter
public class UsuarioAvisoAceito extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "aviso_id", nullable = false)
    private Aviso aviso;

    @Column(name = "aceito_em", nullable = false)
    private Instant aceitoEm;
}
