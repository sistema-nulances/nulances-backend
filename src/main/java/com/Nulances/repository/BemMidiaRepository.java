package com.Nulances.repository;

import com.Nulances.domain.entity.BemMidia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface BemMidiaRepository extends JpaRepository<BemMidia, UUID> {

    List<BemMidia> findByBemIdOrderByOrdemAscCreatedAtAsc(UUID bemId);

    void deleteByBemId(UUID bemId);

    @Query("""
            SELECT m FROM BemMidia m
            WHERE m.bem.id IN :bemIds
            ORDER BY m.bem.id, m.ordem ASC, m.createdAt ASC
            """)
    List<BemMidia> findAllByBemIdIn(@Param("bemIds") Collection<UUID> bemIds);
}