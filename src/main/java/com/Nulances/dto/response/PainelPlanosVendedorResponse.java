package com.Nulances.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PainelPlanosVendedorResponse {
    private List<PlanoAnuncioResponse> planosDisponiveis;
    private MinhaAssinaturaPlanoResponse assinaturaAtual;
}
