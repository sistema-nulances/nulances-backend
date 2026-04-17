package com.Nulances.repository;

import com.Nulances.domain.entity.Leilao;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LeilaoRepository extends JpaRepository<Leilao, UUID> {

    @Modifying
    @Transactional
    @Query("""
        update Leilao l
        set l.comitente = null
        where l.comitente.id = :comitenteId
    """)
    void desvincularComitenteDosLeiloes(@Param("comitenteId") UUID comitenteId);

    @Query("""
    select count(l)
    from Leilao l
    where l.inicioLeilao <= :agora
      and l.fimLeilao > :agora
""")
    long countAoVivo(@Param("agora") Instant agora);

    @Query("""
    select count(l)
    from Leilao l
    where l.inicioLeilao > :agora
""")
    long countEmBreve(@Param("agora") Instant agora);

    @Modifying
    @Transactional
    @Query("""
        update Leilao l
        set l.status = com.Nulances.domain.enums.StatusLeilao.ENCERRADO
        where l.status <> com.Nulances.domain.enums.StatusLeilao.ENCERRADO
          and not exists (
              select 1
              from LeilaoLoteBem ilb
              where ilb.leilaoLote.leilao = l
                and ilb.status in (
                    com.Nulances.domain.enums.StatusItemLeilao.AGUARDANDO_ABERTURA,
                    com.Nulances.domain.enums.StatusItemLeilao.ABERTO,
                    com.Nulances.domain.enums.StatusItemLeilao.PROCESSANDO_RESULTADO
                )
          )
    """)
    int atualizarLeiloesEncerradosAutomaticamente();

    /**
     * Fetch "leve" para evitar erro de múltiplas coleções (bags) no mesmo join fetch.
     * Coleções mais profundas (bens/midias) ficam lazy e são carregadas no serviço (transação readOnly).
     */
    @Query("""
        select distinct l
        from Leilao l
        left join fetch l.leiloeiro
        left join fetch l.comitente
        left join fetch l.lotes ll
        left join fetch ll.lote lote
        order by l.createdAt desc
    """)
    List<Leilao> findAllDetailed();

    @Query("""
        select distinct l
        from Leilao l
        left join fetch l.leiloeiro
        left join fetch l.comitente
        left join fetch l.lotes ll
        left join fetch ll.lote lote
        where l.id = :id
    """)
    Optional<Leilao> findDetailedById(@Param("id") UUID id);

    @Query("""
        select distinct l from Leilao l
        join fetch l.leiloeiro leil
        join fetch l.comitente c
        left join fetch l.lotes ll
        left join fetch ll.lote lot
        left join fetch ll.bens item
        left join fetch item.bem b
        left join fetch b.marca m
        where l.id = :id
        """)
    Optional<Leilao> findByIdComDadosPainel(@Param("id") UUID id);

    @Modifying
    @Transactional
    @Query("""
        update Leilao l
        set l.status = com.Nulances.domain.enums.StatusLeilao.AO_VIVO
        where l.id = :leilaoId
          and l.status = com.Nulances.domain.enums.StatusLeilao.EM_BREVE
    """)
    int atualizarLeilaoParaAoVivo(@Param("leilaoId") UUID leilaoId);

    @Query("""
    select l
    from Leilao l
    left join fetch l.leiloeiro leiloeiro
    left join fetch l.comitente comitente
    where l.id = :id
""")
    Optional<Leilao> findPainelById(@Param("id") UUID id);
}