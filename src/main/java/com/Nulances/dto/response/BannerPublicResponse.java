package com.Nulances.dto.response;

import com.Nulances.domain.enums.TipoBanner;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class BannerPublicResponse {
    private UUID id;
    private TipoBanner tipo;
    private Integer posicao;
    private String textoAlternativo;
    private String imagem;
    private String objectPosition;
}