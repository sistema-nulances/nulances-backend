package com.Nulances.repository;

import com.Nulances.domain.entity.Bem;
import com.Nulances.domain.enums.StatusBem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BemRepository extends JpaRepository<Bem, UUID> {

    Page<Bem> findByStatus(StatusBem status, Pageable pageable);

    Page<Bem> findByModeloContainingIgnoreCase(String modelo, Pageable pageable);

    List<Bem> findAllByIdIn(List<UUID> ids);

    Page<Bem> findByStatusAndModeloContainingIgnoreCase(StatusBem status, String modelo, Pageable pageable);
}