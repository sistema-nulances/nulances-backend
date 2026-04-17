package com.Nulances.dto.response;

import com.Nulances.domain.enums.TipoPessoaVendedor;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class AdminMarketplaceSolicitacaoPendenteDetalheResponse {

    private UUID solicitacaoId;
    private UUID usuarioId;

    private TipoPessoaVendedor tipoPessoa;

    private String nomeExibicao;
    private String cpfOuCnpj;
    private String email;
    private String telefone;

    private String cidade;
    private String estado;
    private String endereco;

    private String informacoesNegocio;

    private String fotoPerfil;
    private String fotoPerfilUrl;

    private Instant createdAt;

    private List<AdminMarketplaceDocumentoPendenteResponse> documentos;
}