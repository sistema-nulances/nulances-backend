package com.Nulances.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VerificarCodigoRecuperacaoRequest {

    @NotBlank(message = "O e-mail é obrigatório")
    @Email(message = "E-mail inválido")
    private String email;

    @NotBlank(message = "O código é obrigatório")
    @Pattern(regexp = "^\\d{6}$", message = "O código deve conter 6 dígitos")
    private String codigo;
}