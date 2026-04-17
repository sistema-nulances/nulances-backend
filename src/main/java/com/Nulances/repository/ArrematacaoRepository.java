package com.Nulances.repository;

import com.Nulances.domain.entity.Arrematacao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ArrematacaoRepository extends JpaRepository<Arrematacao, UUID> {

    boolean existsByLeilaoLoteBemId(UUID leilaoLoteBemId);

    Optional<Arrematacao> findByLeilaoLoteBemId(UUID leilaoLoteBemId);

    Optional<Arrematacao> findByLanceVencedorId(UUID lanceVencedorId);

    Page<Arrematacao> findByUsuarioId(UUID usuarioId, Pageable pageable);

}