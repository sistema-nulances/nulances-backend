package com.Nulances.dto.request;

import com.Nulances.domain.enums.TipoBanner;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BannerCreateRequest {

    @NotNull(message = "Tipo é obrigatório.")
    private TipoBanner tipo;

    @NotNull(message = "Posição é obrigatória.")
    private Integer posicao;

    private String textoAlternativo;

    @NotBlank(message = "A key da imagem é obrigatória.")
    private String imagem;

    private Boolean ativo = true;

    private String objectPosition;
}