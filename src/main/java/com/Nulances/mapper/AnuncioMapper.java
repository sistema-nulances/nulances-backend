package com.Nulances.mapper;

import com.Nulances.domain.entity.Anuncio;
import com.Nulances.domain.entity.AnuncioDetalheTecnico;
import com.Nulances.domain.entity.AnuncioMidia;
import com.Nulances.domain.entity.Usuario;
import com.Nulances.dto.response.AnuncioAdminListResponse;
import com.Nulances.dto.response.AnuncioDetalheTecnicoResponse;
import com.Nulances.dto.response.AnuncioMidiaListResponse;
import com.Nulances.dto.response.AnuncioMidiaResponse;
import com.Nulances.dto.response.AnuncioModerarListResponse;
import com.Nulances.dto.response.AnuncioPublicoDetalheResponse;
import com.Nulances.dto.response.AnuncioPublicoDetalheTecnicoResponse;
import com.Nulances.dto.response.AnuncioPublicoListResponse;
import com.Nulances.dto.response.AnuncioPublicoMidiaResponse;
import com.Nulances.dto.response.AnuncioPublicoVendedorResponse;
import com.Nulances.dto.response.AnuncioResponse;
import com.Nulances.dto.response.AnuncioVendedorListResponse;
import com.Nulances.storage.R2Properties;
import com.Nulances.storage.R2Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;
import java.util.Comparator;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AnuncioMapper {

    private final R2Service r2Service;
    private final R2Properties r2Properties;

    public AnuncioResponse toResponse(Anuncio anuncio) {
        List<AnuncioMidiaResponse> midias = anuncio.getMidias().stream()
                .sorted(Comparator.comparing(AnuncioMidia::getOrdem))
                .map(this::toMidiaResponse)
                .toList();

        return new AnuncioResponse(
                anuncio.getId(),
                anuncio.getVendedor().getId(),
                anuncio.getVendedor().getNomeCompleto(),
                anuncio.getMarca() != null ? anuncio.getMarca().getId() : null,
                anuncio.getMarca() != null ? anuncio.getMarca().getNome().name() : null,
                anuncio.getCategoria(),
                anuncio.getModelo(),
                anuncio.getPreco(),
                anuncio.getCidade(),
                enumName(anuncio.getTipo()),
                enumName(anuncio.getCondicao()),
                anuncio.getAno(),
                anuncio.getQuilometragem(),
                enumName(anuncio.getCombustivel()),
                enumName(anuncio.getCambio()),
                anuncio.getFinalChassi(),
                anuncio.getCor(),
                anuncio.getBlindado(),
                anuncio.getPlacaVeiculo(),
                anuncio.getDescricao(),
                anuncio.getStatus(),
                anuncio.getCreatedAt(),
                midias,
                toDetalheTecnicoResponse(anuncio.getDetalheTecnico())
        );
    }

    public AnuncioVendedorListResponse toVendedorListResponse(Anuncio anuncio) {
        List<AnuncioMidiaListResponse> midias = anuncio.getMidias().stream()
                .sorted(Comparator.comparing(AnuncioMidia::getOrdem))
                .map(this::toMidiaListResponse)
                .toList();

        return AnuncioVendedorListResponse.builder()
                .id(anuncio.getId())
                .categoria(anuncio.getCategoria())
                .modelo(anuncio.getModelo())
                .marcaVeiculo(anuncio.getMarca() != null ? anuncio.getMarca().getNome() : null)
                .quandoFoiPostado(anuncio.getCreatedAt().atOffset(ZoneOffset.UTC))
                .valor(anuncio.getPreco())
                .status(anuncio.getStatus())
                .midias(midias)
                .build();
    }

    public AnuncioAdminListResponse toAdminListResponse(Anuncio anuncio) {
        List<AnuncioMidiaListResponse> midias = anuncio.getMidias().stream()
                .sorted(Comparator.comparing(AnuncioMidia::getOrdem))
                .map(this::toMidiaListResponse)
                .toList();

        return AnuncioAdminListResponse.builder()
                .id(anuncio.getId())
                .categoria(anuncio.getCategoria())
                .modelo(anuncio.getModelo())
                .marcaVeiculo(anuncio.getMarca() != null ? anuncio.getMarca().getNome() : null)
                .quandoFoiPostado(anuncio.getCreatedAt().atOffset(ZoneOffset.UTC))
                .valor(anuncio.getPreco())
                .status(anuncio.getStatus())
                .vendedorId(anuncio.getVendedor().getId())
                .vendedorNome(anuncio.getVendedor().getNomeCompleto())
                .midias(midias)
                .build();
    }

    public AnuncioModerarListResponse toModerarListResponse(Anuncio anuncio) {
        return AnuncioModerarListResponse.builder()
                .id(anuncio.getId())
                .modelo(anuncio.getModelo())
                .nomeVendedor(anuncio.getVendedor().getNomeCompleto())
                .enviadoEm(anuncio.getCreatedAt().atOffset(ZoneOffset.UTC))
                .tipoVeiculo(anuncio.getTipo())
                .build();
    }

    public AnuncioPublicoListResponse toPublicoListResponse(Anuncio anuncio) {
        List<AnuncioPublicoMidiaResponse> imagens = anuncio.getMidias().stream()
                .sorted(Comparator.comparing(AnuncioMidia::getOrdem))
                .map(this::toPublicoMidiaResponse)
                .toList();

        return AnuncioPublicoListResponse.builder()
                .id(anuncio.getId())
                .categoria(anuncio.getCategoria())
                .modelo(anuncio.getModelo())
                .marcaVeiculo(anuncio.getMarca() != null ? anuncio.getMarca().getNome() : null)
                .preco(anuncio.getPreco())
                .cidade(anuncio.getCidade())
                .tipoVeiculo(anuncio.getTipo())
                .condicao(anuncio.getCondicao())
                .ano(anuncio.getAno())
                .quilometragem(anuncio.getQuilometragem())
                .combustivel(anuncio.getCombustivel())
                .cambio(anuncio.getCambio())
                .imagens(imagens)
                .build();
    }

    public AnuncioPublicoDetalheResponse toPublicoDetalheResponse(Anuncio anuncio) {
        List<AnuncioPublicoMidiaResponse> imagens = anuncio.getMidias().stream()
                .sorted(Comparator.comparing(AnuncioMidia::getOrdem))
                .map(this::toPublicoMidiaResponse)
                .toList();

        return AnuncioPublicoDetalheResponse.builder()
                .id(anuncio.getId())
                .categoria(anuncio.getCategoria())
                .marcaVeiculo(anuncio.getMarca() != null ? anuncio.getMarca().getNome() : null)
                .modelo(anuncio.getModelo())
                .preco(anuncio.getPreco())
                .cidade(anuncio.getCidade())
                .tipoVeiculo(anuncio.getTipo())
                .blindado(anuncio.getBlindado())
                .quilometragem(anuncio.getQuilometragem())
                .ano(anuncio.getAno())
                .cor(anuncio.getCor())
                .combustivel(anuncio.getCombustivel())
                .cambio(anuncio.getCambio())
                .descricao(anuncio.getDescricao())
                .condicao(anuncio.getCondicao())
                .detalheTecnico(toPublicoDetalheTecnicoResponse(anuncio.getDetalheTecnico()))
                .vendedor(toPublicoVendedorResponse(anuncio.getVendedor()))
                .imagens(imagens)
                .build();
    }

    private AnuncioDetalheTecnicoResponse toDetalheTecnicoResponse(AnuncioDetalheTecnico detalhe) {
        if (detalhe == null) {
            return null;
        }

        return new AnuncioDetalheTecnicoResponse(
                detalhe.getMotorizacao(),
                detalhe.getCilindros(),
                detalhe.getPotenciaCombinada(),
                detalhe.getTorqueCombinado(),
                detalhe.getTransmissao(),
                detalhe.getTracao(),
                detalhe.getModosConducao(),
                detalhe.getCarroceria(),
                detalhe.getComprimentoLarguraAltura(),
                detalhe.getEntreEixos(),
                detalhe.getPortaMalas(),
                detalhe.getTanqueCombustivel(),
                detalhe.getCiclosUrbano(),
                detalhe.getUsoModoEletrico(),
                detalhe.getEmissoesSeloEficiencia(),
                detalhe.getFreiosDianteiros(),
                detalhe.getSuspensaoDianteira(),
                detalhe.getSuspensaoTraseira(),
                detalhe.getMedidaPneus(),
                detalhe.getEstepe(),
                detalhe.getAirbags(),
                detalhe.getAbsDistribuicaoEletronica(),
                detalhe.getControleEstabilidadeTracao(),
                detalhe.getAssistentePartidaRampa(),
                detalhe.getCameraSensoresEstacionamento(),
                detalhe.getArCondicionadoClimatizador(),
                detalhe.getDirecao(),
                detalhe.getBancosVolante(),
                detalhe.getMultimidiaConectividade(),
                detalhe.getRodasIluminacao(),
                detalhe.getVidrosTravas(),
                detalhe.getProcedenciaNulances(),
                detalhe.getLicenciamentoDebitos(),
                detalhe.getRestricoesGravame(),
                detalhe.getChavesManual(),
                detalhe.getLaudoCautelarInspecao()
        );
    }

    private AnuncioPublicoDetalheTecnicoResponse toPublicoDetalheTecnicoResponse(AnuncioDetalheTecnico detalhe) {
        if (detalhe == null) {
            return null;
        }

        return new AnuncioPublicoDetalheTecnicoResponse(
                detalhe.getMotorizacao(),
                detalhe.getCilindros(),
                detalhe.getPotenciaCombinada(),
                detalhe.getTorqueCombinado(),
                detalhe.getTransmissao(),
                detalhe.getTracao(),
                detalhe.getModosConducao(),
                detalhe.getCarroceria(),
                detalhe.getComprimentoLarguraAltura(),
                detalhe.getEntreEixos(),
                detalhe.getPortaMalas(),
                detalhe.getTanqueCombustivel(),
                detalhe.getCiclosUrbano(),
                detalhe.getUsoModoEletrico(),
                detalhe.getEmissoesSeloEficiencia(),
                detalhe.getFreiosDianteiros(),
                detalhe.getSuspensaoDianteira(),
                detalhe.getSuspensaoTraseira(),
                detalhe.getMedidaPneus(),
                detalhe.getEstepe(),
                detalhe.getAirbags(),
                detalhe.getAbsDistribuicaoEletronica(),
                detalhe.getControleEstabilidadeTracao(),
                detalhe.getAssistentePartidaRampa(),
                detalhe.getCameraSensoresEstacionamento(),
                detalhe.getArCondicionadoClimatizador(),
                detalhe.getDirecao(),
                detalhe.getBancosVolante(),
                detalhe.getMultimidiaConectividade(),
                detalhe.getRodasIluminacao(),
                detalhe.getVidrosTravas(),
                detalhe.getProcedenciaNulances(),
                detalhe.getLicenciamentoDebitos(),
                detalhe.getRestricoesGravame(),
                detalhe.getChavesManual(),
                detalhe.getLaudoCautelarInspecao()
        );
    }

    private AnuncioPublicoVendedorResponse toPublicoVendedorResponse(Usuario vendedor) {
        String sobre = null;
        String telefoneContato = vendedor.getTelefone();
        String cidade = vendedor.getCidade();

        if (vendedor.getSolicitacaoVendedor() != null) {
            sobre = vendedor.getSolicitacaoVendedor().getInformacoesNegocio();

            if (vendedor.getSolicitacaoVendedor().getTelefone() != null
                    && !vendedor.getSolicitacaoVendedor().getTelefone().isBlank()) {
                telefoneContato = vendedor.getSolicitacaoVendedor().getTelefone();
            }

            if (vendedor.getSolicitacaoVendedor().getCidade() != null
                    && !vendedor.getSolicitacaoVendedor().getCidade().isBlank()) {
                cidade = vendedor.getSolicitacaoVendedor().getCidade();
            }
        }

        return new AnuncioPublicoVendedorResponse(
                vendedor.getNomeCompleto(),
                cidade,
                vendedor.getFotoPerfil(),
                gerarUrlAssinada(vendedor.getFotoPerfil()),
                sobre,
                telefoneContato
        );
    }

    private AnuncioMidiaResponse toMidiaResponse(AnuncioMidia midia) {
        return new AnuncioMidiaResponse(
                midia.getTipo(),
                midia.getArquivo(),
                gerarUrlAssinada(midia.getArquivo()),
                midia.getOrdem()
        );
    }

    private AnuncioMidiaListResponse toMidiaListResponse(AnuncioMidia midia) {
        return AnuncioMidiaListResponse.builder()
                .id(midia.getId())
                .tipo(midia.getTipo())
                .arquivo(midia.getArquivo())
                .url(gerarUrlAssinada(midia.getArquivo()))
                .ordem(midia.getOrdem())
                .build();
    }

    private AnuncioPublicoMidiaResponse toPublicoMidiaResponse(AnuncioMidia midia) {
        return new AnuncioPublicoMidiaResponse(
                midia.getTipo(),
                midia.getArquivo(),
                gerarUrlAssinada(midia.getArquivo()),
                midia.getOrdem()
        );
    }

    private String gerarUrlAssinada(String objectKey) {
        if (objectKey == null || objectKey.isBlank()) {
            return null;
        }

        return r2Service.gerarUrlDownload(
                r2Properties.bucket(),
                objectKey,
                r2Properties.downloadExpiresInSeconds()
        );
    }

    private String enumName(Enum<?> value) {
        return value != null ? value.name() : null;
    }
}