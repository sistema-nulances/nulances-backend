package com.Nulances.service;

import com.Nulances.storage.R2Properties;
import com.Nulances.config.security.CustomUserDetails;
import com.Nulances.domain.entity.DocumentoValidacao;
import com.Nulances.domain.entity.Usuario;
import com.Nulances.domain.enums.StatusDocumentoValidacao;
import com.Nulances.dto.request.AtualizarStatusDocumentoValidacaoRequest;
import com.Nulances.dto.request.ConfirmarUploadDocumentoValidacaoRequest;
import com.Nulances.dto.request.GerarUploadDocumentoValidacaoRequest;
import com.Nulances.dto.response.DocumentoValidacaoAdminResponse;
import com.Nulances.dto.response.DocumentoValidacaoResponse;
import com.Nulances.dto.response.UploadDocumentoValidacaoResponse;
import com.Nulances.repository.DocumentoValidacaoRepository;
import com.Nulances.repository.UsuarioRepository;
import com.Nulances.storage.R2Service;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DocumentoValidacaoService {

    private static final long URL_UPLOAD_EXPIRATION = 300L;
    private static final long URL_DOWNLOAD_EXPIRATION = 300L;

    private final DocumentoValidacaoRepository documentoValidacaoRepository;
    private final UsuarioRepository usuarioRepository;
    private final R2Service r2Service;
    private final R2Properties r2Properties;

    @Transactional(readOnly = true)
    public UploadDocumentoValidacaoResponse gerarUrlUpload(Authentication authentication,
                                                           GerarUploadDocumentoValidacaoRequest request) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        Usuario usuario = usuarioRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        validarArquivo(request.getContentType(), request.getNomeArquivo());

        String extensao = extrairExtensao(request.getNomeArquivo());
        String objectKey = montarObjectKey(usuario.getId(), request.getTipo().name(), extensao);

        String uploadUrl = r2Service.gerarUrlUpload(
                r2Properties.bucket(),
                objectKey,
                request.getContentType(),
                URL_UPLOAD_EXPIRATION
        );

        UploadDocumentoValidacaoResponse response = new UploadDocumentoValidacaoResponse();
        response.setObjectKey(objectKey);
        response.setUploadUrl(uploadUrl);
        response.setExpiresInSeconds(URL_UPLOAD_EXPIRATION);
        return response;
    }

    @Transactional
    public DocumentoValidacaoResponse confirmarUpload(Authentication authentication,
                                                      ConfirmarUploadDocumentoValidacaoRequest request) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        Usuario usuario = usuarioRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        DocumentoValidacao documento = documentoValidacaoRepository
                .findByUsuarioIdAndTipo(usuario.getId(), request.getTipo())
                .orElseGet(DocumentoValidacao::new);

        documento.setUsuario(usuario);
        documento.setTipo(request.getTipo());
        documento.setArquivo(request.getObjectKey());
        documento.setStatus(StatusDocumentoValidacao.PENDENTE);

        DocumentoValidacao salvo = documentoValidacaoRepository.save(documento);

        return toResponse(salvo);
    }

    @Transactional(readOnly = true)
    public List<DocumentoValidacaoResponse> listarMeusDocumentos(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        Usuario usuario = usuarioRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        return documentoValidacaoRepository.findByUsuarioId(usuario.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<DocumentoValidacaoAdminResponse> listarPorStatus(StatusDocumentoValidacao status) {
        return documentoValidacaoRepository.findByStatus(status)
                .stream()
                .map(this::toAdminResponse)
                .toList();
    }

    @Transactional
    public DocumentoValidacaoAdminResponse atualizarStatus(UUID documentoId,
                                                           AtualizarStatusDocumentoValidacaoRequest request) {
        DocumentoValidacao documento = documentoValidacaoRepository.findById(documentoId)
                .orElseThrow(() -> new IllegalArgumentException("Documento não encontrado"));

        if (request.getStatus() == null) {
            throw new IllegalArgumentException("Status é obrigatório");
        }

        if (request.getStatus() == StatusDocumentoValidacao.PENDENTE) {
            throw new IllegalArgumentException("Não é permitido voltar documento para PENDENTE");
        }

        documento.setStatus(request.getStatus());

        DocumentoValidacao salvo = documentoValidacaoRepository.save(documento);
        return toAdminResponse(salvo);
    }

    private DocumentoValidacaoResponse toResponse(DocumentoValidacao documento) {
        DocumentoValidacaoResponse response = new DocumentoValidacaoResponse();
        response.setId(documento.getId());
        response.setTipo(documento.getTipo());
        response.setArquivo(documento.getArquivo());
        response.setArquivoUrl(r2Service.gerarUrlDownload(
                r2Properties.bucket(),
                documento.getArquivo(),
                URL_DOWNLOAD_EXPIRATION
        ));
        response.setStatus(documento.getStatus());
        response.setCreatedAt(documento.getCreatedAt());
        response.setUpdatedAt(documento.getUpdatedAt());
        return response;
    }

    private DocumentoValidacaoAdminResponse toAdminResponse(DocumentoValidacao documento) {
        DocumentoValidacaoAdminResponse response = new DocumentoValidacaoAdminResponse();
        response.setId(documento.getId());
        response.setUsuarioId(documento.getUsuario().getId());
        response.setNomeCompleto(documento.getUsuario().getNomeCompleto());
        response.setEmail(documento.getUsuario().getEmail());
        response.setCpf(documento.getUsuario().getCpf());
        response.setTipo(documento.getTipo());
        response.setArquivo(documento.getArquivo());
        response.setArquivoUrl(r2Service.gerarUrlDownload(
                r2Properties.bucket(),
                documento.getArquivo(),
                URL_DOWNLOAD_EXPIRATION
        ));
        response.setStatus(documento.getStatus());
        response.setCreatedAt(documento.getCreatedAt());
        response.setUpdatedAt(documento.getUpdatedAt());
        return response;
    }

    private void validarArquivo(String contentType, String nomeArquivo) {
        if (contentType == null || contentType.isBlank()) {
            throw new IllegalArgumentException("Content-Type é obrigatório");
        }

        boolean contentTypeValido = contentType.equalsIgnoreCase("image/jpeg")
                || contentType.equalsIgnoreCase("image/jpg")
                || contentType.equalsIgnoreCase("image/png")
                || contentType.equalsIgnoreCase("image/webp");

        if (!contentTypeValido) {
            throw new IllegalArgumentException("Formato inválido. Envie apenas imagem JPG, PNG ou WEBP");
        }

        if (nomeArquivo == null || !nomeArquivo.contains(".")) {
            throw new IllegalArgumentException("Nome do arquivo inválido");
        }
    }

    private String extrairExtensao(String nomeArquivo) {
        return nomeArquivo.substring(nomeArquivo.lastIndexOf(".") + 1).toLowerCase();
    }

    private String montarObjectKey(UUID usuarioId, String tipo, String extensao) {
        return "documentos-validacao/%s/%s-%d.%s"
                .formatted(usuarioId, tipo.toLowerCase(), Instant.now().toEpochMilli(), extensao);
    }
}