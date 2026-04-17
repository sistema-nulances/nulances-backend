package com.Nulances.mapper;

import com.Nulances.domain.entity.Arrematacao;
import com.Nulances.domain.entity.Bem;
import com.Nulances.domain.entity.Comitente;
import com.Nulances.domain.entity.Leilao;
import com.Nulances.domain.entity.LeilaoLoteBem;
import com.Nulances.domain.entity.Lote;
import com.Nulances.dto.response.ComitenteResumoResponse;
import com.Nulances.dto.response.ContatoLoteResponse;
import com.Nulances.dto.response.DocumentoLoteResponse;
import com.Nulances.dto.response.LeilaoGanhoItemResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LeiloesGanhosMapper {

    public LeilaoGanhoItemResponse toResponse(Arrematacao a) {
        LeilaoLoteBem llb = a.getLeilaoLoteBem();
        Bem bem = llb != null ? llb.getBem() : null;
        Leilao leilao = (llb != null && llb.getLeilaoLote() != null) ? llb.getLeilaoLote().getLeilao() : null;
        Comitente comitente = leilao != null ? leilao.getComitente() : null;
        Lote lote = bem != null ? bem.getLote() : null;

        return LeilaoGanhoItemResponse.builder()
                .id(str(a.getId()))
                .leilaoId(leilao != null ? str(leilao.getId()) : null)
                .leilaoLoteId(llb != null && llb.getLeilaoLote() != null ? str(llb.getLeilaoLote().getId()) : null)
                .leilaoLoteBemId(llb != null ? str(llb.getId()) : null)

                .loteId(lote != null ? str(lote.getId()) : null)
                .codigoLote(lote != null ? str(lote.getCodigo()) : null)

                .bemId(bem != null ? str(bem.getId()) : null)
                .tituloLeilao(leilao != null ? leilao.getTitulo() : null)

                .titulo(bem != null ? bem.getDescricao() : null)
                .marcaVeiculo(
                        bem != null && bem.getMarca() != null && bem.getMarca().getNome() != null
                                ? bem.getMarca().getNome().name()
                                : null
                )
                .modelo(bem != null ? bem.getModelo() : null)
                .tipoVeiculo(bem != null && bem.getTipoVeiculo() != null ? bem.getTipoVeiculo().name() : null)

                .cidade(leilao != null ? leilao.getCidade() : null)
                .estado(leilao != null ? leilao.getEndereco() : null)

                .anoFabricacao(bem != null ? bem.getAno() : null)
                .anoModelo(bem != null ? bem.getAno() : null)

                .quilometragem(bem != null ? bem.getQuilometragem() : null)
                .cambio(bem != null && bem.getCambio() != null ? bem.getCambio().name() : null)
                .combustivel(bem != null && bem.getCombustivel() != null ? bem.getCombustivel().name() : null)
                .placaVeiculo(bem != null ? bem.getPlacaVeiculo() : null)

                .midiaCapaUrl(extrairCapa(bem))
                .valorArrematado(a.getValorFinal()) // entidade usa valorFinal

                .aberturaDisputa(llb != null ? llb.getAberturaDisputa() : null)
                .encerramentoDisputa(llb != null ? llb.getEncerramentoDisputa() : null)

                .comitente(toComitente(comitente))
                .documentos(extrairDocumentos()) // sem documentos por enquanto
                .contato(toContato(comitente))

                .createdAt(a.getCreatedAt())
                .updatedAt(a.getUpdatedAt())
                .build();
    }

    private ComitenteResumoResponse toComitente(Comitente c) {
        if (c == null) return null;
        return ComitenteResumoResponse.builder()
                .id(str(c.getId()))
                .nome(c.getNome())
                .tipo(c.getTipo() != null ? c.getTipo().name() : null)
                .build();
    }

    private List<DocumentoLoteResponse> extrairDocumentos() {
        return List.of();
    }

    private ContatoLoteResponse toContato(Comitente c) {
        return ContatoLoteResponse.builder()
                .nome(c != null ? c.getNome() : "Atendimento NuLances")
                .email(null)
                .telefone(null)
                .build();
    }

    private String extrairCapa(Bem bem) {
        if (bem == null || bem.getMidias() == null || bem.getMidias().isEmpty()) return null;

        return bem.getMidias().stream()
                .sorted((a, b) -> Integer.compare(
                        a.getOrdem() == null ? Integer.MAX_VALUE : a.getOrdem(),
                        b.getOrdem() == null ? Integer.MAX_VALUE : b.getOrdem()))
                .map(m -> m.getArquivo())
                .filter(v -> v != null && !v.isBlank())
                .findFirst()
                .orElse(null);
    }

    private String str(Object v) {
        return v == null ? null : String.valueOf(v);
    }
}