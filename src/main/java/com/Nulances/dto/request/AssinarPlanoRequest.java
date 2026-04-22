package com.Nulances.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class AssinarPlanoRequest {

    @NotNull(message = "Plano é obrigatório.")
    private UUID planoId;
}
