package com.Nulances.repository;

import com.Nulances.domain.entity.Banner;
import com.Nulances.domain.enums.TipoBanner;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BannerRepository extends JpaRepository<Banner, UUID> {

    List<Banner> findByAtivoTrueAndTipoOrderByPosicaoAscCreatedAtDesc(TipoBanner tipo);

    List<Banner> findAllByOrderByTipoAscPosicaoAscCreatedAtDesc();

    boolean existsByTipoAndPosicao(TipoBanner tipo, Integer posicao);

    boolean existsByTipoAndPosicaoAndIdNot(TipoBanner tipo, Integer posicao, UUID id);
}