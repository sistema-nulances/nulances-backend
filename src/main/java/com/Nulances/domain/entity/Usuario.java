package com.Nulances.domain.entity;

import com.Nulances.domain.enums.UserRole;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "usuarios")
@Getter
@Setter
public class Usuario extends AuditableEntity {

    @Column(name = "nome_completo", nullable = false)
    private String nomeCompleto;

    @Column(name = "data_nascimento", nullable = false)
    private LocalDate dataNascimento;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String senha;

    @Column(nullable = false, unique = true, length = 14)
    private String cpf;

    @Column(length = 20, unique = true)
    private String telefone;

    @Column(name = "foto_perfil", length = 500)
    private String fotoPerfil;

    @Column(length = 9)
    private String cep;

    private String logradouro;
    private String cidade;

    @Column(length = 2)
    private String estado;

    @Column(name = "email_verificado", nullable = false)
    private Boolean emailVerificado = false;

    @Column(name = "email_verificado_em")
    private Instant emailVerificadoEm;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role = UserRole.COMUM;

    @OneToMany(mappedBy = "usuario", fetch = FetchType.LAZY)
    private List<UsuarioConfirmacaoEmail> confirmacoesEmail = new ArrayList<>();

    @OneToMany(mappedBy = "usuario", fetch = FetchType.LAZY)
    private List<DocumentoValidacao> documentosValidacao = new ArrayList<>();

    @OneToMany(mappedBy = "usuario", fetch = FetchType.LAZY)
    private List<DocumentoVendedor> documentosVendedor = new ArrayList<>();

    @OneToMany(mappedBy = "usuario", fetch = FetchType.LAZY)
    private List<Lance> lances = new ArrayList<>();

    @OneToMany(mappedBy = "usuario", fetch = FetchType.LAZY)
    private List<Arrematacao> arrematacoes = new ArrayList<>();

    @OneToMany(mappedBy = "vendedor", fetch = FetchType.LAZY)
    private List<Anuncio> anuncios = new ArrayList<>();

    @OneToMany(mappedBy = "usuario", fetch = FetchType.LAZY)
    private List<UsuarioAvisoAceito> avisosAceitos = new ArrayList<>();

    @OneToOne(mappedBy = "usuario", fetch = FetchType.LAZY)
    private SolicitacaoVendedor solicitacaoVendedor;
}