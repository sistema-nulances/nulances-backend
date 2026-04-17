package com.Nulances.mapper;

import com.Nulances.domain.entity.Bem;
import com.Nulances.domain.entity.BemMidia;
import com.Nulances.dto.response.BemMidiaResponse;
import com.Nulances.dto.response.BemResponse;
import com.Nulances.dto.response.BemResumoResponse;
import com.Nulances.storage.R2Properties;
import com.Nulances.storage.R2Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class BemMapper {

    private static final long URL_DOWNLOAD_EXPIRATION = 300L;

    private final R2Service r2Service;
    private final R2Properties r2Properties;

    public BemResumoResponse toResumoResponse(Bem bem) {
        BemResumoResponse response = new BemResumoResponse();
        response.setId(bem.getId());
        response.setMarca(bem.getMarca() != null ? bem.getMarca().getNome() : null);
        response.setModelo(bem.getModelo());
        response.setTipoVeiculo(bem.getTipoVeiculo());
        response.setAno(bem.getAno());
        response.setPlacaVeiculo(bem.getPlacaVeiculo());
        response.setStatus(bem.getStatus());
        response.setLoteId(bem.getLote() != null ? bem.getLote().getId() : null);
        response.setCreatedAt(bem.getCreatedAt());
        return response;
    }

    public BemResponse toResponse(Bem bem, List<BemMidia> midias) {
        BemResponse response = new BemResponse();
        response.setId(bem.getId());
        response.setLoteId(bem.getLote() != null ? bem.getLote().getId() : null);
        response.setMarcaId(bem.getMarca() != null ? bem.getMarca().getId() : null);
        response.setMarca(bem.getMarca() != null ? bem.getMarca().getNome() : null);
        response.setModelo(bem.getModelo());
        response.setTipoVeiculo(bem.getTipoVeiculo());
        response.setCondicao(bem.getCondicao());
        response.setAno(bem.getAno());
        response.setQuilometragem(bem.getQuilometragem());
        response.setFinalChassi(bem.getFinalChassi());
        response.setCombustivel(bem.getCombustivel());
        response.setCambio(bem.getCambio());
        response.setBlindado(bem.getBlindado());
        response.setCor(bem.getCor());
        response.setPlacaVeiculo(bem.getPlacaVeiculo());
        response.setDescricao(bem.getDescricao());
        response.setStatus(bem.getStatus());
        response.setCreatedAt(bem.getCreatedAt());
        response.setUpdatedAt(bem.getUpdatedAt());

        response.setMidias(midias == null
                ? Collections.emptyList()
                : midias.stream().map(this::toMidiaResponse).toList());

        return response;
    }

    private BemMidiaResponse toMidiaResponse(BemMidia midia) {
        BemMidiaResponse response = new BemMidiaResponse();
        response.setId(midia.getId());
        response.setTipo(midia.getTipo());
        response.setArquivo(midia.getArquivo());
        response.setArquivoUrl(r2Service.gerarUrlDownload(
                r2Properties.bucket(),
                midia.getArquivo(),
                URL_DOWNLOAD_EXPIRATION
        ));
        response.setOrdem(midia.getOrdem());
        response.setCreatedAt(midia.getCreatedAt());
        return response;
    }
}