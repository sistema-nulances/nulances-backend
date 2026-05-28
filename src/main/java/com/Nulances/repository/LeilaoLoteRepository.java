package com.Nulances.repository;

import com.Nulances.domain.entity.LeilaoLote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface LeilaoLoteRepository extends JpaRepository<LeilaoLote, UUID> {

    boolean existsByLoteId(UUID loteId);

    List<LeilaoLote> findByLeilaoId(UUID leilaoId);

    @Query("""
    select distinct ll
    from LeilaoLote ll
    left join fetch ll.lote lote
    left join fetch ll.bens item
    left join fetch item.bem bem
    left join fetch item.maiorLance maiorLance
    left join fetch maiorLance.usuario maiorLanceUsuario
    where ll.leilao.id = :leilaoId
""")
    List<LeilaoLote> findByLeilaoIdForPainel(@Param("leilaoId") UUID leilaoId);
}