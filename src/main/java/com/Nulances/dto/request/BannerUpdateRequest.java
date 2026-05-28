package com.Nulances.dto.request;

import com.Nulances.domain.enums.TipoBanner;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BannerUpdateRequest {

    private TipoBanner tipo;
    private Integer posicao;
    private String textoAlternativo;
    private String imagem;
    private Boolean ativo;

    private String objectPosition;
}