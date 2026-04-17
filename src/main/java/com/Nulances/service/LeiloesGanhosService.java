package com.Nulances.service;

import com.Nulances.config.security.CustomUserDetails;
import com.Nulances.domain.entity.Arrematacao;
import com.Nulances.domain.entity.Usuario;
import com.Nulances.dto.response.LeilaoGanhoItemResponse;
import com.Nulances.dto.response.LeiloesGanhosResponse;
import com.Nulances.mapper.LeiloesGanhosMapper;
import com.Nulances.repository.ArrematacaoRepository;
import com.Nulances.repository.UsuarioRepository;
import com.Nulances.storage.R2Service;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LeiloesGanhosService {

    private static final long DOWNLOAD_EXPIRES_SECONDS = 3600L;

    private final UsuarioRepository usuarioRepository;
    private final ArrematacaoRepository arrematacaoRepository;
    private final LeiloesGanhosMapper leiloesGanhosMapper;
    private final R2Service r2Service;

    @Value("${app.r2.bucket}")
    private String r2Bucket;

    @Transactional(readOnly = true)
    public LeiloesGanhosResponse listarMeusGanhos(Authentication authentication, Pageable pageable) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        Usuario usuario = usuarioRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new BadCredentialsException("Usuário não encontrado"));

        Page<Arrematacao> page = arrematacaoRepository.findByUsuarioId(usuario.getId(), pageable);

        List<LeilaoGanhoItemResponse> itens = page.getContent().stream()
                .map(leiloesGanhosMapper::toResponse)
                .map(this::assinarUrlsSeNecessario)
                .toList();

        return LeiloesGanhosResponse.builder()
                .itens(itens)
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build();
    }

    private LeilaoGanhoItemResponse assinarUrlsSeNecessario(LeilaoGanhoItemResponse item) {
        String capa = resolveR2Url(item.getMidiaCapaUrl());

        return item.toBuilder()
                .midiaCapaUrl(capa)
                .documentos(item.getDocumentos() == null ? List.of() : item.getDocumentos().stream()
                        .map(doc -> doc.toBuilder().url(resolveR2Url(doc.getUrl())).build())
                        .toList())
                .build();
    }

    private String resolveR2Url(String raw) {
        if (raw == null || raw.isBlank()) return null;
        String v = raw.trim();

        if (v.startsWith("http://") || v.startsWith("https://")) {
            return v;
        }

        return r2Service.gerarUrlDownload(r2Bucket, v, DOWNLOAD_EXPIRES_SECONDS);
    }
}