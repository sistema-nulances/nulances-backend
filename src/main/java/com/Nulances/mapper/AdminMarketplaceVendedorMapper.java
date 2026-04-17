package com.Nulances.mapper;

import com.Nulances.domain.entity.DocumentoSolicitacaoVendedor;
import com.Nulances.domain.entity.SolicitacaoVendedor;
import com.Nulances.dto.response.*;
import com.Nulances.storage.R2Properties;
import com.Nulances.storage.R2Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AdminMarketplaceVendedorMapper {

    private final R2Service r2Service;
    private final R2Properties r2Properties;

    public AdminMarketplaceVendedorListItemResponse toAtivoResponse(
            AdminMarketplaceVendedorAtivoProjection p
    ) {
        return new AdminMarketplaceVendedorListItemResponse(
                p.getUsuarioId(),
                p.getUsuarioId(),
                p.getNomeCompleto(),
                null,
                p.getEmail(),
                p.getTelefone(),
                p.getCidade(),
                p.getEstado(),
                montarEndereco(p.getCidade(), p.getEstado()),
                p.getFotoPerfil(),
                gerarUrlAssinada(p.getFotoPerfil()),
                "VENDEDOR",
                "ATIVO",
                null,
                p.getDataAprovacao() != null ? p.getDataAprovacao() : p.getCreatedAt(),
                p.getTotalAnuncios(),
                p.getTotalPublicados()
        );
    }

    public AdminMarketplaceVendedorListItemResponse toPendenteResponse(
            AdminMarketplaceSolicitacaoPendenteProjection p
    ) {
        return new AdminMarketplaceVendedorListItemResponse(
                p.getSolicitacaoId(),
                p.getUsuarioId(),
                p.getNomeExibicao(),
                escolherCpfOuCnpj(p.getCpf(), p.getCnpj()),
                p.getEmail(),
                p.getTelefone(),
                p.getCidade(),
                p.getEstado(),
                montarEndereco(p.getCidade(), p.getEstado()),
                p.getFotoPerfil(),
                gerarUrlAssinada(p.getFotoPerfil()),
                "SOLICITACAO",
                "PENDENTE",
                p.getCreatedAt(),
                null,
                0L,
                0L
        );
    }

    public AdminMarketplaceSolicitacaoPendenteDetalheResponse toDetalhePendenteResponse(
            SolicitacaoVendedor solicitacao
    ) {
        List<AdminMarketplaceDocumentoPendenteResponse> documentos = solicitacao.getDocumentos().stream()
                .sorted(Comparator.comparing(
                        DocumentoSolicitacaoVendedor::getCreatedAt,
                        Comparator.nullsLast(Comparator.naturalOrder())
                ))
                .map(this::toDocumentoResponse)
                .toList();

        return new AdminMarketplaceSolicitacaoPendenteDetalheResponse(
                solicitacao.getId(),
                solicitacao.getUsuario().getId(),
                solicitacao.getTipoPessoa(),
                nomeExibicao(solicitacao),
                escolherCpfOuCnpj(solicitacao.getCpf(), solicitacao.getCnpj()),
                solicitacao.getEmail(),
                solicitacao.getTelefone(),
                solicitacao.getCidade(),
                solicitacao.getEstado(),
                montarEndereco(solicitacao.getCidade(), solicitacao.getEstado()),
                solicitacao.getInformacoesNegocio(),
                solicitacao.getUsuario().getFotoPerfil(),
                gerarUrlAssinada(solicitacao.getUsuario().getFotoPerfil()),
                solicitacao.getCreatedAt(),
                documentos
        );
    }

    private AdminMarketplaceDocumentoPendenteResponse toDocumentoResponse(DocumentoSolicitacaoVendedor doc) {
        return new AdminMarketplaceDocumentoPendenteResponse(
                doc.getTipo(),
                doc.getArquivo(),
                gerarUrlAssinada(doc.getArquivo())
        );
    }

    private String gerarUrlAssinada(String objectKey) {
        if (objectKey == null || objectKey.isBlank()) {
            return null;
        }

        return r2Service.gerarUrlDownload(
                r2Properties.bucket(),
                objectKey,
                r2Properties.downloadExpiresInSeconds()
        );
    }

    private String escolherCpfOuCnpj(String cpf, String cnpj) {
        if (cpf != null && !cpf.isBlank()) {
            return cpf;
        }
        if (cnpj != null && !cnpj.isBlank()) {
            return cnpj;
        }
        return null;
    }

    private String montarEndereco(String cidade, String estado) {
        if ((cidade == null || cidade.isBlank()) && (estado == null || estado.isBlank())) {
            return null;
        }
        if (cidade == null || cidade.isBlank()) {
            return estado;
        }
        if (estado == null || estado.isBlank()) {
            return cidade;
        }
        return cidade + " - " + estado;
    }

    private String nomeExibicao(SolicitacaoVendedor solicitacao) {
        return switch (solicitacao.getTipoPessoa()) {
            case PESSOA_FISICA -> solicitacao.getNomeCompleto();
            case PESSOA_JURIDICA -> solicitacao.getRazaoSocial();
        };
    }
}