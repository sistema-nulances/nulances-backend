package com.Nulances.domain.entity;

import com.Nulances.domain.enums.TipoDocumentoVendedor;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "documentos_vendedor")
@Getter
@Setter
public class DocumentoVendedor extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoDocumentoVendedor tipo;

    @Column(nullable = false, length = 500)
    private String arquivo;
}
