package com.Nulances.dto.response;

import com.Nulances.domain.enums.StatusSolicitacaoVendedor;
import com.Nulances.domain.enums.TipoPessoaVendedor;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class SolicitacaoVendedorResponse {
    private UUID id;
    private TipoPessoaVendedor tipoPessoa;
    private StatusSolicitacaoVendedor status;
    private String cpf;
    private String cnpj;
    private String nomeCompleto;
    private String razaoSocial;
    private String email;
    private String telefone;
    private String cidade;
    private String estado;
    private String informacoesNegocio;
    private String observacaoAdmin;
    private Instant analisadoEm;
    private Instant createdAt;
    private List<DocumentoSolicitacaoVendedorResponse> documentos;
}