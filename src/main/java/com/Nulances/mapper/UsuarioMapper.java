package com.Nulances.mapper;

import com.Nulances.domain.entity.Usuario;
import com.Nulances.dto.response.AdminUsuarioListResponse;
import com.Nulances.dto.response.AdminUsuarioResponse;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMapper {

    public AdminUsuarioListResponse toListResponse(Usuario usuario) {
        return new AdminUsuarioListResponse(
                usuario.getId(),
                usuario.getNomeCompleto(),
                usuario.getEmail(),
                usuario.getCidade(),
                usuario.getRole(),
                usuario.getTelefone()
        );
    }

    public AdminUsuarioResponse toResponse(Usuario usuario) {
        return new AdminUsuarioResponse(
                usuario.getId(),
                usuario.getNomeCompleto(),
                usuario.getDataNascimento(),
                usuario.getEmail(),
                usuario.getCpf(),
                usuario.getTelefone(),
                usuario.getFotoPerfil(),
                usuario.getCep(),
                usuario.getLogradouro(),
                usuario.getCidade(),
                usuario.getEstado(),
                usuario.getEmailVerificado(),
                usuario.getEmailVerificadoEm(),
                usuario.getRole(),
                usuario.getCreatedAt(),
                usuario.getUpdatedAt()
        );
    }
}