package com.Nulances.dto.request;

import com.Nulances.domain.enums.StatusAnuncio;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ListarMeusAnunciosRequest {

    private String busca;
    private StatusAnuncio status;
}