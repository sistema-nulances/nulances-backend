package com.Nulances.repository;

import com.Nulances.domain.entity.DocumentoVendedor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DocumentoVendedorRepository extends JpaRepository<DocumentoVendedor, UUID> {

    List<DocumentoVendedor> findByUsuarioId(UUID usuarioId);
}