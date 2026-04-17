package com.Nulances.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TrocaSenhaRequest {

    @NotBlank(message = "O e-mail é obrigatório")
    @Email(message = "E-mail inválido")
    private String email;

    @NotBlank(message = "O código é obrigatório")
    @Pattern(regexp = "^\\d{6}$", message = "O código deve conter 6 dígitos")
    private String codigo;

    @NotBlank(message = "A nova senha é obrigatória")
    @Size(min = 6, max = 100, message = "A nova senha deve ter entre 6 e 100 caracteres")
    private String novaSenha;

    @NotBlank(message = "A confirmação da senha é obrigatória")
    private String confirmarNovaSenha;
}