package com.Nulances.repository;

import com.Nulances.domain.entity.Anuncio;
import com.Nulances.domain.enums.StatusAnuncio;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface AnuncioRepository extends JpaRepository<Anuncio, UUID> {

    @EntityGraph(attributePaths = {"marca", "vendedor", "midias"})
    Page<Anuncio> findByVendedorIdOrderByCreatedAtDesc(UUID vendedorId, Pageable pageable);

    @EntityGraph(attributePaths = {"marca", "vendedor", "midias"})
    Page<Anuncio> findByVendedorIdAndStatusOrderByCreatedAtDesc(
            UUID vendedorId,
            StatusAnuncio status,
            Pageable pageable
    );

    @EntityGraph(attributePaths = {"marca", "vendedor", "midias"})
    Page<Anuncio> findByVendedorIdAndModeloContainingIgnoreCaseOrderByCreatedAtDesc(
            UUID vendedorId,
            String modelo,
            Pageable pageable
    );

    @EntityGraph(attributePaths = {"marca", "vendedor", "midias"})
    Page<Anuncio> findByVendedorIdAndStatusAndModeloContainingIgnoreCaseOrderByCreatedAtDesc(
            UUID vendedorId,
            StatusAnuncio status,
            String modelo,
            Pageable pageable
    );

    @EntityGraph(attributePaths = {"marca", "vendedor", "midias"})
    Page<Anuncio> findAllByOrderByCreatedAtDesc(Pageable pageable);

    @EntityGraph(attributePaths = {"marca", "vendedor", "midias"})
    Page<Anuncio> findByStatusOrderByCreatedAtDesc(
            StatusAnuncio status,
            Pageable pageable
    );

    @EntityGraph(attributePaths = {"marca", "vendedor", "midias"})
    Page<Anuncio> findByModeloContainingIgnoreCaseOrderByCreatedAtDesc(
            String modelo,
            Pageable pageable
    );

    @EntityGraph(attributePaths = {"marca", "vendedor", "midias"})
    Page<Anuncio> findByVendedorNomeCompletoContainingIgnoreCaseOrderByCreatedAtDesc(
            String vendedorNome,
            Pageable pageable
    );

    @EntityGraph(attributePaths = {"marca", "vendedor", "midias"})
    Page<Anuncio> findByStatusAndModeloContainingIgnoreCaseOrderByCreatedAtDesc(
            StatusAnuncio status,
            String modelo,
            Pageable pageable
    );

    @EntityGraph(attributePaths = {"marca", "vendedor", "midias"})
    Page<Anuncio> findByStatusAndVendedorNomeCompletoContainingIgnoreCaseOrderByCreatedAtDesc(
            StatusAnuncio status,
            String vendedorNome,
            Pageable pageable
    );

    @EntityGraph(attributePaths = {"marca", "vendedor", "midias"})
    Page<Anuncio> findByModeloContainingIgnoreCaseAndVendedorNomeCompletoContainingIgnoreCaseOrderByCreatedAtDesc(
            String modelo,
            String vendedorNome,
            Pageable pageable
    );

    @EntityGraph(attributePaths = {"marca", "vendedor", "midias"})
    Page<Anuncio> findByStatusAndModeloContainingIgnoreCaseAndVendedorNomeCompletoContainingIgnoreCaseOrderByCreatedAtDesc(
            StatusAnuncio status,
            String modelo,
            String vendedorNome,
            Pageable pageable
    );

    @EntityGraph(attributePaths = {"marca", "midias"})
    Page<Anuncio> findAllByStatusOrderByCreatedAtDesc(
            StatusAnuncio status,
            Pageable pageable
    );

    @EntityGraph(attributePaths = {"marca", "midias"})
    Page<Anuncio> findAllByStatusAndModeloContainingIgnoreCaseOrderByCreatedAtDesc(
            StatusAnuncio status,
            String modelo,
            Pageable pageable
    );

    @Query("""
        select distinct a
        from Anuncio a
        left join fetch a.vendedor
        left join fetch a.marca
        left join fetch a.midias
        left join fetch a.detalheTecnico
        where a.id = :id
    """)
    Optional<Anuncio> findDetalhadoById(@Param("id") UUID id);

    @Query("""
        select distinct a
        from Anuncio a
        left join fetch a.vendedor
        left join fetch a.marca
        left join fetch a.midias
        left join fetch a.detalheTecnico
        where a.id = :id
          and a.vendedor.id = :vendedorId
    """)
    Optional<Anuncio> findDetalhadoByIdAndVendedorId(
            @Param("id") UUID id,
            @Param("vendedorId") UUID vendedorId
    );

    @Query("""
        select distinct a
        from Anuncio a
        left join fetch a.vendedor v
        left join fetch v.solicitacaoVendedor sv
        left join fetch a.marca
        left join fetch a.midias
        left join fetch a.detalheTecnico
        where a.id = :id
          and a.status = com.Nulances.domain.enums.StatusAnuncio.PUBLICADO
    """)
    Optional<Anuncio> findDetalhadoPublicadoById(@Param("id") UUID id);

    @EntityGraph(attributePaths = {"vendedor"})
    Page<Anuncio> findByStatusOrderByCreatedAtAsc(StatusAnuncio status, Pageable pageable);

    long countByStatus(StatusAnuncio status);
}