package com.Nulances.controller;

import com.Nulances.domain.enums.TipoBanner;
import com.Nulances.dto.response.BannerPublicResponse;
import com.Nulances.service.BannerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.Base64;
import java.util.List;

@RestController
@RequestMapping("/banners")
@RequiredArgsConstructor
public class BannerController {

    private static final long BROWSER_MAX_AGE_SECONDS = 60L;
    private static final long CDN_S_MAX_AGE_SECONDS = 300L;
    private static final long STALE_WHILE_REVALIDATE_SECONDS = 60L;

    private final BannerService bannerService;

    @GetMapping
    public ResponseEntity<List<BannerPublicResponse>> listarPorTipo(
            @RequestParam TipoBanner tipo,
            @RequestHeader(value = HttpHeaders.IF_NONE_MATCH, required = false) String ifNoneMatch
    ) {
        List<BannerPublicResponse> payload = bannerService.listarPublicoPorTipo(tipo);
        String etag = gerarEtag(payload);

        if (ifNoneMatchContem(ifNoneMatch, etag)) {
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED)
                    .eTag(etag)
                    .cacheControl(CacheControl.maxAge(Duration.ofSeconds(BROWSER_MAX_AGE_SECONDS)).cachePublic())
                    .header("CDN-Cache-Control",
                            "public, s-maxage=" + CDN_S_MAX_AGE_SECONDS +
                                    ", stale-while-revalidate=" + STALE_WHILE_REVALIDATE_SECONDS)
                    .build();
        }

        return ResponseEntity.ok()
                .eTag(etag)
                .cacheControl(CacheControl.maxAge(Duration.ofSeconds(BROWSER_MAX_AGE_SECONDS)).cachePublic())
                .header("CDN-Cache-Control",
                        "public, s-maxage=" + CDN_S_MAX_AGE_SECONDS +
                                ", stale-while-revalidate=" + STALE_WHILE_REVALIDATE_SECONDS)
                .body(payload);
    }

    private boolean ifNoneMatchContem(String ifNoneMatch, String etag) {
        if (ifNoneMatch == null || ifNoneMatch.isBlank()) return false;
        String[] tags = ifNoneMatch.split(",");
        for (String raw : tags) {
            String t = raw.trim();
            if ("*".equals(t) || etag.equals(t)) return true;
        }
        return false;
    }

    private String gerarEtag(List<BannerPublicResponse> payload) {
        try {
            StringBuilder sb = new StringBuilder();
            for (BannerPublicResponse b : payload) {
                sb.append(b.getTipo()).append('|')
                        .append(b.getPosicao()).append('|')
                        .append(b.getImagem()).append('|')
                        .append(b.getTextoAlternativo()).append(';');
            }

            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(sb.toString().getBytes(StandardCharsets.UTF_8));
            String base64 = Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
            return "\"" + base64 + "\"";
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("Falha ao gerar ETag de banners.", ex);
        }
    }
}