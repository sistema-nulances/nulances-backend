package com.Nulances.mapper;

import com.Nulances.domain.entity.BemMidia;
import com.Nulances.dto.response.BemMidiaResponse;
import com.Nulances.storage.R2Properties;
import com.Nulances.storage.R2Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BemMidiaMapper {

    private static final long URL_DOWNLOAD_EXPIRATION = 300L;

    private final R2Service r2Service;
    private final R2Properties r2Properties;

    public BemMidiaResponse toResponse(BemMidia midia) {
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