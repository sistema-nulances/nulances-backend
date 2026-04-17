package com.Nulances.service;

import com.Nulances.config.security.CustomUserDetails;
import com.Nulances.domain.entity.Usuario;
import com.Nulances.domain.enums.UserRole;
import com.Nulances.dto.request.GerarUploadMidiaAnuncioRequest;
import com.Nulances.dto.response.UploadMidiaAnuncioResponse;
import com.Nulances.repository.UsuarioRepository;
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
public class AnuncioMidiaUploadService {

    private final UsuarioRepository usuarioRepository;
    private final R2Service r2Service;
    private final R2Properties r2Properties;

    @PreAuthorize("hasAnyRole('VENDEDOR','ADMIN')")
    @Transactional(readOnly = true)
    public UploadMidiaAnuncioResponse gerarUploadUrl(
            GerarUploadMidiaAnuncioRequest request,
            CustomUserDetails userDetails
    ) {
        Usuario usuario = buscarUsuarioAutenticado(userDetails);
        validarPermissao(usuario);
        validarRequest(request);
        validarContentType(request.getContentType());

        String extensao = extrairExtensao(request.getNomeArquivo());
        String objectKey = "marketplace/anuncios/%s/%d-%s-%s.%s"
                .formatted(
                        usuario.getId(),
                        Instant.now().toEpochMilli(),
                        slug(request.getTipo().name()),
                        UUID.randomUUID(),
                        extensao
                );

        String uploadUrl = r2Service.gerarUrlUpload(
                r2Properties.bucket(),
                objectKey,
                request.getContentType(),
                r2Properties.uploadExpiresInSeconds()
        );

        String fileUrl = r2Service.gerarUrlDownload(
                r2Properties.bucket(),
                objectKey,
                r2Properties.downloadExpiresInSeconds()
        );

        return new UploadMidiaAnuncioResponse(
                uploadUrl,
                objectKey,
                fileUrl,
                r2Properties.uploadExpiresInSeconds()
        );
    }

    private Usuario buscarUsuarioAutenticado(CustomUserDetails userDetails) {
        return usuarioRepository.findById(userDetails.getId())
                .orElseThrow(() -> new IllegalArgumentException("Usuário autenticado não encontrado."));
    }

    private void validarPermissao(Usuario usuario) {
        if (usuario.getRole() != UserRole.VENDEDOR && usuario.getRole() != UserRole.ADMIN) {
            throw new IllegalArgumentException("Somente vendedores ou administradores podem enviar mídias de anúncio.");
        }
    }

    private void validarRequest(GerarUploadMidiaAnuncioRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Requisição inválida.");
        }

        if (request.getTipo() == null) {
            throw new IllegalArgumentException("Tipo da mídia é obrigatório.");
        }

        if (request.getNomeArquivo() == null || request.getNomeArquivo().isBlank()) {
            throw new IllegalArgumentException("Nome do arquivo é obrigatório.");
        }
    }

    private void validarContentType(String contentType) {
        if (contentType == null || contentType.isBlank()) {
            throw new IllegalArgumentException("Content-Type é obrigatório.");
        }

        boolean permitido =
                contentType.equalsIgnoreCase("image/jpeg") ||
                        contentType.equalsIgnoreCase("image/jpg") ||
                        contentType.equalsIgnoreCase("image/png") ||
                        contentType.equalsIgnoreCase("image/webp") ||
                        contentType.equalsIgnoreCase("video/mp4") ||
                        contentType.equalsIgnoreCase("video/webm");

        if (!permitido) {
            throw new IllegalArgumentException("Tipo de mídia não permitido.");
        }
    }

    private String extrairExtensao(String nomeArquivo) {
        if (nomeArquivo == null || nomeArquivo.isBlank() || !nomeArquivo.contains(".")) {
            return "bin";
        }

        String ext = nomeArquivo.substring(nomeArquivo.lastIndexOf('.') + 1).toLowerCase(Locale.ROOT);
        return ext.isBlank() ? "bin" : ext;
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