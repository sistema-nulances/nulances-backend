package com.Nulances.service;

import com.Nulances.domain.entity.Usuario;
import com.Nulances.dto.request.AdminUsuarioRoleUpdateRequest;
import com.Nulances.dto.request.AdminUsuarioUpdateRequest;
import com.Nulances.dto.response.AdminUsuarioListResponse;
import com.Nulances.dto.response.AdminUsuarioResponse;
import com.Nulances.exception.BusinessException;
import com.Nulances.mapper.UsuarioMapper;
import com.Nulances.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.UUID;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(readOnly = true)
    public Page<AdminUsuarioListResponse> listar(String busca, Pageable pageable) {
        Page<Usuario> pagina;

        if (busca == null || busca.isBlank()) {
            pagina = usuarioRepository.findAllByOrderByCreatedAtDesc(pageable);
        } else {
            pagina = usuarioRepository
                    .findByNomeCompletoContainingIgnoreCaseOrEmailContainingIgnoreCaseOrderByCreatedAtDesc(
                            busca.trim(),
                            busca.trim(),
                            pageable
                    );
        }

        return pagina.map(usuarioMapper::toListResponse);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(readOnly = true)
    public AdminUsuarioResponse buscarPorId(UUID usuarioId) {
        Usuario usuario = buscarEntidadePorId(usuarioId);
        return usuarioMapper.toResponse(usuario);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public AdminUsuarioResponse editarParcial(UUID usuarioId, AdminUsuarioUpdateRequest request) {
        Usuario usuario = buscarEntidadePorId(usuarioId);

        validarCamposUnicos(usuarioId, request.email(), request.cpf(), request.telefone());

        if (request.nomeCompleto() != null) {
            usuario.setNomeCompleto(request.nomeCompleto().trim());
        }

        if (request.dataNascimento() != null) {
            usuario.setDataNascimento(request.dataNascimento());
        }

        if (request.email() != null) {
            usuario.setEmail(request.email().trim().toLowerCase());
        }

        if (request.cpf() != null) {
            usuario.setCpf(request.cpf().trim());
        }

        if (request.telefone() != null) {
            usuario.setTelefone(request.telefone().trim());
        }

        if (request.fotoPerfil() != null) {
            usuario.setFotoPerfil(request.fotoPerfil().trim());
        }

        if (request.cep() != null) {
            usuario.setCep(request.cep().trim());
        }

        if (request.logradouro() != null) {
            usuario.setLogradouro(request.logradouro().trim());
        }

        if (request.cidade() != null) {
            usuario.setCidade(request.cidade().trim());
        }

        if (request.estado() != null) {
            usuario.setEstado(request.estado().trim().toUpperCase());
        }

        if (request.emailVerificado() != null) {
            usuario.setEmailVerificado(request.emailVerificado());

            if (Boolean.TRUE.equals(request.emailVerificado())) {
                if (usuario.getEmailVerificadoEm() == null) {
                    usuario.setEmailVerificadoEm(Instant.now());
                }
            } else {
                usuario.setEmailVerificadoEm(null);
            }
        }

        usuarioRepository.save(usuario);
        return usuarioMapper.toResponse(usuario);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public AdminUsuarioResponse alterarCargo(UUID usuarioId, AdminUsuarioRoleUpdateRequest request) {
        Usuario usuario = buscarEntidadePorId(usuarioId);

        if (request.role() == null) {
            throw new BusinessException("O cargo é obrigatório.");
        }

        usuario.setRole(request.role());
        usuarioRepository.save(usuario);

        return usuarioMapper.toResponse(usuario);
    }

    private void validarCamposUnicos(UUID usuarioId, String email, String cpf, String telefone) {
        if (email != null && usuarioRepository.existsByEmailAndIdNot(email.trim().toLowerCase(), usuarioId)) {
            throw new BusinessException("Já existe um usuário com este e-mail.");
        }

        if (cpf != null && usuarioRepository.existsByCpfAndIdNot(cpf.trim(), usuarioId)) {
            throw new BusinessException("Já existe um usuário com este CPF.");
        }

        if (telefone != null && !telefone.isBlank()
                && usuarioRepository.existsByTelefoneAndIdNot(telefone.trim(), usuarioId)) {
            throw new BusinessException("Já existe um usuário com este telefone.");
        }
    }

    private Usuario buscarEntidadePorId(UUID usuarioId) {
        return usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Usuário não encontrado."));
    }
}