package com.Nulances.repository;

import com.Nulances.domain.entity.SolicitacaoVendedor;
import com.Nulances.domain.enums.StatusSolicitacaoVendedor;
import com.Nulances.dto.response.AdminMarketplaceSolicitacaoPendenteProjection;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SolicitacaoVendedorRepository extends JpaRepository<SolicitacaoVendedor, UUID> {

    Optional<SolicitacaoVendedor> findByUsuario_Id(UUID usuarioId);

    List<SolicitacaoVendedor> findAllByStatusOrderByCreatedAtDesc(StatusSolicitacaoVendedor status);

    List<SolicitacaoVendedor> findAllByOrderByCreatedAtDesc();

    @EntityGraph(attributePaths = {"documentos", "usuario"})
    Optional<SolicitacaoVendedor> findDetalhadaById(UUID id);

    @Query("""
        select new com.Nulances.dto.response.AdminMarketplaceSolicitacaoPendenteProjection(
            s.id,
            s.usuario.id,
            s.tipoPessoa,
            case
                when s.tipoPessoa = com.Nulances.domain.enums.TipoPessoaVendedor.PESSOA_FISICA then s.nomeCompleto
                else s.razaoSocial
            end,
            s.cpf,
            s.cnpj,
            s.email,
            s.telefone,
            s.cidade,
            s.estado,
            s.informacoesNegocio,
            s.usuario.fotoPerfil,
            s.createdAt
        )
        from SolicitacaoVendedor s
        where s.status = com.Nulances.domain.enums.StatusSolicitacaoVendedor.PENDENTE
        order by s.createdAt desc
    """)
    List<AdminMarketplaceSolicitacaoPendenteProjection> listarPendentesAdmin();

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query(value = """
        update solicitacoes_vendedor
           set status = :status,
               analisado_em = :analisadoEm,
               analisado_por_id = :analisadoPorId,
               observacao_admin = :observacaoAdmin
         where id = :solicitacaoId
    """, nativeQuery = true)
    int atualizarAnaliseNativa(
            @Param("solicitacaoId") UUID solicitacaoId,
            @Param("status") String status,
            @Param("analisadoEm") Timestamp analisadoEm,
            @Param("analisadoPorId") UUID analisadoPorId,
            @Param("observacaoAdmin") String observacaoAdmin
    );
}