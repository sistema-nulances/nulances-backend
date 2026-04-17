package com.Nulances.helpers;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Component
public class BemMidiaHelper {

    private static final Set<String> CONTENT_TYPES_VALIDOS = Set.of(
            "image/jpeg",
            "image/jpg",
            "image/png",
            "image/webp",
            "video/mp4",
            "video/webm",
            "video/quicktime"
    );

    public void validarArquivo(String contentType, String nomeArquivo) {
        if (contentType == null || contentType.isBlank()) {
            throw new IllegalArgumentException("Content-Type é obrigatório");
        }

        if (!CONTENT_TYPES_VALIDOS.contains(contentType.toLowerCase())) {
            throw new IllegalArgumentException("Formato inválido. Envie apenas JPG, PNG, WEBP, MP4, WEBM ou MOV");
        }

        if (nomeArquivo == null || nomeArquivo.isBlank() || !nomeArquivo.contains(".")) {
            throw new IllegalArgumentException("Nome do arquivo inválido");
        }
    }

    public String extrairExtensao(String nomeArquivo) {
        return nomeArquivo.substring(nomeArquivo.lastIndexOf(".") + 1).toLowerCase();
    }

    public String montarObjectKey(UUID bemId, String tipo, String extensao) {
        return "bens/%s/%s-%d.%s"
                .formatted(bemId, tipo.toLowerCase(), Instant.now().toEpochMilli(), extensao);
    }
}