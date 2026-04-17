package com.Nulances.domain.entity;

import com.Nulances.domain.enums.StatusSolicitacaoVendedor;
import com.Nulances.domain.enums.TipoPessoaVendedor;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "solicitacoes_vendedor",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_solicitacao_vendedor_usuario", columnNames = "usuario_id")
        }
)
@Getter
@Setter
public class SolicitacaoVendedor extends AuditableEntity {

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_pessoa", nullable = false)
    private TipoPessoaVendedor tipoPessoa;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusSolicitacaoVendedor status = StatusSolicitacaoVendedor.PENDENTE;

    private String cpf;
    private String cnpj;

    private String nomeCompleto;
    private String razaoSocial;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String telefone;

    @Column(nullable = false)
    private String cidade;

    @Column(nullable = false, length = 2)
    private String estado;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String informacoesNegocio;

    @Column(columnDefinition = "TEXT")
    private String observacaoAdmin;

    private Instant analisadoEm;

    @ManyToOne(fetch = FetchType.LAZY)
    private Usuario analisadoPor;

    @OneToMany(mappedBy = "solicitacao", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DocumentoSolicitacaoVendedor> documentos = new ArrayList<>();
}