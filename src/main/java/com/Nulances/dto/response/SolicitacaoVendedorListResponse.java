package com.Nulances.dto.response;

import com.Nulances.domain.enums.StatusSolicitacaoVendedor;
import com.Nulances.domain.enums.TipoPessoaVendedor;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class SolicitacaoVendedorListResponse {
    private UUID id;
    private String nomeExibicao;
    private TipoPessoaVendedor tipoPessoa;
    private StatusSolicitacaoVendedor status;
    private String email;
    private String telefone;
    private String cidade;
    private String estado;
    private Instant createdAt;
}