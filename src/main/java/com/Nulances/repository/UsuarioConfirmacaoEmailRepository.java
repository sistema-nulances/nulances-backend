package com.Nulances.repository;

import com.Nulances.domain.entity.UsuarioConfirmacaoEmail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UsuarioConfirmacaoEmailRepository extends JpaRepository<UsuarioConfirmacaoEmail, UUID> {

    Optional<UsuarioConfirmacaoEmail> findTopByUsuarioIdAndUsadoFalseOrderByCreatedAtDesc(UUID usuarioId);

    Optional<UsuarioConfirmacaoEmail> findTopByUsuarioIdAndCodigoAndUsadoFalseOrderByCreatedAtDesc(UUID usuarioId, String codigo);
}