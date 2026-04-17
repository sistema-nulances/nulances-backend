package com.Nulances.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class LeiloesGanhosResponse {
    private List<LeilaoGanhoItemResponse> itens;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
}