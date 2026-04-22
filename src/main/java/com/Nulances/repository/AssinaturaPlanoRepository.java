package com.Nulances.repository;

import com.Nulances.domain.entity.AssinaturaPlano;
import com.Nulances.domain.enums.StatusAssinaturaPlano;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AssinaturaPlanoRepository extends JpaRepository<AssinaturaPlano, UUID> {
    Optional<AssinaturaPlano> findFirstByVendedorIdOrderByCreatedAtDesc(UUID vendedorId);
    Optional<AssinaturaPlano> findFirstByVendedorIdAndStatusOrderByCreatedAtDesc(UUID vendedorId, StatusAssinaturaPlano status);
    List<AssinaturaPlano> findByStatusAndProximaCobrancaLessThanEqual(StatusAssinaturaPlano status, Instant limite);
    List<AssinaturaPlano> findByStatusAndProximaCobrancaLessThan(StatusAssinaturaPlano status, Instant limite);
}
