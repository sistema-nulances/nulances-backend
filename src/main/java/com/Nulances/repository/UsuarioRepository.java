package com.Nulances.repository;

import com.Nulances.domain.entity.Usuario;
import com.Nulances.domain.enums.UserRole;
import com.Nulances.dto.response.AdminMarketplaceVendedorAtivoProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {

    Optional<Usuario> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByCpf(String cpf);

    boolean existsByTelefone(String telefone);

    @Query("""
        select
            u.id as usuarioId,
            u.nomeCompleto as nomeCompleto,
            u.email as email,
            u.telefone as telefone,
            u.cidade as cidade,
            u.estado as estado,
            u.fotoPerfil as fotoPerfil,
            u.createdAt as createdAt,
            s.analisadoEm as dataAprovacao,
            count(a.id) as totalAnuncios,
            coalesce(sum(case when a.status = com.Nulances.domain.enums.StatusAnuncio.PUBLICADO then 1L else 0L end), 0L) as totalPublicados
        from Usuario u
        left join u.anuncios a
        left join SolicitacaoVendedor s
               on s.usuario.id = u.id
              and s.status = com.Nulances.domain.enums.StatusSolicitacaoVendedor.APROVADA
        where u.role = com.Nulances.domain.enums.UserRole.VENDEDOR
        group by
            u.id,
            u.nomeCompleto,
            u.email,
            u.telefone,
            u.cidade,
            u.estado,
            u.fotoPerfil,
            u.createdAt,
            s.analisadoEm
        order by s.analisadoEm desc, u.createdAt desc
    """)
    List<AdminMarketplaceVendedorAtivoProjection> listarVendedoresAdmin();

    @EntityGraph(attributePaths = {"documentosValidacao", "documentosVendedor"})
    Optional<Usuario> findDetalhadoByEmail(String email);
    Page<Usuario> findAllByOrderByCreatedAtDesc(Pageable pageable);

    Page<Usuario> findByNomeCompletoContainingIgnoreCaseOrEmailContainingIgnoreCaseOrderByCreatedAtDesc(
            String nome,
            String email,
            Pageable pageable
    );

    Optional<Usuario> findByCpf(String cpf);

    Optional<Usuario> findByTelefone(String telefone);

    boolean existsByEmailAndIdNot(String email, UUID id);

    boolean existsByCpfAndIdNot(String cpf, UUID id);

    boolean existsByTelefoneAndIdNot(String telefone, UUID id);

    long countByRole(UserRole role);
}