package com.Nulances.dto.request;

import com.Nulances.domain.enums.FormatoLeilao;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class LeilaoCreateRequest {

    @NotBlank(message = "Título é obrigatório.")
    private String titulo;

    @Size(max = 500, message = "Link da live deve ter no máximo 500 caracteres.")
    private String linkLive;

    @NotNull(message = "Formato do leilão é obrigatório.")
    private FormatoLeilao formato;

    private String cidade;
    private String endereco;

    @NotNull(message = "Leiloeiro é obrigatório.")
    private UUID leiloeiroId;

    @NotNull(message = "Comitente é obrigatório.")
    private UUID comitenteId;

    @Valid
    @NotEmpty(message = "Informe ao menos um lote.")
    private List<LoteRequest> lotes;

    @Getter
    @Setter
    public static class LoteRequest {

        @NotNull(message = "Lote é obrigatório.")
        private UUID loteId;

        @Valid
        @NotEmpty(message = "Informe ao menos um bem para o lote.")
        private List<BemRequest> bens;
    }

    @Getter
    @Setter
    public static class BemRequest {

        @NotNull(message = "Bem é obrigatório.")
        private UUID bemId;

        @NotNull(message = "Valor inicial é obrigatório.")
        @DecimalMin(value = "0.01", message = "Valor inicial deve ser maior que zero.")
        private BigDecimal valorInicial;

        @NotNull(message = "Incremento mínimo é obrigatório.")
        @DecimalMin(value = "0.01", message = "Incremento mínimo deve ser maior que zero.")
        private BigDecimal incrementoMinimo;

        @NotNull(message = "Abertura da disputa é obrigatória.")
        private Instant aberturaDisputa;

        @NotNull(message = "Encerramento da disputa é obrigatório.")
        private Instant encerramentoDisputa;
    }
}