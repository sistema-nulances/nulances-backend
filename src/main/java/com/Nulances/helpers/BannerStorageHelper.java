package com.Nulances.helpers;

import java.text.Normalizer;
import java.time.Instant;
import java.util.UUID;

public final class BannerStorageHelper {

    private BannerStorageHelper() {
    }

    public static String gerarObjectKey(String fileName) {
        String nomeNormalizado = normalizarNomeArquivo(fileName);
        return "banners/" + Instant.now().toEpochMilli() + "-" + UUID.randomUUID() + "-" + nomeNormalizado;
    }

    private static String normalizarNomeArquivo(String fileName) {
        if (fileName == null || fileName.isBlank()) {
            return "arquivo";
        }

        String normalizado = Normalizer.normalize(fileName.trim(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .replaceAll("[^a-zA-Z0-9._-]", "-")
                .replaceAll("-{2,}", "-")
                .toLowerCase();

        return normalizado.isBlank() ? "arquivo" : normalizado;
    }
}