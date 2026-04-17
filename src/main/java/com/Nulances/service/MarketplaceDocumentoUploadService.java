package com.Nulances.service;

import com.Nulances.domain.entity.Usuario;
import com.Nulances.domain.enums.UserRole;
import com.Nulances.dto.request.GerarUploadDocumentoVendedorRequest;
import com.Nulances.dto.response.UploadDocumentoVendedorResponse;
import com.Nulances.storage.R2Properties;
import com.Nulances.storage.R2Service;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.time.Instant;
import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MarketplaceDocumentoUploadService {

    private final R2Service r2Service;
    private final R2Properties r2Properties;

    @PreAuthorize("hasRole('COMUM')")
    @Transactional(readOnly = true)
    public UploadDocumentoVendedorResponse gerarUpload(Usuario usuario, GerarUploadDocumentoVendedorRequest request) {
        if (usuario.getRole() != UserRole.COMUM) {
            throw new IllegalArgumentException("Somente usuários com perfil COMUM podem enviar documentos para solicitação de vendedor.");
        }

        validarContentType(request.getContentType());

        String extensao = extrairExtensao(request.getNomeArquivo());
        String nomeSeguro = slug(request.getTipoDocumento().name().toLowerCase(Locale.ROOT));

        String objectKey = "marketplace/solicitacoes-vendedor/%s/%d-%s-%s.%s"
                .formatted(
                        usuario.getId(),
                        Instant.now().toEpochMilli(),
                        nomeSeguro,
                        UUID.randomUUID(),
                        extensao
                );

        String uploadUrl = r2Service.gerarUrlUpload(
                r2Properties.bucket(),
                objectKey,
                request.getContentType(),
                r2Properties.uploadExpiresInSeconds()
        );

        String fileUrl = montarFileUrl(objectKey);

        return new UploadDocumentoVendedorResponse(
                uploadUrl,
                objectKey,
                fileUrl,
                r2Properties.uploadExpiresInSeconds()
        );
    }

    private void validarContentType(String contentType) {
        if (contentType == null || contentType.isBlank()) {
            throw new IllegalArgumentException("Content-Type é obrigatório.");
        }

        boolean permitido = contentType.equalsIgnoreCase("image/jpeg")
                || contentType.equalsIgnoreCase("image/jpg")
                || contentType.equalsIgnoreCase("image/png")
                || contentType.equalsIgnoreCase("image/webp")
                || contentType.equalsIgnoreCase("application/pdf");

        if (!permitido) {
            throw new IllegalArgumentException("Tipo de arquivo não permitido.");
        }
    }

    private String extrairExtensao(String nomeArquivo) {
        if (nomeArquivo == null || !nomeArquivo.contains(".")) {
            return "bin";
        }

        String ext = nomeArquivo.substring(nomeArquivo.lastIndexOf('.') + 1).toLowerCase(Locale.ROOT);
        return ext.isBlank() ? "bin" : ext;
    }

    private String montarFileUrl(String objectKey) {
        if (r2Properties.publicBaseUrl() == null || r2Properties.publicBaseUrl().isBlank()) {
            return objectKey;
        }

        String base = r2Properties.publicBaseUrl().endsWith("/")
                ? r2Properties.publicBaseUrl().substring(0, r2Properties.publicBaseUrl().length() - 1)
                : r2Properties.publicBaseUrl();

        return base + "/" + objectKey;
    }

    private String slug(String texto) {
        String normalizado = Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");

        return normalizado
                .replaceAll("[^a-zA-Z0-9]+", "-")
                .replaceAll("^-|-$", "")
                .toLowerCase(Locale.ROOT);
    }
}