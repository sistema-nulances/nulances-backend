package com.Nulances.domain.entity;

import com.Nulances.domain.enums.StatusDocumentoValidacao;
import com.Nulances.domain.enums.TipoDocumentoValidacao;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "documentos_validacao")
@Getter
@Setter
public class DocumentoValidacao extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoDocumentoValidacao tipo;

    @Column(nullable = false, length = 500)
    private String arquivo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusDocumentoValidacao status = StatusDocumentoValidacao.PENDENTE;
}