package com.Nulances.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class AdminMarketplaceVendedorAtivoProjection {
    private UUID usuarioId;
    private String nomeCompleto;
    private String email;
    private String telefone;
    private String cidade;
    private String estado;
    private String fotoPerfil;
    private Instant createdAt;
    private Instant dataAprovacao;
    private Long totalAnuncios;
    private Long totalPublicados;
}