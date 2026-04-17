package com.Nulances.dto.request;

import com.Nulances.domain.enums.TipoPessoaVendedor;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SolicitarAcessoVendedorRequest {

    @NotNull(message = "Tipo de pessoa é obrigatório.")
    private TipoPessoaVendedor tipoPessoa;

    private String cpf;
    private String cnpj;

    private String nomeCompleto;
    private String razaoSocial;

    @NotBlank(message = "E-mail é obrigatório.")
    @Email(message = "E-mail inválido.")
    private String email;

    @NotBlank(message = "Telefone é obrigatório.")
    private String telefone;

    @NotBlank(message = "Cidade é obrigatória.")
    private String cidade;

    @NotBlank(message = "Estado é obrigatório.")
    private String estado;

    @NotBlank(message = "Informações sobre o negócio são obrigatórias.")
    private String informacoesNegocio;

    // PF
    private String rgFrenteKey;
    private String rgVersoKey;
    private String cpfFrenteKey;
    private String cpfVersoKey;

    // PJ
    private String selfieComDocumentoKey;
    private String contratoSocialKey;
}