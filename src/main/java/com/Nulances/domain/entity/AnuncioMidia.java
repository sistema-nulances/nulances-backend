package com.Nulances.domain.entity;

import com.Nulances.domain.enums.TipoMidiaAnuncio;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "anuncio_midias")
@Getter
@Setter
public class AnuncioMidia extends CreatedOnlyEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "anuncio_id", nullable = false)
    private Anuncio anuncio;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoMidiaAnuncio tipo;

    @Column(nullable = false, length = 500)
    private String arquivo;

    @Column(nullable = false)
    private Integer ordem = 0;
}
