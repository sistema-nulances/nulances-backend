package com.Nulances.mapper;

import com.Nulances.domain.entity.BemMidia;
import com.Nulances.domain.entity.Lance;
import com.Nulances.domain.entity.Leilao;
import com.Nulances.domain.entity.LeilaoLote;
import com.Nulances.domain.entity.LeilaoLoteBem;
import com.Nulances.domain.enums.MarcaVeiculo;
import com.Nulances.dto.response.LeilaoCardResponse;
import com.Nulances.dto.response.LeilaoItemDetalheResponse;
import com.Nulances.dto.response.LeilaoResponse;
import org.springframework.stereotype.Component;

import java.util.Comparator;

@Component
public class LeilaoMapper {

    public LeilaoResponse toResponse(Leilao entity) {
        LeilaoResponse response = new LeilaoResponse();
        response.setId(entity.getId());
        response.setTitulo(entity.getTitulo());
        response.setLinkLive(entity.getLinkLive());
        response.setFormato(entity.getFormato());
        response.setCidade(entity.getCidade());
        response.setEndereco(entity.getEndereco());
        response.setLeiloeiroId(entity.getLeiloeiro().getId());
        response.setComitenteId(entity.getComitente().getId());
        response.setInicioLeilao(entity.getInicioLeilao());
        response.setFimLeilao(entity.getFimLeilao());
        response.setStatus(entity.getStatus());
        response.setCreatedAt(entity.getCreatedAt());
        response.setUpdatedAt(entity.getUpdatedAt());

        entity.getLotes().stream()
                .sorted(Comparator.comparing(leilaoLote -> leilaoLote.getLote().getCodigo()))
                .forEach(leilaoLote -> response.getLotes().add(toLoteResponse(leilaoLote)));

        return response;
    }

    public LeilaoCardResponse toCardResponse(LeilaoLoteBem item) {
        LeilaoCardResponse response = new LeilaoCardResponse();

        response.setLeilaoId(item.getLeilaoLote().getLeilao().getId());
        response.setLeilaoLoteId(item.getLeilaoLote().getId());
        response.setLeilaoLoteBemId(item.getId());
        response.setLoteId(item.getLeilaoLote().getLote().getId());
        response.setBemId(item.getBem().getId());

        response.setCodigoLote(item.getLeilaoLote().getLote().getCodigo());
        response.setStatusLeilao(item.getLeilaoLote().getLeilao().getStatus());

        response.setMarcaVeiculo(resolveMarcaVeiculo(item));
        response.setTipoVeiculo(item.getBem().getTipoVeiculo());
        response.setModelo(item.getBem().getModelo());
        response.setDescricao(item.getBem().getDescricao());
        response.setAno(item.getBem().getAno());
        response.setQuilometragem(item.getBem().getQuilometragem());
        response.setCambio(item.getBem().getCambio());
        response.setCombustivel(item.getBem().getCombustivel());
        response.setCondicao(item.getBem().getCondicao());

        response.setCidade(item.getLeilaoLote().getLeilao().getCidade());
        response.setEndereco(item.getLeilaoLote().getLeilao().getEndereco());

        response.setAberturaLeilao(item.getAberturaDisputa());
        response.setEncerramentoLeilao(item.getEncerramentoDisputa());

        response.setValorInicial(item.getValorInicial());
        response.setLanceAtual(item.getValorAtual());
        response.setProximoLance(item.getProximoLance());
        response.setIncrementoMinimo(item.getIncrementoMinimo());

        item.getBem().getMidias().stream()
                .sorted(Comparator.comparing(BemMidia::getOrdem))
                .forEach(midia -> {
                    LeilaoCardResponse.MidiaResponse midiaResponse = new LeilaoCardResponse.MidiaResponse();
                    midiaResponse.setId(midia.getId());
                    midiaResponse.setTipo(midia.getTipo());
                    midiaResponse.setArquivo(midia.getArquivo());
                    midiaResponse.setOrdem(midia.getOrdem());
                    response.getMidias().add(midiaResponse);
                });

        return response;
    }

