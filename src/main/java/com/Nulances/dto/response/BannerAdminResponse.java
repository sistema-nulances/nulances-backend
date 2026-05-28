package com.Nulances.dto.response;

import com.Nulances.domain.enums.TipoBanner;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
public class BannerAdminResponse {
    private UUID id;
    private TipoBanner tipo;
    private Integer posicao;
    private String textoAlternativo;
    private String imagem;
    private String arquivoUrl;
    private Boolean ativo;
    private String objectPosition;
    private Instant createdAt;
    private Instant updatedAt;
}