package com.Nulances.domain.entity;

import com.Nulances.domain.enums.MarcaVeiculo;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "marcas")
@Getter
@Setter
public class Marca extends AuditableEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "nome", nullable = false, unique = true)
    private MarcaVeiculo nome;
}
