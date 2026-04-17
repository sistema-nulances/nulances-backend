package com.Nulances.mapper;

import com.Nulances.domain.entity.DocumentoSolicitacaoVendedor;
import com.Nulances.domain.entity.SolicitacaoVendedor;
import com.Nulances.domain.enums.TipoPessoaVendedor;
import com.Nulances.dto.response.*;
import com.Nulances.storage.R2Properties;
import com.Nulances.storage.R2Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SolicitacaoVendedorMapper {

    private final R2Service r2Service;
    private final R2Properties r2Properties;

    public SolicitacaoVendedorResponse toResponse(SolicitacaoVendedor solicitacao) {

        List<DocumentoSolicitacaoVendedorResponse> documentos = solicitacao.getDocumentos().stream()
                .sorted(Comparator.comparing(
                        DocumentoSolicitacaoVendedor::getCreatedAt,
                        Comparator.nullsLast(Comparator.naturalOrder())
                ))
                .map(this::toDocumentoResponse)
                .toList();

        return new SolicitacaoVendedorResponse(
                solicitacao.getId(),
                solicitacao.getTipoPessoa(),
                solicitacao.getStatus(),
                solicitacao.getCpf(),
                solicitacao.getCnpj(),
                solicitacao.getNomeCompleto(),
                solicitacao.getRazaoSocial(),
                solicitacao.getEmail(),
                solicitacao.getTelefone(),
                solicitacao.getCidade(),
                solicitacao.getEstado(),
                solicitacao.getInformacoesNegocio(),
                solicitacao.getObservacaoAdmin(),
                solicitacao.getAnalisadoEm(),
                solicitacao.getCreatedAt(),
                documentos
        );
    }

    public SolicitacaoVendedorListResponse toListResponse(SolicitacaoVendedor solicitacao) {
        String nome = solicitacao.getTipoPessoa() == TipoPessoaVendedor.PESSOA_FISICA
                ? solicitacao.getNomeCompleto()
                : solicitacao.getRazaoSocial();

        return new SolicitacaoVendedorListResponse(
                solicitacao.getId(),
                nome,
                solicitacao.getTipoPessoa(),
                solicitacao.getStatus(),
                solicitacao.getEmail(),
                solicitacao.getTelefone(),
                solicitacao.getCidade(),
                solicitacao.getEstado(),
                solicitacao.getCreatedAt()
        );
    }

    private DocumentoSolicitacaoVendedorResponse toDocumentoResponse(DocumentoSolicitacaoVendedor doc) {
        String url = r2Service.gerarUrlDownload(
                r2Properties.bucket(),
                doc.getArquivo(),
                r2Properties.downloadExpiresInSeconds()
        );

        return new DocumentoSolicitacaoVendedorResponse(
                doc.getId(),
                doc.getTipo(),
                doc.getArquivo(),
                url
        );
    }
}