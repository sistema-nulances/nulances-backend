package com.Nulances.service;

import com.Nulances.config.security.CustomUserDetails;
import com.Nulances.domain.entity.Anuncio;
import com.Nulances.domain.entity.Usuario;
import com.Nulances.domain.enums.StatusAnuncio;
import com.Nulances.domain.enums.UserRole;
import com.Nulances.dto.request.ListarAdminAnunciosRequest;
import com.Nulances.dto.request.SuspenderAnuncioRequest;
import com.Nulances.dto.response.AnuncioAdminListResponse;
import com.Nulances.dto.response.AnuncioModerarListResponse;
import com.Nulances.dto.response.AnuncioStatusResponse;
import com.Nulances.dto.response.DashboardStatsMarketplaceResponse;
import com.Nulances.mapper.AnuncioMapper;
import com.Nulances.mapper.AnuncioStatusMapper;
import com.Nulances.repository.AnuncioRepository;
import com.Nulances.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AnuncioModerarService {

    private final AnuncioRepository anuncioRepository;
    private final UsuarioRepository usuarioRepository;
    private final AnuncioMapper anuncioMapper;
    private final AnuncioStatusMapper anuncioStatusMapper;

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(readOnly = true)
    public Page<AnuncioModerarListResponse> listarFilaModeracaoDashboard(
            Pageable pageable,
            CustomUserDetails userDetails
    ) {
        Usuario admin = buscarUsuarioAutenticado(userDetails);
        validarAdmin(admin);

        Page<Anuncio> page = anuncioRepository.findByStatusOrderByCreatedAtAsc(
                StatusAnuncio.PENDENTE,
                pageable
        );

        return page.map(anuncioMapper::toModerarListResponse);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(readOnly = true)
    public Page<AnuncioAdminListResponse> listarParaAdmin(
            ListarAdminAnunciosRequest request,
            Pageable pageable,
            CustomUserDetails userDetails
    ) {
        Usuario admin = buscarUsuarioAutenticado(userDetails);
        validarAdmin(admin);

        String busca = request != null ? normalizarTextoOpcional(request.getBusca()) : null;
        String vendedor = request != null ? normalizarTextoOpcional(request.getVendedor()) : null;
        StatusAnuncio status = request != null ? request.getStatus() : null;

        Page<Anuncio> page;

        if (status == null && busca == null && vendedor == null) {
            page = anuncioRepository.findAllByOrderByCreatedAtDesc(pageable);
        } else if (status != null && busca == null && vendedor == null) {
            page = anuncioRepository.findByStatusOrderByCreatedAtDesc(status, pageable);
        } else if (status == null && busca != null && vendedor == null) {
            page = anuncioRepository.findByModeloContainingIgnoreCaseOrderByCreatedAtDesc(busca, pageable);
        } else if (status == null && busca == null && vendedor != null) {
            page = anuncioRepository.findByVendedorNomeCompletoContainingIgnoreCaseOrderByCreatedAtDesc(vendedor, pageable);
        } else if (status != null && busca != null && vendedor == null) {
            page = anuncioRepository.findByStatusAndModeloContainingIgnoreCaseOrderByCreatedAtDesc(
                    status, busca, pageable
            );
        } else if (status != null && busca == null && vendedor != null) {
            page = anuncioRepository.findByStatusAndVendedorNomeCompletoContainingIgnoreCaseOrderByCreatedAtDesc(
                    status, vendedor, pageable
            );
        } else if (status == null) {
            page = anuncioRepository.findByModeloContainingIgnoreCaseAndVendedorNomeCompletoContainingIgnoreCaseOrderByCreatedAtDesc(
                    busca, vendedor, pageable
            );
        } else {
            page = anuncioRepository.findByStatusAndModeloContainingIgnoreCaseAndVendedorNomeCompletoContainingIgnoreCaseOrderByCreatedAtDesc(
                    status, busca, vendedor, pageable
            );
        }

        return page.map(anuncioMapper::toAdminListResponse);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public AnuncioStatusResponse aprovar(UUID anuncioId, CustomUserDetails userDetails) {
        Usuario admin = buscarUsuarioAutenticado(userDetails);
        validarAdmin(admin);

        Anuncio anuncio = anuncioRepository.findDetalhadoById(anuncioId)
                .orElseThrow(() -> new IllegalArgumentException("Anúncio não encontrado."));

        if (anuncio.getStatus() == StatusAnuncio.PUBLICADO) {
            throw new IllegalArgumentException("Este anúncio já está aprovado.");
        }

        if (anuncio.getStatus() == StatusAnuncio.SUSPENSO) {
            throw new IllegalArgumentException("Anúncio suspenso deve ser reativado, não aprovado.");
        }

        if (anuncio.getStatus() != StatusAnuncio.PENDENTE) {
            throw new IllegalArgumentException("Somente anúncios pendentes podem ser aprovados.");
        }

        anuncio.setStatus(StatusAnuncio.PUBLICADO);
        anuncio = anuncioRepository.save(anuncio);

        return anuncioStatusMapper.toResponse(
                anuncio,
                "Anúncio aprovado com sucesso.",
                null
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public AnuncioStatusResponse reativar(UUID anuncioId, CustomUserDetails userDetails) {
        Usuario admin = buscarUsuarioAutenticado(userDetails);
        validarAdmin(admin);

        Anuncio anuncio = anuncioRepository.findDetalhadoById(anuncioId)
                .orElseThrow(() -> new IllegalArgumentException("Anúncio não encontrado."));

        if (anuncio.getStatus() == StatusAnuncio.PUBLICADO) {
            throw new IllegalArgumentException("Este anúncio já está publicado.");
        }

        if (anuncio.getStatus() != StatusAnuncio.SUSPENSO) {
            throw new IllegalArgumentException("Somente anúncios suspensos podem ser reativados.");
        }

        anuncio.setStatus(StatusAnuncio.PUBLICADO);
        anuncio = anuncioRepository.save(anuncio);

        return anuncioStatusMapper.toResponse(
                anuncio,
                "Anúncio reativado com sucesso.",
                null
        );
    }

    @PreAuthorize("hasAnyRole('VENDEDOR','ADMIN')")
    @Transactional
    public AnuncioStatusResponse suspender(
            UUID anuncioId,
            SuspenderAnuncioRequest request,
            CustomUserDetails userDetails
    ) {
        Usuario usuario = buscarUsuarioAutenticado(userDetails);

        Anuncio anuncio = buscarAnuncioParaSuspensao(anuncioId, usuario);

        if (anuncio.getStatus() == StatusAnuncio.SUSPENSO) {
            throw new IllegalArgumentException("Este anúncio já está suspenso.");
        }

        if (anuncio.getStatus() != StatusAnuncio.PENDENTE && anuncio.getStatus() != StatusAnuncio.PUBLICADO) {
            throw new IllegalArgumentException("Somente anúncios pendentes ou publicados podem ser suspensos.");
        }

        anuncio.setStatus(StatusAnuncio.SUSPENSO);
        anuncio = anuncioRepository.save(anuncio);

        String motivo = request != null ? normalizarTextoOpcional(request.getMotivo()) : null;

        return anuncioStatusMapper.toResponse(
                anuncio,
                "Anúncio suspenso com sucesso.",
                motivo
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(readOnly = true)
    public DashboardStatsMarketplaceResponse buscarDashboardStatsMarketplace(
            CustomUserDetails userDetails
    ) {
        Usuario admin = buscarUsuarioAutenticado(userDetails);
        validarAdmin(admin);

        long totalAnuncios = anuncioRepository.count();
        long totalPublicados = anuncioRepository.countByStatus(StatusAnuncio.PUBLICADO);
        long totalPendentes = anuncioRepository.countByStatus(StatusAnuncio.PENDENTE);
        long totalSuspensos = anuncioRepository.countByStatus(StatusAnuncio.SUSPENSO);

        return DashboardStatsMarketplaceResponse.builder()
                .totalAnuncios(totalAnuncios)
                .totalPublicados(totalPublicados)
                .totalPendentes(totalPendentes)
                .totalSuspensos(totalSuspensos)
                .build();
    }

    private Anuncio buscarAnuncioParaSuspensao(UUID anuncioId, Usuario usuario) {
        if (usuario.getRole() == UserRole.ADMIN) {
            return anuncioRepository.findDetalhadoById(anuncioId)
                    .orElseThrow(() -> new IllegalArgumentException("Anúncio não encontrado."));
        }

        if (usuario.getRole() == UserRole.VENDEDOR) {
            return anuncioRepository.findDetalhadoByIdAndVendedorId(anuncioId, usuario.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Anúncio não encontrado."));
        }

        throw new IllegalArgumentException("Você não tem permissão para suspender anúncios.");
    }

    private Usuario buscarUsuarioAutenticado(CustomUserDetails userDetails) {
        return usuarioRepository.findById(userDetails.getId())
                .orElseThrow(() -> new IllegalArgumentException("Usuário autenticado não encontrado."));
    }

    private void validarAdmin(Usuario usuario) {
        if (usuario.getRole() != UserRole.ADMIN) {
            throw new IllegalArgumentException("Somente administradores podem acessar esta funcionalidade.");
        }
    }

    private String normalizarTextoOpcional(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        return valor.trim();
    }
}