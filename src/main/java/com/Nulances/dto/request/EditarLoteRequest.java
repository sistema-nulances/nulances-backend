package com.Nulances.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class EditarLoteRequest {

    private String nome;
    private String observacoes;
    private List<UUID> bemIds;
}