package com.Nulances.repository;

import com.Nulances.domain.entity.UsuarioRecuperacaoSenha;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UsuarioRecuperacaoSenhaRepository extends JpaRepository<UsuarioRecuperacaoSenha, UUID> {

    Optional<UsuarioRecuperacaoSenha> findTopByUsuarioIdAndUsadoFalseOrderByCreatedAtDesc(UUID usuarioId);

    Optional<UsuarioRecuperacaoSenha> findTopByUsuarioIdAndCodigoAndUsadoFalseOrderByCreatedAtDesc(UUID usuarioId, String codigo);
}