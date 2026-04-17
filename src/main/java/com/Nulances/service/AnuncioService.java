package com.Nulances.service;

import com.Nulances.config.security.CustomUserDetails;
import com.Nulances.domain.entity.Anuncio;
import com.Nulances.domain.entity.AnuncioDetalheTecnico;
import com.Nulances.domain.entity.AnuncioMidia;
import com.Nulances.domain.entity.Marca;
import com.Nulances.domain.entity.Usuario;
import com.Nulances.domain.enums.MarcaVeiculo;
import com.Nulances.domain.enums.StatusAnuncio;
import com.Nulances.domain.enums.UserRole;
import com.Nulances.dto.request.AnuncioDetalheTecnicoRequest;
import com.Nulances.dto.request.AnuncioMidiaRequest;
import com.Nulances.dto.request.CriarAnuncioRequest;
import com.Nulances.dto.request.EditarAnuncioDetalheTecnicoRequest;
import com.Nulances.dto.request.EditarAnuncioRequest;
import com.Nulances.dto.request.ListarAnunciosPublicosRequest;
import com.Nulances.dto.request.ListarMeusAnunciosRequest;
import com.Nulances.dto.response.AnuncioPublicoDetalheResponse;
import com.Nulances.dto.response.AnuncioPublicoListResponse;
import com.Nulances.dto.response.AnuncioResponse;
import com.Nulances.dto.response.AnuncioVendedorListResponse;
import com.Nulances.mapper.AnuncioMapper;
import com.Nulances.repository.AnuncioRepository;
import com.Nulances.repository.MarcaRepository;
import com.Nulances.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AnuncioService {

    private final AnuncioRepository anuncioRepository;
    private final MarcaRepository marcaRepository;
    private final UsuarioRepository usuarioRepository;
    private final AnuncioMapper anuncioMapper;

    @PreAuthorize("hasAnyRole('VENDEDOR','ADMIN')")
    @Transactional
    public AnuncioResponse criar(CriarAnuncioRequest request, CustomUserDetails userDetails) {
        Usuario vendedor = buscarUsuarioAutenticado(userDetails);
        validarPermissaoVendedorOuAdmin(vendedor);

        Marca marca = buscarMarca(request.getMarca());

        Anuncio anuncio = new Anuncio();
        anuncio.setVendedor(vendedor);
        anuncio.setMarca(marca);
        anuncio.setModelo(trim(request.getModelo()));
        anuncio.setPreco(request.getPreco());
        anuncio.setCidade(trim(request.getCidade()));
        anuncio.setTipo(request.getTipo());
        anuncio.setCondicao(request.getCondicao());
        anuncio.setAno(request.getAno());
        anuncio.setQuilometragem(request.getQuilometragem());
        anuncio.setCombustivel(request.getCombustivel());
        anuncio.setCambio(request.getCambio());
        anuncio.setFinalChassi(trim(request.getFinalChassi()));
        anuncio.setCor(trim(request.getCor()));
        anuncio.setBlindado(request.getBlindado() != null ? request.getBlindado() : false);
        anuncio.setPlacaVeiculo(trim(request.getPlacaVeiculo()));
        anuncio.setDescricao(trim(request.getDescricao()));
        anuncio.setStatus(StatusAnuncio.PENDENTE);

        if (request.getDetalheTecnico() != null) {
            anuncio.definirDetalheTecnico(criarDetalheTecnico(request.getDetalheTecnico()));
        }

        List<AnuncioMidiaRequest> midiasRequest = request.getMidias() != null
                ? request.getMidias()
                : Collections.emptyList();

        for (AnuncioMidiaRequest midiaRequest : midiasRequest) {
            AnuncioMidia midia = new AnuncioMidia();
            midia.setTipo(midiaRequest.getTipo());
            midia.setArquivo(trim(midiaRequest.getArquivo()));
            midia.setOrdem(midiaRequest.getOrdem() != null ? midiaRequest.getOrdem() : 0);
            anuncio.adicionarMidia(midia);
        }

        anuncio = anuncioRepository.save(anuncio);

        Anuncio anuncioDetalhado = anuncioRepository.findDetalhadoById(anuncio.getId())
                .orElseThrow(() -> new IllegalArgumentException("Anúncio criado, mas não foi possível carregá-lo."));

        return anuncioMapper.toResponse(anuncioDetalhado);
    }

    @Transactional(readOnly = true)
    public Page<AnuncioPublicoListResponse> listarPublicos(
            ListarAnunciosPublicosRequest request,
            Pageable pageable
    ) {
        String busca = normalizarBusca(request != null ? request.getBusca() : null);

        Page<Anuncio> page;

        if (busca == null) {
            page = anuncioRepository.findAllByStatusOrderByCreatedAtDesc(StatusAnuncio.PUBLICADO, pageable);
        } else {
            page = anuncioRepository.findAllByStatusAndModeloContainingIgnoreCaseOrderByCreatedAtDesc(
                    StatusAnuncio.PUBLICADO,
                    busca,
                    pageable
            );
        }

        return page.map(anuncioMapper::toPublicoListResponse);
    }

    @Transactional(readOnly = true)
    public AnuncioPublicoDetalheResponse buscarPublicadoPorId(UUID id) {
        Anuncio anuncio = anuncioRepository.findDetalhadoPublicadoById(id)
                .orElseThrow(() -> new IllegalArgumentException("Anúncio publicado não encontrado."));

        return anuncioMapper.toPublicoDetalheResponse(anuncio);
    }

    @PreAuthorize("hasAnyRole('VENDEDOR','ADMIN')")
    @Transactional(readOnly = true)
    public Page<AnuncioVendedorListResponse> listarMeusAnuncios(
            ListarMeusAnunciosRequest request,
            Pageable pageable,
            CustomUserDetails userDetails
    ) {
        Usuario vendedor = buscarUsuarioAutenticado(userDetails);
        validarPermissaoVendedorOuAdmin(vendedor);

        String busca = normalizarBusca(request != null ? request.getBusca() : null);
        StatusAnuncio status = request != null ? request.getStatus() : null;

        Page<Anuncio> page;

        if (status == null && busca == null) {
            page = anuncioRepository.findByVendedorIdOrderByCreatedAtDesc(vendedor.getId(), pageable);
        } else if (status != null && busca == null) {
            page = anuncioRepository.findByVendedorIdAndStatusOrderByCreatedAtDesc(
                    vendedor.getId(),
                    status,
                    pageable
            );
        } else if (status == null) {
            page = anuncioRepository.findByVendedorIdAndModeloContainingIgnoreCaseOrderByCreatedAtDesc(
                    vendedor.getId(),
                    busca,
                    pageable
            );
        } else {
            page = anuncioRepository.findByVendedorIdAndStatusAndModeloContainingIgnoreCaseOrderByCreatedAtDesc(
                    vendedor.getId(),
                    status,
                    busca,
                    pageable
            );
        }

        return page.map(anuncioMapper::toVendedorListResponse);
    }

    @PreAuthorize("hasAnyRole('VENDEDOR','ADMIN')")
    @Transactional(readOnly = true)
    public AnuncioResponse buscarMeuAnuncioPorId(UUID id, CustomUserDetails userDetails) {
        Usuario vendedor = buscarUsuarioAutenticado(userDetails);
        validarPermissaoVendedorOuAdmin(vendedor);

        Anuncio anuncio = anuncioRepository.findDetalhadoByIdAndVendedorId(id, vendedor.getId())
                .orElseThrow(() -> new IllegalArgumentException("Anúncio não encontrado."));

        return anuncioMapper.toResponse(anuncio);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(readOnly = true)
    public AnuncioResponse buscarAnuncioPorIdParaAdmin(UUID id, CustomUserDetails userDetails) {
        Usuario admin = buscarUsuarioAutenticado(userDetails);
        validarAdmin(admin);

        Anuncio anuncio = anuncioRepository.findDetalhadoById(id)
                .orElseThrow(() -> new IllegalArgumentException("Anúncio não encontrado."));

        return anuncioMapper.toResponse(anuncio);
    }

    @PreAuthorize("hasAnyRole('VENDEDOR','ADMIN')")
    @Transactional
    public AnuncioResponse editarParcial(UUID id, EditarAnuncioRequest request, CustomUserDetails userDetails) {
        Usuario vendedor = buscarUsuarioAutenticado(userDetails);
        validarPermissaoVendedorOuAdmin(vendedor);

        Anuncio anuncio = anuncioRepository.findDetalhadoByIdAndVendedorId(id, vendedor.getId())
                .orElseThrow(() -> new IllegalArgumentException("Anúncio não encontrado."));

        aplicarEdicao(anuncio, request);

        anuncio = anuncioRepository.save(anuncio);

        Anuncio anuncioAtualizado = anuncioRepository.findDetalhadoById(anuncio.getId())
                .orElseThrow(() -> new IllegalArgumentException("Anúncio atualizado, mas não foi possível carregá-lo."));

        return anuncioMapper.toResponse(anuncioAtualizado);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public AnuncioResponse editarParcialParaAdmin(UUID id, EditarAnuncioRequest request, CustomUserDetails userDetails) {
        Usuario admin = buscarUsuarioAutenticado(userDetails);
        validarAdmin(admin);

        Anuncio anuncio = anuncioRepository.findDetalhadoById(id)
                .orElseThrow(() -> new IllegalArgumentException("Anúncio não encontrado."));

        aplicarEdicao(anuncio, request);

        anuncio = anuncioRepository.save(anuncio);

        Anuncio anuncioAtualizado = anuncioRepository.findDetalhadoById(anuncio.getId())
                .orElseThrow(() -> new IllegalArgumentException("Anúncio atualizado, mas não foi possível carregá-lo."));

        return anuncioMapper.toResponse(anuncioAtualizado);
    }

    private void aplicarEdicao(Anuncio anuncio, EditarAnuncioRequest request) {
        if (request.getMarca() != null) {
            anuncio.setMarca(buscarMarca(request.getMarca()));
        }

        if (request.getModelo() != null) {
            validarTextoObrigatorio(request.getModelo(), "Modelo não pode ser vazio.");
            anuncio.setModelo(request.getModelo().trim());
        }

        if (request.getPreco() != null) {
            validarPreco(request.getPreco());
            anuncio.setPreco(request.getPreco());
        }

        if (request.getCidade() != null) {
            validarTextoObrigatorio(request.getCidade(), "Cidade não pode ser vazia.");
            anuncio.setCidade(request.getCidade().trim());
        }

        if (request.getTipo() != null) {
            anuncio.setTipo(request.getTipo());
        }

        if (request.getCondicao() != null) {
            anuncio.setCondicao(request.getCondicao());
        }

        if (request.getAno() != null) {
            anuncio.setAno(request.getAno());
        }

        if (request.getQuilometragem() != null) {
            anuncio.setQuilometragem(request.getQuilometragem());
        }

        if (request.getCombustivel() != null) {
            anuncio.setCombustivel(request.getCombustivel());
        }

        if (request.getCambio() != null) {
            anuncio.setCambio(request.getCambio());
        }

        if (request.getFinalChassi() != null) {
            anuncio.setFinalChassi(normalizarTextoOpcional(request.getFinalChassi()));
        }

        if (request.getCor() != null) {
            anuncio.setCor(normalizarTextoOpcional(request.getCor()));
        }

        if (request.getBlindado() != null) {
            anuncio.setBlindado(request.getBlindado());
        }

        if (request.getPlacaVeiculo() != null) {
            anuncio.setPlacaVeiculo(normalizarTextoOpcional(request.getPlacaVeiculo()));
        }

        if (request.getDescricao() != null) {
            validarTextoObrigatorio(request.getDescricao(), "Descrição não pode ser vazia.");
            anuncio.setDescricao(request.getDescricao().trim());
        }

        if (request.getDetalheTecnico() != null) {
            aplicarEdicaoDetalheTecnico(anuncio, request.getDetalheTecnico());
        }
    }

    private void aplicarEdicaoDetalheTecnico(Anuncio anuncio, EditarAnuncioDetalheTecnicoRequest request) {
        AnuncioDetalheTecnico detalhe = anuncio.getDetalheTecnico();

        if (detalhe == null) {
            detalhe = new AnuncioDetalheTecnico();
            anuncio.definirDetalheTecnico(detalhe);
        }

        if (request.getMotorizacao() != null) detalhe.setMotorizacao(normalizarTextoOpcional(request.getMotorizacao()));
        if (request.getCilindros() != null) detalhe.setCilindros(normalizarTextoOpcional(request.getCilindros()));
        if (request.getPotenciaCombinada() != null) detalhe.setPotenciaCombinada(normalizarTextoOpcional(request.getPotenciaCombinada()));
        if (request.getTorqueCombinado() != null) detalhe.setTorqueCombinado(normalizarTextoOpcional(request.getTorqueCombinado()));
        if (request.getTransmissao() != null) detalhe.setTransmissao(normalizarTextoOpcional(request.getTransmissao()));
        if (request.getTracao() != null) detalhe.setTracao(normalizarTextoOpcional(request.getTracao()));
        if (request.getModosConducao() != null) detalhe.setModosConducao(normalizarTextoOpcional(request.getModosConducao()));
        if (request.getCarroceria() != null) detalhe.setCarroceria(normalizarTextoOpcional(request.getCarroceria()));
        if (request.getComprimentoLarguraAltura() != null) detalhe.setComprimentoLarguraAltura(normalizarTextoOpcional(request.getComprimentoLarguraAltura()));
        if (request.getEntreEixos() != null) detalhe.setEntreEixos(normalizarTextoOpcional(request.getEntreEixos()));
        if (request.getPortaMalas() != null) detalhe.setPortaMalas(normalizarTextoOpcional(request.getPortaMalas()));
        if (request.getTanqueCombustivel() != null) detalhe.setTanqueCombustivel(normalizarTextoOpcional(request.getTanqueCombustivel()));
        if (request.getCiclosUrbano() != null) detalhe.setCiclosUrbano(normalizarTextoOpcional(request.getCiclosUrbano()));
        if (request.getUsoModoEletrico() != null) detalhe.setUsoModoEletrico(normalizarTextoOpcional(request.getUsoModoEletrico()));
        if (request.getEmissoesSeloEficiencia() != null) detalhe.setEmissoesSeloEficiencia(normalizarTextoOpcional(request.getEmissoesSeloEficiencia()));
        if (request.getFreiosDianteiros() != null) detalhe.setFreiosDianteiros(normalizarTextoOpcional(request.getFreiosDianteiros()));
        if (request.getSuspensaoDianteira() != null) detalhe.setSuspensaoDianteira(normalizarTextoOpcional(request.getSuspensaoDianteira()));
        if (request.getSuspensaoTraseira() != null) detalhe.setSuspensaoTraseira(normalizarTextoOpcional(request.getSuspensaoTraseira()));
        if (request.getMedidaPneus() != null) detalhe.setMedidaPneus(normalizarTextoOpcional(request.getMedidaPneus()));
        if (request.getEstepe() != null) detalhe.setEstepe(normalizarTextoOpcional(request.getEstepe()));
        if (request.getAirbags() != null) detalhe.setAirbags(normalizarTextoOpcional(request.getAirbags()));
        if (request.getAbsDistribuicaoEletronica() != null) detalhe.setAbsDistribuicaoEletronica(normalizarTextoOpcional(request.getAbsDistribuicaoEletronica()));
        if (request.getControleEstabilidadeTracao() != null) detalhe.setControleEstabilidadeTracao(normalizarTextoOpcional(request.getControleEstabilidadeTracao()));
        if (request.getAssistentePartidaRampa() != null) detalhe.setAssistentePartidaRampa(normalizarTextoOpcional(request.getAssistentePartidaRampa()));
        if (request.getCameraSensoresEstacionamento() != null) detalhe.setCameraSensoresEstacionamento(normalizarTextoOpcional(request.getCameraSensoresEstacionamento()));
        if (request.getArCondicionadoClimatizador() != null) detalhe.setArCondicionadoClimatizador(normalizarTextoOpcional(request.getArCondicionadoClimatizador()));
        if (request.getDirecao() != null) detalhe.setDirecao(normalizarTextoOpcional(request.getDirecao()));
        if (request.getBancosVolante() != null) detalhe.setBancosVolante(normalizarTextoOpcional(request.getBancosVolante()));
        if (request.getMultimidiaConectividade() != null) detalhe.setMultimidiaConectividade(normalizarTextoOpcional(request.getMultimidiaConectividade()));
        if (request.getRodasIluminacao() != null) detalhe.setRodasIluminacao(normalizarTextoOpcional(request.getRodasIluminacao()));
        if (request.getVidrosTravas() != null) detalhe.setVidrosTravas(normalizarTextoOpcional(request.getVidrosTravas()));
        if (request.getProcedenciaNulances() != null) detalhe.setProcedenciaNulances(normalizarTextoOpcional(request.getProcedenciaNulances()));
        if (request.getLicenciamentoDebitos() != null) detalhe.setLicenciamentoDebitos(normalizarTextoOpcional(request.getLicenciamentoDebitos()));
        if (request.getRestricoesGravame() != null) detalhe.setRestricoesGravame(normalizarTextoOpcional(request.getRestricoesGravame()));
        if (request.getChavesManual() != null) detalhe.setChavesManual(normalizarTextoOpcional(request.getChavesManual()));
        if (request.getLaudoCautelarInspecao() != null) detalhe.setLaudoCautelarInspecao(normalizarTextoOpcional(request.getLaudoCautelarInspecao()));
    }

    private void validarPermissaoVendedorOuAdmin(Usuario usuario) {
        if (usuario.getRole() != UserRole.VENDEDOR && usuario.getRole() != UserRole.ADMIN) {
            throw new IllegalArgumentException("Somente vendedores ou administradores podem acessar anúncios.");
        }
    }

    private void validarAdmin(Usuario usuario) {
        if (usuario.getRole() != UserRole.ADMIN) {
            throw new IllegalArgumentException("Somente administradores podem acessar anúncios de administração.");
        }
    }

    private Usuario buscarUsuarioAutenticado(CustomUserDetails userDetails) {
        return usuarioRepository.findById(userDetails.getId())
                .orElseThrow(() -> new IllegalArgumentException("Usuário autenticado não encontrado."));
    }

    private Marca buscarMarca(MarcaVeiculo marcaVeiculo) {
        if (marcaVeiculo == null) {
            throw new IllegalArgumentException("Marca é obrigatória.");
        }

        return marcaRepository.findByNome(marcaVeiculo)
                .orElseThrow(() -> new IllegalArgumentException("Marca não encontrada."));
    }

    private AnuncioDetalheTecnico criarDetalheTecnico(AnuncioDetalheTecnicoRequest request) {
        AnuncioDetalheTecnico detalhe = new AnuncioDetalheTecnico();
        detalhe.setMotorizacao(trim(request.getMotorizacao()));
        detalhe.setCilindros(trim(request.getCilindros()));
        detalhe.setPotenciaCombinada(trim(request.getPotenciaCombinada()));
        detalhe.setTorqueCombinado(trim(request.getTorqueCombinado()));
        detalhe.setTransmissao(trim(request.getTransmissao()));
        detalhe.setTracao(trim(request.getTracao()));
        detalhe.setModosConducao(trim(request.getModosConducao()));
        detalhe.setCarroceria(trim(request.getCarroceria()));
        detalhe.setComprimentoLarguraAltura(trim(request.getComprimentoLarguraAltura()));
        detalhe.setEntreEixos(trim(request.getEntreEixos()));
        detalhe.setPortaMalas(trim(request.getPortaMalas()));
        detalhe.setTanqueCombustivel(trim(request.getTanqueCombustivel()));
        detalhe.setCiclosUrbano(trim(request.getCiclosUrbano()));
        detalhe.setUsoModoEletrico(trim(request.getUsoModoEletrico()));
        detalhe.setEmissoesSeloEficiencia(trim(request.getEmissoesSeloEficiencia()));
        detalhe.setFreiosDianteiros(trim(request.getFreiosDianteiros()));
        detalhe.setSuspensaoDianteira(trim(request.getSuspensaoDianteira()));
        detalhe.setSuspensaoTraseira(trim(request.getSuspensaoTraseira()));
        detalhe.setMedidaPneus(trim(request.getMedidaPneus()));
        detalhe.setEstepe(trim(request.getEstepe()));
        detalhe.setAirbags(trim(request.getAirbags()));
        detalhe.setAbsDistribuicaoEletronica(trim(request.getAbsDistribuicaoEletronica()));
        detalhe.setControleEstabilidadeTracao(trim(request.getControleEstabilidadeTracao()));
        detalhe.setAssistentePartidaRampa(trim(request.getAssistentePartidaRampa()));
        detalhe.setCameraSensoresEstacionamento(trim(request.getCameraSensoresEstacionamento()));
        detalhe.setArCondicionadoClimatizador(trim(request.getArCondicionadoClimatizador()));
        detalhe.setDirecao(trim(request.getDirecao()));
        detalhe.setBancosVolante(trim(request.getBancosVolante()));
        detalhe.setMultimidiaConectividade(trim(request.getMultimidiaConectividade()));
        detalhe.setRodasIluminacao(trim(request.getRodasIluminacao()));
        detalhe.setVidrosTravas(trim(request.getVidrosTravas()));
        detalhe.setProcedenciaNulances(trim(request.getProcedenciaNulances()));
        detalhe.setLicenciamentoDebitos(trim(request.getLicenciamentoDebitos()));
        detalhe.setRestricoesGravame(trim(request.getRestricoesGravame()));
        detalhe.setChavesManual(trim(request.getChavesManual()));
        detalhe.setLaudoCautelarInspecao(trim(request.getLaudoCautelarInspecao()));
        return detalhe;
    }

    private void validarTextoObrigatorio(String valor, String mensagem) {
        if (valor.isBlank()) {
            throw new IllegalArgumentException(mensagem);
        }
    }

    private void validarPreco(BigDecimal preco) {
        if (preco.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Preço deve ser maior que zero.");
        }
    }

    private String trim(String valor) {
        return valor == null ? null : valor.trim();
    }

    private String normalizarBusca(String busca) {
        if (busca == null || busca.isBlank()) {
            return null;
        }
        return busca.trim();
    }

    private String normalizarTextoOpcional(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        return valor.trim();
    }
}