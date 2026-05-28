package com.Nulances.domain.entity;

import com.Nulances.domain.enums.TipoBanner;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "banners")
@Getter
@Setter
public class Banner extends AuditableEntity {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoBanner tipo;

    @Column(nullable = false)
    private Integer posicao;

    @Column(name = "texto_alternativo")
    private String textoAlternativo;

    @Column(nullable = false, length = 500)
    private String imagem;

    @Column(nullable = false)
    private Boolean ativo = true;

    /** Ponto focal da imagem em formato CSS: ex. "50% 30%". Nulo = "50% 50%" (centro). */
    @Column(name = "object_position", length = 20)
    private String objectPosition;
}