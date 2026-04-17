package com.Nulances.repository;

import com.Nulances.domain.entity.Marca;
import com.Nulances.domain.enums.MarcaVeiculo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface MarcaRepository extends JpaRepository<Marca, UUID> {

    Optional<Marca> findByNome(MarcaVeiculo nome);
    boolean existsByNome(MarcaVeiculo nome);

}