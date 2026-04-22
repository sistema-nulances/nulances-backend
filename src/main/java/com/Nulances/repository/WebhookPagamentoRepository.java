package com.Nulances.repository;

import com.Nulances.domain.entity.WebhookPagamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface WebhookPagamentoRepository extends JpaRepository<WebhookPagamento, UUID> {
}
