package com.Nulances.mapper;

import com.Nulances.domain.entity.Comitente;
import com.Nulances.dto.request.ComitenteCreateRequest;
import com.Nulances.dto.request.ComitenteUpdateRequest;
import com.Nulances.dto.response.ComitenteResponse;
import org.springframework.stereotype.Component;

@Component
public class ComitenteMapper {

    public Comitente toEntity(ComitenteCreateRequest request) {
        Comitente entity = new Comitente();
        entity.setNome(request.getNome());
        entity.setTipo(request.getTipo());
        entity.setDocumento(request.getDocumento());
        entity.setAtivoPlataforma(request.getAtivoPlataforma() != null ? request.getAtivoPlataforma() : true);
        entity.setSede(request.getSede());
        return entity;
    }

    public void updateEntity(Comitente entity, ComitenteUpdateRequest request) {
        if (request.getNome() != null) {
            entity.setNome(request.getNome());
        }
        if (request.getTipo() != null) {
            entity.setTipo(request.getTipo());
        }
        if (request.getDocumento() != null) {
            entity.setDocumento(request.getDocumento());
        }
        if (request.getAtivoPlataforma() != null) {
            entity.setAtivoPlataforma(request.getAtivoPlataforma());
        }
        if (request.getSede() != null) {
            entity.setSede(request.getSede());
        }
    }

    public ComitenteResponse toResponse(Comitente entity) {
        ComitenteResponse response = new ComitenteResponse();
        response.setId(entity.getId());
        response.setNome(entity.getNome());
        response.setTipo(entity.getTipo());
        response.setDocumento(entity.getDocumento());
        response.setAtivoPlataforma(entity.getAtivoPlataforma());
        response.setSede(entity.getSede());
        response.setCreatedAt(entity.getCreatedAt());
        response.setUpdatedAt(entity.getUpdatedAt());
        return response;
    }
}