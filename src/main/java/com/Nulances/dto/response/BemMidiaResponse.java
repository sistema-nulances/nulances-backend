package com.Nulances.dto.response;

import com.Nulances.domain.enums.TipoMidiaBem;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
public class BemMidiaResponse {

    private UUID id;
    private TipoMidiaBem tipo;
    private String arquivo;
    /** URL GET pré-assinada (R2) para o front exibir foto/vídeo. */
    private String arquivoUrl;
    private Integer ordem;
    private Instant createdAt;
}