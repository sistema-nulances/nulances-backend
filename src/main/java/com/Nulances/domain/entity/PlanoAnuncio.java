package com.Nulances.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "planos_anuncio")
@Getter
@Setter
public class PlanoAnuncio extends AuditableEntity {

    @Column(nullable = false, unique = true, length = 100)
    private String nome;

    @Column(length = 255)
    private String descricao;

    @Column(name = "valor_mensal", nullable = false, precision = 15, scale = 2)
    private BigDecimal valorMensal;

    @Column(name = "total_anuncios", nullable = false)
    private Integer totalAnuncios;

    @Column(nullable = false)
    private Boolean ilimitado = false;

    @Column(nullable = false)
    private Boolean ativo = true;
}
