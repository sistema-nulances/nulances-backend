package com.Nulances.dto.response;

import com.Nulances.domain.enums.TipoPessoaVendedor;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class AdminMarketplaceSolicitacaoPendenteProjection {
    private UUID solicitacaoId;
    private UUID usuarioId;
    private TipoPessoaVendedor tipoPessoa;
    private String nomeExibicao;
    private String cpf;
    private String cnpj;
    private String email;
    private String telefone;
    private String cidade;
    private String estado;
    private String informacoesNegocio;
    private String fotoPerfil;
    private Instant createdAt;
}