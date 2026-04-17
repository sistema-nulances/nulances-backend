package com.Nulances.mapper;

import com.Nulances.domain.entity.DocumentoValidacao;
import com.Nulances.domain.entity.DocumentoVendedor;
import com.Nulances.domain.entity.Usuario;
import com.Nulances.dto.response.DocumentoValidacaoResponse;
import com.Nulances.dto.response.DocumentoVendedorResponse;
import com.Nulances.dto.response.MeResponse;
import com.Nulances.dto.response.RegisterResponse;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class AuthMapper {

    public RegisterResponse toRegisterResponse(Usuario usuario, String mensagem) {
        return new RegisterResponse(
                usuario.getId(),
                usuario.getNomeCompleto(),
                usuario.getCpf(),
                usuario.getDataNascimento(),
                usuario.getEmail(),
                usuario.getRole(),
                usuario.getEmailVerificado(),
                mensagem
        );
    }

    public MeResponse toMeResponse(
            Usuario usuario,
            List<DocumentoValidacao> documentosValidacao,
            List<DocumentoVendedor> documentosVendedor
    ) {
        return new MeResponse(
                usuario.getId(),
                usuario.getNomeCompleto(),
                usuario.getEmail(),
                usuario.getTelefone(),
                usuario.getFotoPerfil(),
                usuario.getCpf(),
                usuario.getCep(),
                usuario.getLogradouro(),
                usuario.getCidade(),
                usuario.getEstado(),
                usuario.getEmailVerificado(),
                usuario.getEmailVerificadoEm(),
                usuario.getRole(),
                usuario.getCreatedAt(),
                usuario.getUpdatedAt(),
                mapDocumentosValidacao(documentosValidacao),
                mapDocumentosVendedor(documentosVendedor)
        );
    }

    public List<DocumentoValidacaoResponse> mapDocumentosValidacao(List<DocumentoValidacao> documentos) {
        if (documentos == null || documentos.isEmpty()) {
            return Collections.emptyList();
        }

        return documentos.stream()
                .map(this::toDocumentoValidacaoResponse)
                .toList();
    }

    public List<DocumentoVendedorResponse> mapDocumentosVendedor(List<DocumentoVendedor> documentos) {
        if (documentos == null || documentos.isEmpty()) {
            return Collections.emptyList();
        }

        return documentos.stream()
                .map(this::toDocumentoVendedorResponse)
                .toList();
    }

    public DocumentoValidacaoResponse toDocumentoValidacaoResponse(DocumentoValidacao doc) {
        DocumentoValidacaoResponse response = new DocumentoValidacaoResponse();
        response.setId(doc.getId());
        response.setTipo(doc.getTipo());
        response.setArquivo(doc.getArquivo());
        response.setArquivoUrl(null); // depois trocar pela URL assinada
        response.setStatus(doc.getStatus());
        response.setCreatedAt(doc.getCreatedAt());
        response.setUpdatedAt(doc.getUpdatedAt());
        return response;
    }

    public DocumentoVendedorResponse toDocumentoVendedorResponse(DocumentoVendedor doc) {
        return new DocumentoVendedorResponse(
                doc.getId(),
                doc.getTipo(),
                doc.getArquivo(),
                doc.getCreatedAt(),
                doc.getUpdatedAt()
        );
    }
}