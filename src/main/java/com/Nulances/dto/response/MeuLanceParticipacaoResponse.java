package com.Nulances.dto.response;

import com.Nulances.domain.enums.MarcaVeiculo;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class MeuLanceParticipacaoResponse {

    private UUID leilaoLoteBemId;
    private UUID lanceId;
    private BigDecimal meuValor;
    private Instant meuLanceEm;
    private MarcaVeiculo marcaVeiculo;

    private UUID leilaoId;
    private String tituloLeilao;
    private String formatoLeilao;
    private String statusLeilao;
    private String cidade;

    private String codigoLote;
    private String nomeBem;
    private String tipoVeiculo;
    private String statusItem;
    private BigDecimal valorAtual;
    private Instant aberturaDisputa;
    private Instant encerramentoDisputa;

    private String midiaCapaUrl;

    private ResultadoParticipacaoUsuarioLeilao resultadoParticipacao;
    private Integer quantidadeLancesMeuUsuario;

    public UUID getLeilaoLoteBemId() {
        return leilaoLoteBemId;
    }

    public void setLeilaoLoteBemId(UUID leilaoLoteBemId) {
        this.leilaoLoteBemId = leilaoLoteBemId;
    }

    public UUID getLanceId() {
        return lanceId;
    }

    public void setLanceId(UUID lanceId) {
        this.lanceId = lanceId;
    }

    public BigDecimal getMeuValor() {
        return meuValor;
    }

    public void setMeuValor(BigDecimal meuValor) {
        this.meuValor = meuValor;
    }

    public Instant getMeuLanceEm() {
        return meuLanceEm;
    }

    public void setMeuLanceEm(Instant meuLanceEm) {
        this.meuLanceEm = meuLanceEm;
    }

    public MarcaVeiculo getMarcaVeiculo() {
        return marcaVeiculo;
    }

    public void setMarcaVeiculo(MarcaVeiculo marcaVeiculo) {
        this.marcaVeiculo = marcaVeiculo;
    }

    public UUID getLeilaoId() {
        return leilaoId;
    }

    public void setLeilaoId(UUID leilaoId) {
        this.leilaoId = leilaoId;
    }

    public String getTituloLeilao() {
        return tituloLeilao;
    }

    public void setTituloLeilao(String tituloLeilao) {
        this.tituloLeilao = tituloLeilao;
    }

    public String getFormatoLeilao() {
        return formatoLeilao;
    }

    public void setFormatoLeilao(String formatoLeilao) {
        this.formatoLeilao = formatoLeilao;
    }

    public String getStatusLeilao() {
        return statusLeilao;
    }

    public void setStatusLeilao(String statusLeilao) {
        this.statusLeilao = statusLeilao;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getCodigoLote() {
        return codigoLote;
    }

    public void setCodigoLote(String codigoLote) {
        this.codigoLote = codigoLote;
    }

    public String getNomeBem() {
        return nomeBem;
    }

    public void setNomeBem(String nomeBem) {
        this.nomeBem = nomeBem;
    }

    public String getTipoVeiculo() {
        return tipoVeiculo;
    }

    public void setTipoVeiculo(String tipoVeiculo) {
        this.tipoVeiculo = tipoVeiculo;
    }

    public String getStatusItem() {
        return statusItem;
    }

    public void setStatusItem(String statusItem) {
        this.statusItem = statusItem;
    }

    public BigDecimal getValorAtual() {
        return valorAtual;
    }

    public void setValorAtual(BigDecimal valorAtual) {
        this.valorAtual = valorAtual;
    }

    public Instant getAberturaDisputa() {
        return aberturaDisputa;
    }

    public void setAberturaDisputa(Instant aberturaDisputa) {
        this.aberturaDisputa = aberturaDisputa;
    }

    public Instant getEncerramentoDisputa() {
        return encerramentoDisputa;
    }

    public void setEncerramentoDisputa(Instant encerramentoDisputa) {
        this.encerramentoDisputa = encerramentoDisputa;
    }

    public String getMidiaCapaUrl() {
        return midiaCapaUrl;
    }

    public void setMidiaCapaUrl(String midiaCapaUrl) {
        this.midiaCapaUrl = midiaCapaUrl;
    }

    public ResultadoParticipacaoUsuarioLeilao getResultadoParticipacao() {
        return resultadoParticipacao;
    }

    public void setResultadoParticipacao(ResultadoParticipacaoUsuarioLeilao resultadoParticipacao) {
        this.resultadoParticipacao = resultadoParticipacao;
    }

    public Integer getQuantidadeLancesMeuUsuario() {
        return quantidadeLancesMeuUsuario;
    }

    public void setQuantidadeLancesMeuUsuario(Integer quantidadeLancesMeuUsuario) {
        this.quantidadeLancesMeuUsuario = quantidadeLancesMeuUsuario;
    }
}