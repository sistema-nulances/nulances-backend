package com.Nulances.repository;

import com.Nulances.domain.entity.Leiloeiro;
import com.Nulances.dto.response.LeiloeiroListResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LeiloeiroRepository extends JpaRepository<Leiloeiro, UUID> {

    boolean existsByCpf(String cpf);

    boolean existsByEmail(String email);

    boolean existsByRegistroProfissional(String registroProfissional);

    boolean existsByCpfAndIdNot(String cpf, UUID id);

    boolean existsByEmailAndIdNot(String email, UUID id);

    boolean existsByRegistroProfissionalAndIdNot(String registroProfissional, UUID id);

    Optional<Leiloeiro> findByEmail(String email);

    @Query("""
        select new com.Nulances.dto.response.LeiloeiroListResponse(
            l.id,
            l.nome,
            l.ativoPlataforma,
            l.registroProfissional,
            l.cpf,
            l.email,
            l.telefone,
            l.local,
            count(le.id)
        )
        from Leiloeiro l
        left join l.leiloes le
        group by
            l.id,
            l.nome,
            l.ativoPlataforma,
            l.registroProfissional,
            l.cpf,
            l.email,
            l.telefone,
            l.local,
            l.createdAt
        order by l.createdAt desc
    """)
    List<LeiloeiroListResponse> listarComTotalLeiloes();

    @Query("""
        select count(le.id)
        from Leilao le
        where le.leiloeiro.id = :leiloeiroId
    """)
    long contarLeiloesPorLeiloeiroId(UUID leiloeiroId);

    @Query("""
        select count(l)
        from Leiloeiro l
        where exists (
            select 1
            from Leilao le
            where le.leiloeiro = l
        )
    """)
    long contarLeiloeirosComLeilaoVinculado();

    @Query(value = """
        select
            count(*) as total_leiloeiros,
            count(*) filter (where l.ativo_plataforma = true) as total_leiloeiros_ativos_plataforma,
            count(*) filter (where l.ativo_plataforma = false) as total_leiloeiros_inativos_plataforma,
            count(*) filter (
                where exists (
                    select 1
                    from leiloes le
                    where le.leiloeiro_id = l.id
                )
            ) as total_leiloeiros_com_leilao_vinculado
        from leiloeiros l
    """, nativeQuery = true)
    List<Object[]> buscarStatsLeiloeiros();
}