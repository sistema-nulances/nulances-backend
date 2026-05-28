package com.Nulances.service;

import com.Nulances.domain.entity.Banner;
import com.Nulances.domain.enums.TipoBanner;
import com.Nulances.dto.request.BannerCreateRequest;
import com.Nulances.dto.request.BannerUpdateRequest;
import com.Nulances.dto.request.BannerUploadRequest;
import com.Nulances.dto.response.BannerAdminResponse;
import com.Nulances.dto.response.BannerPublicResponse;
import com.Nulances.dto.response.BannerUploadResponse;
import com.Nulances.helpers.BannerStorageHelper;
import com.Nulances.mapper.BannerMapper;
import com.Nulances.repository.BannerRepository;
import com.Nulances.storage.R2Properties;
import com.Nulances.storage.R2Service;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BannerService {

    private static final long UPLOAD_EXPIRES_IN_SECONDS = 300L;

    // Subimos validade para reduzir custo por requisição pública.
    // Ajuste conforme sua política de segurança de URL assinada.
    private static final long DOWNLOAD_EXPIRES_IN_SECONDS = 3600L;

    // Nome de cache lógico para leitura pública por tipo.
    private static final String CACHE_BANNERS_PUBLICOS_POR_TIPO = "bannersPublicosPorTipo";

    private final BannerRepository bannerRepository;
    private final BannerMapper bannerMapper;
    private final R2Service r2Service;
    private final R2Properties r2Properties;

    @PreAuthorize("hasRole('ADMIN')")
    public BannerUploadResponse gerarUploadUrl(BannerUploadRequest request) {
        validarContentType(request.getContentType());

        String objectKey = BannerStorageHelper.gerarObjectKey(request.getFileName());

        String uploadUrl = r2Service.gerarUrlUpload(
                r2Properties.bucket(),
                objectKey,
                request.getContentType(),
                UPLOAD_EXPIRES_IN_SECONDS
        );

        return new BannerUploadResponse(uploadUrl, objectKey);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    @CacheEvict(cacheNames = CACHE_BANNERS_PUBLICOS_POR_TIPO, allEntries = true)
    public BannerAdminResponse criar(BannerCreateRequest request) {
        validarPosicaoDuplicada(request.getTipo(), request.getPosicao(), null);

        Banner banner = bannerMapper.toEntity(request);
        Banner salvo = bannerRepository.save(banner);

        return bannerMapper.toAdminResponse(salvo, gerarArquivoUrl(salvo.getImagem()));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(readOnly = true)
    public List<BannerAdminResponse> listarAdmin() {
        return bannerRepository.findAllByOrderByTipoAscPosicaoAscCreatedAtDesc().stream()
                .map(banner -> bannerMapper.toAdminResponse(banner, gerarArquivoUrl(banner.getImagem())))
                .toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(readOnly = true)
    public BannerAdminResponse buscarPorIdAdmin(UUID id) {
        Banner banner = buscarEntidade(id);
        return bannerMapper.toAdminResponse(banner, gerarArquivoUrl(banner.getImagem()));
    }

    @Transactional(readOnly = true)
    @Cacheable(
            cacheNames = CACHE_BANNERS_PUBLICOS_POR_TIPO,
            key = "#tipo.name()",
            unless = "#result == null",
            sync = true
    )
    public List<BannerPublicResponse> listarPublicoPorTipo(TipoBanner tipo) {
        return bannerRepository.findByAtivoTrueAndTipoOrderByPosicaoAscCreatedAtDesc(tipo).stream()
                .map(bannerMapper::toPublicResponse)
                .map(this::assinarImagemBannerPublico)
                .toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    @CacheEvict(cacheNames = CACHE_BANNERS_PUBLICOS_POR_TIPO, allEntries = true)
    public BannerAdminResponse editar(UUID id, BannerUpdateRequest request) {
        Banner banner = buscarEntidade(id);

        TipoBanner tipoFinal = request.getTipo() != null ? request.getTipo() : banner.getTipo();
        Integer posicaoFinal = request.getPosicao() != null ? request.getPosicao() : banner.getPosicao();

        validarPosicaoDuplicada(tipoFinal, posicaoFinal, id);

        if (request.getTipo() != null) {
            banner.setTipo(request.getTipo());
        }

        if (request.getPosicao() != null) {
            banner.setPosicao(request.getPosicao());
        }

        if (request.getTextoAlternativo() != null) {
            banner.setTextoAlternativo(normalizarOpcional(request.getTextoAlternativo()));
        }

        if (request.getImagem() != null) {
            banner.setImagem(normalizarObrigatorio(request.getImagem(), "A key da imagem é obrigatória."));
        }

        if (request.getAtivo() != null) {
            banner.setAtivo(request.getAtivo());
        }

        if (request.getObjectPosition() != null) {
            banner.setObjectPosition(normalizarOpcional(request.getObjectPosition()));
        }

        Banner atualizado = bannerRepository.save(banner);

        return bannerMapper.toAdminResponse(atualizado, gerarArquivoUrl(atualizado.getImagem()));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    @CacheEvict(cacheNames = CACHE_BANNERS_PUBLICOS_POR_TIPO, allEntries = true)
    public void excluir(UUID id) {
        Banner banner = buscarEntidade(id);
        bannerRepository.delete(banner);
    }

    private Banner buscarEntidade(UUID id) {
        return bannerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Banner não encontrado."));
    }

    private void validarPosicaoDuplicada(TipoBanner tipo, Integer posicao, UUID idIgnorado) {
        if (tipo == null) {
            throw new IllegalArgumentException("Tipo é obrigatório.");
        }

        if (posicao == null || posicao < 1) {
            throw new IllegalArgumentException("Posição deve ser maior que zero.");
        }

        boolean existe = idIgnorado == null
                ? bannerRepository.existsByTipoAndPosicao(tipo, posicao)
                : bannerRepository.existsByTipoAndPosicaoAndIdNot(tipo, posicao, idIgnorado);

        if (existe) {
            throw new IllegalArgumentException("Já existe um banner nesta posição para este tipo.");
        }
    }

    private String gerarArquivoUrl(String objectKey) {
        return r2Service.gerarUrlDownload(
                r2Properties.bucket(),
                objectKey,
                DOWNLOAD_EXPIRES_IN_SECONDS
        );
    }

    private BannerPublicResponse assinarImagemBannerPublico(BannerPublicResponse response) {
        if (response == null) {
            return null;
        }

        String objectKey = response.getImagem();
        if (objectKey == null || objectKey.isBlank()) {
            return response;
        }

        String signedUrl = r2Service.gerarUrlDownload(
                r2Properties.bucket(),
                objectKey.trim(),
                DOWNLOAD_EXPIRES_IN_SECONDS
        );

        response.setImagem(signedUrl);
        return response;
    }

    private void validarContentType(String contentType) {
        if (contentType == null || contentType.isBlank()) {
            throw new IllegalArgumentException("Content-Type é obrigatório.");
        }

        String normalizado = contentType.trim().toLowerCase();

        if (!List.of("image/png", "image/jpeg", "image/jpg", "image/webp").contains(normalizado)) {
            throw new IllegalArgumentException("Tipo de arquivo não permitido para banner.");
        }
    }

    private String normalizarObrigatorio(String valor, String mensagemErro) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(mensagemErro);
        }
        return valor.trim();
    }

    private String normalizarOpcional(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        return valor.trim();
    }
}