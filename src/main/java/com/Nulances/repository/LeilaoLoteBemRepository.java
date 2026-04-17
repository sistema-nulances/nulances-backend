package com.Nulances.repository;

import com.Nulances.domain.entity.LeilaoLoteBem;
import com.Nulances.domain.enums.StatusItemLeilao;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LeilaoLoteBemRepository extends JpaRepository<LeilaoLoteBem, UUID> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select i from LeilaoLoteBem i where i.id = :id")
    Optional<LeilaoLoteBem> findByIdForUpdate(UUID id);

    boolean existsByBemId(UUID bemId);

    @Query("""
    select distinct item
    from LeilaoLoteBem item
    join fetch item.leilaoLote ll
    join fetch ll.leilao l
    join fetch ll.lote lote
    join fetch item.bem bem
    left join fetch bem.midias midia
    order by item.aberturaDisputa asc
""")
    List<LeilaoLoteBem> findAllForCards();

    @Query("""
    select item
    from LeilaoLoteBem item
    join fetch item.leilaoLote ll
    join fetch ll.leilao l
    join fetch ll.lote lote
    where l.inicioLeilao <= :agora
      and l.fimLeilao > :agora
    order by l.fimLeilao asc, lote.codigo asc
""")
    List<LeilaoLoteBem> buscarItensLeiloesAoVivo(@Param("agora") Instant agora, Pageable pageable);

    @Query("""
    select distinct i
    from LeilaoLoteBem i
    join fetch i.bem b
    join fetch i.leilaoLote ll
    join fetch ll.lote l
    join fetch ll.leilao le
    join fetch le.leiloeiro
    join fetch le.comitente
    left join fetch b.midias m
    where i.id = :id
""")
    Optional<LeilaoLoteBem> findDetailedById(@Param("id") UUID id);

    List<LeilaoLoteBem> findByStatusAndAberturaDisputaLessThanEqual(StatusItemLeilao status, Instant now);

    List<LeilaoLoteBem> findByStatusAndEncerramentoDisputaLessThanEqual(StatusItemLeilao status, Instant now);
}