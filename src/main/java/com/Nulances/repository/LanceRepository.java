package com.Nulances.repository;

import com.Nulances.domain.entity.Lance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LanceRepository extends JpaRepository<Lance, UUID> {

    boolean existsByLeilaoLoteBemIdAndUsuarioIdAndClientRequestId(
            UUID leilaoLoteBemId,
            UUID usuarioId,
            String clientRequestId
    );

    @Query("""
        select l from Lance l
        join fetch l.usuario u
        where l.leilaoLoteBem.id in :itemIds
        order by l.leilaoLoteBem.id, l.createdAt desc
        """)
    List<Lance> findForPainelByItemIds(@Param("itemIds") Collection<UUID> itemIds);

    boolean existsByLeilaoLoteBemIdAndUsuarioIdAndValor(
            UUID leilaoLoteBemId,
            UUID usuarioId,
            BigDecimal valor
    );

    Optional<Lance> findTopByLeilaoLoteBemIdOrderByCreatedAtDesc(UUID leilaoLoteBemId);

    void deleteByLeilaoLoteBemId(UUID leilaoLoteBemId);

    @Query("""
        select l from Lance l
        join fetch l.usuario u
        where l.leilaoLoteBem.id = :leilaoLoteBemId
        order by l.createdAt desc
        """)
    List<Lance> findTop20ByLeilaoLoteBemIdOrderByCreatedAtDesc(
            @Param("leilaoLoteBemId") UUID leilaoLoteBemId
    );

    @Query("""
            SELECT l FROM Lance l
            JOIN FETCH l.leilaoLoteBem llb
            JOIN FETCH llb.bem b
            JOIN FETCH llb.leilaoLote ll
            JOIN FETCH ll.leilao leilao
            LEFT JOIN FETCH llb.maiorLance ml
            WHERE l.usuario.id = :usuarioId
            ORDER BY l.createdAt DESC
            """)
    List<Lance> findAllForMeusLancesParticipacao(@Param("usuarioId") UUID usuarioId);
}