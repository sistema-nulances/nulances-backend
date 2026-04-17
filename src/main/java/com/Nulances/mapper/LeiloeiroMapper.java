package com.Nulances.mapper;

import com.Nulances.domain.entity.Leiloeiro;
import com.Nulances.dto.response.LeiloeiroResponse;
import org.springframework.stereotype.Component;

@Component
public class LeiloeiroMapper {

    public LeiloeiroResponse toResponse(Leiloeiro leiloeiro, long totalLeiloes) {
        LeiloeiroResponse response = new LeiloeiroResponse();
        response.setId(leiloeiro.getId());
        response.setNome(leiloeiro.getNome());
        response.setRegistroProfissional(leiloeiro.getRegistroProfissional());
        response.setCpf(leiloeiro.getCpf());
        response.setEmail(leiloeiro.getEmail());
        response.setTelefone(leiloeiro.getTelefone());
        response.setAtivoPlataforma(leiloeiro.getAtivoPlataforma());
        response.setLocal(leiloeiro.getLocal());
        response.setTotalLeiloes(totalLeiloes);
        response.setCreatedAt(leiloeiro.getCreatedAt());
        response.setUpdatedAt(leiloeiro.getUpdatedAt());
        return response;
    }
}