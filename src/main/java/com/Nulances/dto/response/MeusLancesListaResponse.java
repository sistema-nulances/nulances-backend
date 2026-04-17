package com.Nulances.dto.response;

import java.util.List;

public class MeusLancesListaResponse {

    private List<MeuLanceParticipacaoResponse> itens;
    private long totalElements;

    public List<MeuLanceParticipacaoResponse> getItens() { return itens; }
    public void setItens(List<MeuLanceParticipacaoResponse> itens) { this.itens = itens; }

    public long getTotalElements() { return totalElements; }
    public void setTotalElements(long totalElements) { this.totalElements = totalElements; }
}