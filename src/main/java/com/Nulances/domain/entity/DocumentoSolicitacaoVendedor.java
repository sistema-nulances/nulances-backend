package com.Nulances.domain.entity;

import com.Nulances.domain.enums.TipoDocumentoSolicitacaoVendedor;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "documentos_solicitacao_vendedor")
@Getter
@Setter
public class DocumentoSolicitacaoVendedor extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "solicitacao_id", nullable = false)
    private SolicitacaoVendedor solicitacao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoDocumentoSolicitacaoVendedor tipo;

    @Column(nullable = false)
    private String arquivo;
}