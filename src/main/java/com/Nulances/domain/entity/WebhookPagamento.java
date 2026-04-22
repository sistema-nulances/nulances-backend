package com.Nulances.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "webhooks_pagamento")
@Getter
@Setter
public class WebhookPagamento extends AuditableEntity {

    @Column(nullable = false, length = 50)
    private String provider;

    @Column(nullable = false, length = 80)
    private String evento;

    @Column(name = "external_id", length = 120)
    private String externalId;

    @Column(nullable = false, columnDefinition = "text")
    private String payload;

    @Column(nullable = false)
    private Boolean processado = false;
}
