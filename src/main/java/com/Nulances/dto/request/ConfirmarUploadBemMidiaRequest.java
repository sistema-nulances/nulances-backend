package com.Nulances.dto.request;

import com.Nulances.domain.enums.TipoMidiaBem;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConfirmarUploadBemMidiaRequest {

    private TipoMidiaBem tipo;
    private String objectKey;
    private Integer ordem;
}