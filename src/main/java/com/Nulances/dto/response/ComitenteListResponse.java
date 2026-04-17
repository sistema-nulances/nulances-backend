package com.Nulances.dto.response;

import com.Nulances.domain.enums.TipoComitente;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class ComitenteListResponse {

    private UUID id;
    private String nome;
    private String status;
    private TipoComitente tipo;
    private String documento;
    private Long totalLeiloes;
    private Long totalLeiloesAoVivo;
    private Long totalLeiloesEmBreve;
    private Long totalLeiloesEncerrado;

    public ComitenteListResponse(
            UUID id,
            String nome,
            Boolean ativoPlataforma,
            TipoComitente tipo,
            String documento,
            Number totalLeiloes,
            Number totalLeiloesAoVivo,
            Number totalLeiloesEmBreve,
            Number totalLeiloesEncerrado
    ) {
        this.id = id;
        this.nome = nome;
        this.status = Boolean.TRUE.equals(ativoPlataforma) ? "ATIVO" : "INATIVO";
        this.tipo = tipo;
        this.documento = documento;
        this.totalLeiloes = totalLeiloes != null ? totalLeiloes.longValue() : 0L;
        this.totalLeiloesAoVivo = totalLeiloesAoVivo != null ? totalLeiloesAoVivo.longValue() : 0L;
        this.totalLeiloesEmBreve = totalLeiloesEmBreve != null ? totalLeiloesEmBreve.longValue() : 0L;
        this.totalLeiloesEncerrado = totalLeiloesEncerrado != null ? totalLeiloesEncerrado.longValue() : 0L;
    }
}