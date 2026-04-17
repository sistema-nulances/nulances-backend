package com.Nulances.dto.request;

import com.Nulances.domain.enums.StatusAnuncio;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ListarAdminAnunciosRequest {

    private String busca;
    private StatusAnuncio status;
    private String vendedor;
}