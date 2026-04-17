package com.Nulances.service;

import com.Nulances.domain.entity.DocumentoSolicitacaoVendedor;
import com.Nulances.domain.entity.SolicitacaoVendedor;
import com.Nulances.domain.entity.Usuario;
import com.Nulances.domain.enums.*;
import com.Nulances.dto.request.AnalisarSolicitacaoVendedorRequest;
import com.Nulances.dto.request.SolicitarAcessoVendedorRequest;
import com.Nulances.dto.response.SolicitacaoVendedorListResponse;
import com.Nulances.dto.response.SolicitacaoVendedorResponse;
import com.Nulances.mapper.SolicitacaoVendedorMapper;
import com.Nulances.repository.SolicitacaoVendedorRepository;
import com.Nulances.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SolicitacaoVendedorService {

    private final SolicitacaoVendedorRepository repository;
    private final UsuarioRepository usuarioRepository;
    private final SolicitacaoVendedorMapper mapper;

    @PreAuthorize("hasRole('COMUM')")
    @Transactional
    public SolicitacaoVendedorResponse solicitar(Usuario usuario, SolicitarAcessoVendedorRequest request) {
        validarPermissaoSolicitacao(usuario);
        validar(request);

        SolicitacaoVendedor solicitacao = repository.findByUsuario_Id(usuario.getId())
                .orElseGet(SolicitacaoVendedor::new);

        if (solicitacao.getId() != null && solicitacao.getStatus() == StatusSolicitacaoVendedor.PENDENTE) {
            throw new IllegalArgumentException("Já existe uma solicitação pendente.");
        }

        solicitacao.setUsuario(usuario);
        solicitacao.setTipoPessoa(request.getTipoPessoa());
        solicitacao.setStatus(StatusSolicitacaoVendedor.PENDENTE);

        solicitacao.setCpf(request.getTipoPessoa() == TipoPessoaVendedor.PESSOA_FISICA ? limpar(request.getCpf()) : null);
        solicitacao.setCnpj(request.getTipoPessoa() == TipoPessoaVendedor.PESSOA_JURIDICA ? limpar(request.getCnpj()) : null);

        solicitacao.setNomeCompleto(request.getTipoPessoa() == TipoPessoaVendedor.PESSOA_FISICA ? trim(request.getNomeCompleto()) : null);
        solicitacao.setRazaoSocial(request.getTipoPessoa() == TipoPessoaVendedor.PESSOA_JURIDICA ? trim(request.getRazaoSocial()) : null);

        solicitacao.setEmail(trim(request.getEmail()));
        solicitacao.setTelefone(trim(request.getTelefone()));
        solicitacao.setCidade(trim(request.getCidade()));
        solicitacao.setEstado(trim(request.getEstado()).toUpperCase());
        solicitacao.setInformacoesNegocio(trim(request.getInformacoesNegocio()));

        solicitacao.setObservacaoAdmin(null);
        solicitacao.setAnalisadoEm(null);
        solicitacao.setAnalisadoPor(null);

        solicitacao.getDocumentos().clear();
        adicionarDocs(solicitacao, request);

        return mapper.toResponse(repository.save(solicitacao));
    }

    @Transactional(readOnly = true)
    public SolicitacaoVendedorResponse buscarMinhaSolicitacao(Usuario usuario) {
        SolicitacaoVendedor solicitacao = repository.findByUsuario_Id(usuario.getId())
                .orElseThrow(() -> new IllegalArgumentException("Solicitação não encontrada."));

        return mapper.toResponse(solicitacao);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(readOnly = true)
    public List<SolicitacaoVendedorListResponse> listarTodas(StatusSolicitacaoVendedor status) {
        List<SolicitacaoVendedor> lista = status != null
                ? repository.findAllByStatusOrderByCreatedAtDesc(status)
                : repository.findAllByOrderByCreatedAtDesc();

        return lista.stream()
                .map(mapper::toListResponse)
                .toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(readOnly = true)
    public SolicitacaoVendedorResponse buscarPorId(UUID id) {
        SolicitacaoVendedor solicitacao = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Solicitação não encontrada."));

        return mapper.toResponse(solicitacao);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public SolicitacaoVendedorResponse aprovar(UUID id, Usuario admin) {
        SolicitacaoVendedor solicitacao = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Solicitação não encontrada."));

        if (solicitacao.getStatus() != StatusSolicitacaoVendedor.PENDENTE) {
            throw new IllegalArgumentException("Somente solicitações pendentes podem ser aprovadas.");
        }

        Usuario usuario = solicitacao.getUsuario();
        usuario.setRole(UserRole.VENDEDOR);
        usuarioRepository.save(usuario);

        solicitacao.setStatus(StatusSolicitacaoVendedor.APROVADA);
        solicitacao.setAnalisadoEm(Instant.now());
        solicitacao.setAnalisadoPor(admin);

        return mapper.toResponse(repository.save(solicitacao));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public SolicitacaoVendedorResponse recusar(UUID id, AnalisarSolicitacaoVendedorRequest request, Usuario admin) {
        SolicitacaoVendedor solicitacao = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Solicitação não encontrada."));

        if (solicitacao.getStatus() != StatusSolicitacaoVendedor.PENDENTE) {
            throw new IllegalArgumentException("Somente solicitações pendentes podem ser recusadas.");
        }

        solicitacao.setStatus(StatusSolicitacaoVendedor.RECUSADA);
        solicitacao.setObservacaoAdmin(trim(request.getObservacao()));
        solicitacao.setAnalisadoEm(Instant.now());
        solicitacao.setAnalisadoPor(admin);

        return mapper.toResponse(repository.save(solicitacao));
    }

    private void validarPermissaoSolicitacao(Usuario usuario) {
        if (usuario.getRole() != UserRole.COMUM) {
            throw new IllegalArgumentException("Somente usuários com perfil COMUM podem solicitar acesso para vendedor.");
        }
    }

    private void adicionarDocs(SolicitacaoVendedor s, SolicitarAcessoVendedorRequest r) {
        List<DocumentoSolicitacaoVendedor> docs = new ArrayList<>();

        if (r.getTipoPessoa() == TipoPessoaVendedor.PESSOA_FISICA) {
            docs.add(doc(s, TipoDocumentoSolicitacaoVendedor.RG_FRENTE, r.getRgFrenteKey()));
            docs.add(doc(s, TipoDocumentoSolicitacaoVendedor.RG_VERSO, r.getRgVersoKey()));
            docs.add(doc(s, TipoDocumentoSolicitacaoVendedor.CPF_FRENTE, r.getCpfFrenteKey()));
            docs.add(doc(s, TipoDocumentoSolicitacaoVendedor.CPF_VERSO, r.getCpfVersoKey()));
        } else {
            docs.add(doc(s, TipoDocumentoSolicitacaoVendedor.SELFIE_COM_DOCUMENTO, r.getSelfieComDocumentoKey()));
            docs.add(doc(s, TipoDocumentoSolicitacaoVendedor.CONTRATO_SOCIAL, r.getContratoSocialKey()));
        }

        s.getDocumentos().addAll(docs);
    }

    private DocumentoSolicitacaoVendedor doc(
            SolicitacaoVendedor s,
            TipoDocumentoSolicitacaoVendedor tipo,
            String key
    ) {
        if (key == null || key.isBlank()) {
            throw new IllegalArgumentException("Documento obrigatório.");
        }

        DocumentoSolicitacaoVendedor d = new DocumentoSolicitacaoVendedor();
        d.setSolicitacao(s);
        d.setTipo(tipo);
        d.setArquivo(trim(key));
        return d;
    }

    private void validar(SolicitarAcessoVendedorRequest r) {
        if (r.getTipoPessoa() == null) {
            throw new IllegalArgumentException("Tipo de pessoa é obrigatório.");
        }

        validarTexto(r.getEmail(), "E-mail é obrigatório.");
        validarTexto(r.getTelefone(), "Telefone é obrigatório.");
        validarTexto(r.getCidade(), "Cidade é obrigatória.");
        validarTexto(r.getEstado(), "Estado é obrigatório.");
        validarTexto(r.getInformacoesNegocio(), "Informações sobre o negócio são obrigatórias.");

        if (trim(r.getEstado()).length() != 2) {
            throw new IllegalArgumentException("Estado deve conter 2 caracteres.");
        }

        if (r.getTipoPessoa() == TipoPessoaVendedor.PESSOA_FISICA) {
            validarTexto(r.getCpf(), "CPF é obrigatório para pessoa física.");
            validarTexto(r.getNomeCompleto(), "Nome completo é obrigatório para pessoa física.");
            validarTexto(r.getRgFrenteKey(), "RG frente é obrigatório.");
            validarTexto(r.getRgVersoKey(), "RG verso é obrigatório.");
            validarTexto(r.getCpfFrenteKey(), "CPF frente é obrigatório.");
            validarTexto(r.getCpfVersoKey(), "CPF verso é obrigatório.");
        } else {
            validarTexto(r.getCnpj(), "CNPJ é obrigatório para pessoa jurídica.");
            validarTexto(r.getRazaoSocial(), "Razão social é obrigatória para pessoa jurídica.");
            validarTexto(r.getSelfieComDocumentoKey(), "Selfie com documento é obrigatória.");
            validarTexto(r.getContratoSocialKey(), "Contrato social é obrigatório.");
        }
    }

    private void validarTexto(String valor, String mensagem) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(mensagem);
        }
    }

    private String trim(String valor) {
        return valor == null ? null : valor.trim();
    }

    private String limpar(String valor) {
        return valor == null ? null : valor.replaceAll("\\D", "");
    }
}