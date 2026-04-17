package com.Nulances.repository;

import com.Nulances.domain.entity.AnuncioMidia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AnuncioMidiaRepository extends JpaRepository<AnuncioMidia, UUID> {

    List<AnuncioMidia> findByAnuncioIdInOrderByOrdemAscCreatedAtAsc(List<UUID> anuncioIds);
}