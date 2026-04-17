package com.Nulances.mapper;

import com.Nulances.domain.entity.Banner;
import com.Nulances.dto.request.BannerCreateRequest;
import com.Nulances.dto.response.BannerAdminResponse;
import com.Nulances.dto.response.BannerPublicResponse;
import org.springframework.stereotype.Component;

@Component
public class BannerMapper {

    public Banner toEntity(BannerCreateRequest request) {
        Banner banner = new Banner();
        banner.setTipo(request.getTipo());
        banner.setPosicao(request.getPosicao());
        banner.setTextoAlternativo(normalizarOpcional(request.getTextoAlternativo()));
        banner.setImagem(request.getImagem().trim());
        banner.setAtivo(request.getAtivo() != null ? request.getAtivo() : true);
        return banner;
    }

    public BannerPublicResponse toPublicResponse(Banner banner) {
        BannerPublicResponse response = new BannerPublicResponse();
        response.setId(banner.getId());
        response.setTipo(banner.getTipo());
        response.setPosicao(banner.getPosicao());
        response.setTextoAlternativo(banner.getTextoAlternativo());
        response.setImagem(banner.getImagem());
        return response;
    }

    public BannerAdminResponse toAdminResponse(Banner banner, String arquivoUrl) {
        BannerAdminResponse response = new BannerAdminResponse();
        response.setId(banner.getId());
        response.setTipo(banner.getTipo());
        response.setPosicao(banner.getPosicao());
        response.setTextoAlternativo(banner.getTextoAlternativo());
        response.setImagem(banner.getImagem());
        response.setArquivoUrl(arquivoUrl);
        response.setAtivo(banner.getAtivo());
        response.setCreatedAt(banner.getCreatedAt());
        response.setUpdatedAt(banner.getUpdatedAt());
        return response;
    }

    private String normalizarOpcional(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        return valor.trim();
    }
}