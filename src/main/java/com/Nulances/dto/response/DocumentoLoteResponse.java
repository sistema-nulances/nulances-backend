package com.Nulances.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(toBuilder = true)
public class DocumentoLoteResponse {
    private String id;
    private String nome;
    private String tipo;
    private String url; // objectKey ou URL (service resolve)
    private boolean disponivel;
}