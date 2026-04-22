package com.Nulances.repository;

import com.Nulances.domain.entity.PlanoAnuncio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PlanoAnuncioRepository extends JpaRepository<PlanoAnuncio, UUID> {
    List<PlanoAnuncio> findAllByAtivoTrueOrderByValorMensalAsc();
    Optional<PlanoAnuncio> findByNomeIgnoreCase(String nome);
}
