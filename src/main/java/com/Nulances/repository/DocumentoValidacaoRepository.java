package com.Nulances.repository;

import com.Nulances.domain.entity.DocumentoValidacao;
import com.Nulances.domain.enums.StatusDocumentoValidacao;
import com.Nulances.domain.enums.TipoDocumentoValidacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DocumentoValidacaoRepository extends JpaRepository<DocumentoValidacao, UUID> {

    List<DocumentoValidacao> findByUsuarioId(UUID usuarioId);

    Optional<DocumentoValidacao> findByUsuarioIdAndTipo(UUID usuarioId, TipoDocumentoValidacao tipo);

    List<DocumentoValidacao> findByStatus(StatusDocumentoValidacao status);
}