package com.Nulances.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class AdminMarketplaceVendedorListItemResponse {

    private UUID id;
    private UUID usuarioId;
    private String nomeExibicao;
    private String cpfOuCnpj;
    private String email;
    private String telefone;
    private String cidade;
    private String estado;
    private String endereco;

    private String fotoPerfil;
    private String fotoPerfilUrl;

    private String tipoRegistro;
    private String statusConta;

    private Instant dataSolicitacao;
    private Instant dataAprovacao;

    private Long totalAnuncios;
    private Long totalPublicados;
}