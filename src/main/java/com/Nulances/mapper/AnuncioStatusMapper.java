package com.Nulances.mapper;

import com.Nulances.domain.entity.Anuncio;
import com.Nulances.dto.response.AnuncioStatusResponse;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;

@Component
public class AnuncioStatusMapper {

    public AnuncioStatusResponse toResponse(Anuncio anuncio, String mensagem, String motivo) {
        return AnuncioStatusResponse.builder()
                .id(anuncio.getId())
                .status(anuncio.getStatus())
                .mensagem(mensagem)
                .motivo(motivo)
                .atualizadoEm(anuncio.getUpdatedAt() != null
                        ? anuncio.getUpdatedAt().atOffset(ZoneOffset.UTC)
                        : anuncio.getCreatedAt().atOffset(ZoneOffset.UTC))
                .build();
    }
}