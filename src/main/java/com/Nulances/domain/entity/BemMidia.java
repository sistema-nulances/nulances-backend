package com.Nulances.domain.entity;

import com.Nulances.domain.enums.TipoMidiaBem;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "bem_midias")
@Getter
@Setter
public class BemMidia extends CreatedOnlyEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bem_id", nullable = false)
    private Bem bem;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoMidiaBem tipo;

    @Column(nullable = false, length = 500)
    private String arquivo;

    @Column(nullable = false)
    private Integer ordem = 0;
}
