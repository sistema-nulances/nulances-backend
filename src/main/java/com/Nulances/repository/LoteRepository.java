package com.Nulances.repository;

import com.Nulances.domain.entity.Lote;
import com.Nulances.domain.enums.StatusLote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LoteRepository extends JpaRepository<Lote, UUID> {

    boolean existsByCodigo(String codigo);

    long countByStatus(StatusLote status);
}