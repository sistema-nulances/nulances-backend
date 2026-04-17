package com.Nulances.service;

import com.Nulances.config.security.CustomUserDetails;
import com.Nulances.domain.entity.SolicitacaoVendedor;
import com.Nulances.domain.entity.Usuario;
import com.Nulances.domain.enums.StatusContaMarketplaceAdmin;
import com.Nulances.domain.enums.StatusSolicitacaoVendedor;
import com.Nulances.domain.enums.UserRole;
import com.Nulances.dto.request.RecusarSolicitacaoVendedorRequest;
import com.Nulances.dto.response.AdminMarketplaceSolicitacaoPendenteDetalheResponse;
import com.Nulances.dto.response.AdminMarketplaceVendedorListItemResponse;
import com.Nulances.mapper.AdminMarketplaceVendedorMapper;
import com.Nulances.repository.SolicitacaoVendedorRepository;
import com.Nulances.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class AdminMarketplaceVendedorService {

    private final UsuarioRepository usuarioRepository;
    private final SolicitacaoVendedorRepository solicitacaoVendedorRepository;
    private final AdminMarketplaceVendedorMapper mapper;

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(readOnly = true)
    public List<AdminMarketplaceVendedorListItemResponse> listar(
            StatusContaMarketplaceAdmin status,
            String search
    ) {
        StatusContaMarketplaceAdmin statusFiltro =
                status != null ? status : StatusContaMarketplaceAdmin.TODOS;

        List<AdminMarketplaceVendedorListItemResponse> ativos =
                statusFiltro == StatusContaMarketplaceAdmin.PENDENTE
                        ? List.of()
                        : usuarioRepository.listarVendedoresAdmin().stream()
                        .map(mapper::toAtivoResponse)
                        .toList();

        List<AdminMarketplaceVendedorListItemResponse> pendentes =
                statusFiltro == StatusContaMarketplaceAdmin.ATIVO
                        ? List.of()
                        : solicitacaoVendedorRepository.listarPendentesAdmin().stream()
                        .map(mapper::toPendenteResponse)
                        .toList();

        String termo = normalizar(search);

        return Stream.concat(ativos.stream(), pendentes.stream())
                .filter(item -> matchBusca(item, termo))
                .sorted(
                        Comparator.comparing(
                                        AdminMarketplaceVendedorListItemResponse::getDataSolicitacao,
                                        Comparator.nullsLast(Comparator.reverseOrder())
                                )
                                .thenComparing(
                                        AdminMarketplaceVendedorListItemResponse::getDataAprovacao,
                                        Comparator.nullsLast(Comparator.reverseOrder())
                                )
                )
                .toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(readOnly = true)
    public AdminMarketplaceSolicitacaoPendenteDetalheResponse buscarDetalhePendente(UUID solicitacaoId) {
        SolicitacaoVendedor solicitacao = buscarSolicitacaoDetalhada(solicitacaoId);

        if (solicitacao.getStatus() != StatusSolicitacaoVendedor.PENDENTE) {
            throw new IllegalArgumentException("A solicitação informada não está pendente.");
        }

        return mapper.toDetalhePendenteResponse(solicitacao);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void aprovarSolicitacao(UUID solicitacaoId, CustomUserDetails userDetails) {
        Usuario admin = buscarUsuarioAutenticado(userDetails);
        SolicitacaoVendedor solicitacao = buscarSolicitacaoParaAtualizacao(solicitacaoId);

        if (solicitacao.getStatus() != StatusSolicitacaoVendedor.PENDENTE) {
            throw new IllegalArgumentException("Somente solicitações pendentes podem ser aprovadas.");
        }

        Usuario usuario = solicitacao.getUsuario();

        if (usuario.getRole() != UserRole.COMUM) {
            throw new IllegalArgumentException("Somente usuários COMUM podem ser aprovados como vendedor.");
        }

        usuario.setRole(UserRole.VENDEDOR);
        usuarioRepository.saveAndFlush(usuario);

        int linhasAfetadas = solicitacaoVendedorRepository.atualizarAnaliseNativa(
                solicitacaoId,
                StatusSolicitacaoVendedor.APROVADA.name(),
                Timestamp.from(Instant.now()),
                admin.getId(),
                null
        );

        if (linhasAfetadas == 0) {
            throw new IllegalStateException("Não foi possível atualizar a solicitação para APROVADA.");
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void recusarSolicitacao(
            UUID solicitacaoId,
            RecusarSolicitacaoVendedorRequest request,
            CustomUserDetails userDetails
    ) {
        Usuario admin = buscarUsuarioAutenticado(userDetails);
        SolicitacaoVendedor solicitacao = buscarSolicitacaoParaAtualizacao(solicitacaoId);

        if (solicitacao.getStatus() != StatusSolicitacaoVendedor.PENDENTE) {
            throw new IllegalArgumentException("Somente solicitações pendentes podem ser recusadas.");
        }

        int linhasAfetadas = solicitacaoVendedorRepository.atualizarAnaliseNativa(
                solicitacaoId,
                StatusSolicitacaoVendedor.RECUSADA.name(),
                Timestamp.from(Instant.now()),
                admin.getId(),
                trim(request.getObservacao())
        );

        if (linhasAfetadas == 0) {
            throw new IllegalStateException("Não foi possível atualizar a solicitação para RECUSADA.");
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void revogarVendedor(UUID usuarioId, CustomUserDetails userDetails) {
        buscarUsuarioAutenticado(userDetails);

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));

        if (usuario.getRole() != UserRole.VENDEDOR) {
            throw new IllegalArgumentException("Somente usuários com perfil VENDEDOR podem ser revogados.");
        }

        usuario.setRole(UserRole.COMUM);
        usuarioRepository.saveAndFlush(usuario);
    }

    private SolicitacaoVendedor buscarSolicitacaoDetalhada(UUID solicitacaoId) {
        return solicitacaoVendedorRepository.findDetalhadaById(solicitacaoId)
                .orElseThrow(() -> new IllegalArgumentException("Solicitação não encontrada."));
    }

    private SolicitacaoVendedor buscarSolicitacaoParaAtualizacao(UUID solicitacaoId) {
        return solicitacaoVendedorRepository.findById(solicitacaoId)
                .orElseThrow(() -> new IllegalArgumentException("Solicitação não encontrada."));
    }

    private Usuario buscarUsuarioAutenticado(CustomUserDetails userDetails) {
        return usuarioRepository.findById(userDetails.getId())
                .orElseThrow(() -> new IllegalArgumentException("Usuário autenticado não encontrado."));
    }

    private boolean matchBusca(AdminMarketplaceVendedorListItemResponse item, String termo) {
        if (termo == null || termo.isBlank()) {
            return true;
        }

        return contains(item.getNomeExibicao(), termo)
                || contains(item.getCpfOuCnpj(), termo)
                || contains(item.getEmail(), termo)
                || contains(item.getTelefone(), termo)
                || contains(item.getCidade(), termo)
                || contains(item.getEstado(), termo)
                || contains(item.getEndereco(), termo)
                || contains(String.valueOf(item.getId()), termo);
    }

    private boolean contains(String valor, String termo) {
        return valor != null && normalizar(valor).contains(termo);
    }

    private String normalizar(String valor) {
        return valor == null ? "" : valor.trim().toLowerCase(Locale.ROOT);
    }

    private String trim(String valor) {
        return valor == null ? null : valor.trim();
    }
}