    public LeilaoItemDetalheResponse toItemDetalheResponse(LeilaoLoteBem entity) {
        LeilaoItemDetalheResponse response = new LeilaoItemDetalheResponse();

        response.setLeilaoId(entity.getLeilaoLote().getLeilao().getId());
        response.setLeilaoLoteId(entity.getLeilaoLote().getId());
        response.setLeilaoLoteBemId(entity.getId());
        response.setLoteId(entity.getLeilaoLote().getLote().getId());
        response.setBemId(entity.getBem().getId());

        response.setTituloLeilao(entity.getLeilaoLote().getLeilao().getTitulo());
        response.setCodigoLote(entity.getLeilaoLote().getLote().getCodigo());
        response.setMarcaVeiculo(resolveMarcaVeiculo(entity));
        response.setModelo(entity.getBem().getModelo());
        response.setDescricao(entity.getBem().getDescricao());
        response.setCidade(entity.getLeilaoLote().getLeilao().getCidade());
        response.setFormatoLeilao(entity.getLeilaoLote().getLeilao().getFormato());

        response.setTipoVeiculo(entity.getBem().getTipoVeiculo());
        response.setAno(entity.getBem().getAno());
        response.setQuilometragem(entity.getBem().getQuilometragem());
        response.setCambio(entity.getBem().getCambio());
        response.setCombustivel(entity.getBem().getCombustivel());
        response.setCondicao(entity.getBem().getCondicao());
        response.setBlindado(entity.getBem().getBlindado());
        response.setCor(entity.getBem().getCor());
        response.setPlacaVeiculo(entity.getBem().getPlacaVeiculo());
        response.setFinalChassi(entity.getBem().getFinalChassi());

        response.setStatusLeilao(entity.getLeilaoLote().getLeilao().getStatus());
        response.setStatusItem(entity.getStatus());

        response.setValorInicial(entity.getValorInicial());
        response.setIncrementoMinimo(entity.getIncrementoMinimo());
        response.setLanceAtual(entity.getValorAtual());
        response.setProximoLance(entity.getProximoLance());
        response.setAberturaDisputa(entity.getAberturaDisputa());
        response.setEncerramentoDisputa(entity.getEncerramentoDisputa());

        response.setLeiloeiroNome(entity.getLeilaoLote().getLeilao().getLeiloeiro().getNome());
        response.setComitenteNome(entity.getLeilaoLote().getLeilao().getComitente().getNome());

        entity.getBem().getMidias().stream()
                .sorted(Comparator.comparing(BemMidia::getOrdem))
                .forEach(midia -> {
                    LeilaoItemDetalheResponse.MidiaResponse midiaResponse = new LeilaoItemDetalheResponse.MidiaResponse();
                    midiaResponse.setId(midia.getId());
                    midiaResponse.setTipo(midia.getTipo());
                    midiaResponse.setArquivo(midia.getArquivo());
                    midiaResponse.setOrdem(midia.getOrdem());
                    response.getMidias().add(midiaResponse);
                });

        return response;
    }

    public LeilaoItemDetalheResponse.HistoricoLanceResponse toHistoricoLanceResponse(Lance lance) {
        LeilaoItemDetalheResponse.HistoricoLanceResponse response =
                new LeilaoItemDetalheResponse.HistoricoLanceResponse();

        response.setLanceId(lance.getId());
        response.setValor(lance.getValor());
        response.setDataHora(lance.getCreatedAt());
        response.setUsuarioNome(lance.getUsuario() != null ? lance.getUsuario().getNomeCompleto() : null);

        return response;
    }

    private LeilaoResponse.LoteResponse toLoteResponse(LeilaoLote entity) {
        LeilaoResponse.LoteResponse response = new LeilaoResponse.LoteResponse();
        response.setLeilaoLoteId(entity.getId());
        response.setLoteId(entity.getLote().getId());
        response.setCodigoLote(entity.getLote().getCodigo());

        entity.getBens().stream()
                .sorted(Comparator.comparing(LeilaoLoteBem::getAberturaDisputa))
                .forEach(item -> response.getBens().add(toItemResponse(item)));

        return response;
    }

    private LeilaoResponse.ItemResponse toItemResponse(LeilaoLoteBem entity) {
        LeilaoResponse.ItemResponse response = new LeilaoResponse.ItemResponse();
        response.setLeilaoLoteBemId(entity.getId());
        response.setBemId(entity.getBem().getId());
        response.setMarcaVeiculo(resolveMarcaVeiculo(entity));
        response.setTipoVeiculo(entity.getBem().getTipoVeiculo());
        response.setModelo(entity.getBem().getModelo());
        response.setDescricao(entity.getBem().getDescricao());
        response.setAno(entity.getBem().getAno());
        response.setQuilometragem(entity.getBem().getQuilometragem());
        response.setCambio(entity.getBem().getCambio());
        response.setCombustivel(entity.getBem().getCombustivel());
        response.setCondicao(entity.getBem().getCondicao());
        response.setValorInicial(entity.getValorInicial());
        response.setIncrementoMinimo(entity.getIncrementoMinimo());
        response.setLanceAtual(entity.getValorAtual());
        response.setProximoLance(entity.getProximoLance());
        response.setAberturaDisputa(entity.getAberturaDisputa());
        response.setEncerramentoDisputa(entity.getEncerramentoDisputa());
        response.setStatus(entity.getStatus());

        entity.getBem().getMidias().stream()
                .sorted(Comparator.comparing(BemMidia::getOrdem))
                .forEach(midia -> {
                    LeilaoResponse.MidiaResponse midiaResponse = new LeilaoResponse.MidiaResponse();
                    midiaResponse.setId(midia.getId());
                    midiaResponse.setTipo(midia.getTipo());
                    midiaResponse.setArquivo(midia.getArquivo());
                    midiaResponse.setOrdem(midia.getOrdem());
                    response.getMidias().add(midiaResponse);
                });

        return response;
    }

    /**
     * {@code Marca.nome} é o enum {@link MarcaVeiculo}.
     */
    private MarcaVeiculo resolveMarcaVeiculo(LeilaoLoteBem item) {
        if (item == null || item.getBem() == null || item.getBem().getMarca() == null) {
            return null;
        }
        return item.getBem().getMarca().getNome();
    }
}