package com.Nulances.repository;

import com.Nulances.domain.entity.PagamentoPlano;
import com.Nulances.domain.enums.StatusPagamentoPlano;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PagamentoPlanoRepository extends JpaRepository<PagamentoPlano, UUID> {
    Optional<PagamentoPlano> findByReferencia(String referencia);
    Optional<PagamentoPlano> findByMercadoPagoPaymentId(String mercadoPagoPaymentId);
    Optional<PagamentoPlano> findByMercadoPagoPreferenceId(String mercadoPagoPreferenceId);
    Optional<PagamentoPlano> findFirstByAssinaturaIdAndStatusOrderByCreatedAtDesc(UUID assinaturaId, StatusPagamentoPlano status);
    List<PagamentoPlano> findByAssinaturaVendedorIdOrderByCreatedAtDesc(UUID vendedorId);
    List<PagamentoPlano> findAllByOrderByCreatedAtDesc();
}
