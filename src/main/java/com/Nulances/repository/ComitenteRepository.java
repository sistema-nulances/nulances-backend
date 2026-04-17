package com.Nulances.repository;

import com.Nulances.domain.entity.Comitente;
import com.Nulances.dto.response.ComitenteListResponse;
import com.Nulances.dto.response.ComitenteStatsResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ComitenteRepository extends JpaRepository<Comitente, UUID> {

    boolean existsByDocumento(String documento);

    boolean existsByDocumentoAndIdNot(String documento, UUID id);

    Optional<Comitente> findByDocumento(String documento);

    @Query("""
        select new com.Nulances.dto.response.ComitenteListResponse(
            c.id,
            c.nome,
            c.ativoPlataforma,
            c.tipo,
            c.documento,
            count(l.id),
            coalesce(sum(case when l.status = com.Nulances.domain.enums.StatusLeilao.AO_VIVO then 1L else 0L end), 0L),
            coalesce(sum(case when l.status = com.Nulances.domain.enums.StatusLeilao.EM_BREVE then 1L else 0L end), 0L),
            coalesce(sum(case when l.status = com.Nulances.domain.enums.StatusLeilao.ENCERRADO then 1L else 0L end), 0L)
        )
        from Comitente c
        left join c.leiloes l
        group by c.id, c.nome, c.ativoPlataforma, c.tipo, c.documento
        order by c.createdAt desc
    """)
    List<ComitenteListResponse> findAllForList();

    @Query("""
        select new com.Nulances.dto.response.ComitenteStatsResponse(
            count(c),
            coalesce(sum(case when c.tipo = com.Nulances.domain.enums.TipoComitente.BANCO then 1L else 0L end), 0L),
            coalesce(sum(case when c.tipo = com.Nulances.domain.enums.TipoComitente.SEGURADORA then 1L else 0L end), 0L),
            coalesce(sum(case when c.tipo = com.Nulances.domain.enums.TipoComitente.PESSOA_FISICA then 1L else 0L end), 0L),
            coalesce(sum(case when c.tipo = com.Nulances.domain.enums.TipoComitente.EMPRESA then 1L else 0L end), 0L)
        )
        from Comitente c
    """)
    ComitenteStatsResponse getStats();
}