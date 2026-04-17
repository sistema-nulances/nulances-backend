package com.Nulances.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ComitenteStatsResponse {

    private Long totalComitentes;
    private Long totalBancos;
    private Long totalSeguradoras;
    private Long totalPessoaFisica;
    private Long totalEmpresas;

    public ComitenteStatsResponse(
            Number totalComitentes,
            Number totalBancos,
            Number totalSeguradoras,
            Number totalPessoaFisica,
            Number totalEmpresas
    ) {
        this.totalComitentes = totalComitentes != null ? totalComitentes.longValue() : 0L;
        this.totalBancos = totalBancos != null ? totalBancos.longValue() : 0L;
        this.totalSeguradoras = totalSeguradoras != null ? totalSeguradoras.longValue() : 0L;
        this.totalPessoaFisica = totalPessoaFisica != null ? totalPessoaFisica.longValue() : 0L;
        this.totalEmpresas = totalEmpresas != null ? totalEmpresas.longValue() : 0L;
    }
}