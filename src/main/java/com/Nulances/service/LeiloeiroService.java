package com.Nulances.service;

import com.Nulances.domain.entity.Leiloeiro;
import com.Nulances.dto.request.CriarLeiloeiroRequest;
import com.Nulances.dto.request.EditarLeiloeiroRequest;
import com.Nulances.dto.response.LeiloeiroListResponse;
import com.Nulances.dto.response.LeiloeiroResponse;
import com.Nulances.dto.response.LeiloeiroStatsResponse;
import com.Nulances.mapper.LeiloeiroMapper;
import com.Nulances.repository.LeiloeiroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LeiloeiroService {

    private final LeiloeiroRepository leiloeiroRepository;
    private final LeiloeiroMapper leiloeiroMapper;

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public LeiloeiroResponse criar(CriarLeiloeiroRequest request) {
        validarDuplicidadesCriacao(request);

        Leiloeiro leiloeiro = new Leiloeiro();
        leiloeiro.setNome(normalizarObrigatorio(request.getNome(), "Nome é obrigatório."));
        leiloeiro.setRegistroProfissional(normalizarObrigatorio(request.getRegistroProfissional(), "Registro profissional é obrigatório."));
        leiloeiro.setCpf(normalizarCpf(request.getCpf()));
        leiloeiro.setEmail(normalizarEmail(request.getEmail()));
        leiloeiro.setTelefone(normalizarOpcional(request.getTelefone()));
        leiloeiro.setLocal(normalizarOpcional(request.getLocal()));
        leiloeiro.setAtivoPlataforma(true);

        leiloeiro = leiloeiroRepository.save(leiloeiro);

        return leiloeiroMapper.toResponse(leiloeiro, 0L);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(readOnly = true)
    public List<LeiloeiroListResponse> listar() {
        return leiloeiroRepository.listarComTotalLeiloes();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(readOnly = true)
    public LeiloeiroResponse buscarPorId(UUID id) {
        Leiloeiro leiloeiro = buscarEntidade(id);
        long totalLeiloes = leiloeiroRepository.contarLeiloesPorLeiloeiroId(id);
        return leiloeiroMapper.toResponse(leiloeiro, totalLeiloes);
    }

    @Transactional(readOnly = true)
    public LeiloeiroStatsResponse listarStatsLeiloeiro() {
        List<Object[]> result = leiloeiroRepository.buscarStatsLeiloeiros();

        Object[] row = result.get(0);

        LeiloeiroStatsResponse response = new LeiloeiroStatsResponse();
        response.setTotalLeiloeiros(((Number) row[0]).longValue());
        response.setTotalLeiloeirosAtivosPlataforma(((Number) row[1]).longValue());
        response.setTotalLeiloeirosInativosPlataforma(((Number) row[2]).longValue());
        response.setTotalLeiloeirosComLeilaoVinculado(((Number) row[3]).longValue());

        return response;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public LeiloeiroResponse editar(UUID id, EditarLeiloeiroRequest request) {
        Leiloeiro leiloeiro = buscarEntidade(id);

        if (request.getNome() != null) {
            leiloeiro.setNome(normalizarObrigatorio(request.getNome(), "Nome não pode ser vazio."));
        }

        if (request.getRegistroProfissional() != null) {
            String registro = normalizarObrigatorio(request.getRegistroProfissional(), "Registro profissional não pode ser vazio.");
            if (leiloeiroRepository.existsByRegistroProfissionalAndIdNot(registro, id)) {
                throw new IllegalArgumentException("Já existe um leiloeiro com este registro profissional.");
            }
            leiloeiro.setRegistroProfissional(registro);
        }

        if (request.getCpf() != null) {
            String cpf = normalizarCpf(request.getCpf());
            if (leiloeiroRepository.existsByCpfAndIdNot(cpf, id)) {
                throw new IllegalArgumentException("Já existe um leiloeiro com este CPF.");
            }
            leiloeiro.setCpf(cpf);
        }

        if (request.getEmail() != null) {
            String email = normalizarEmail(request.getEmail());
            if (leiloeiroRepository.existsByEmailAndIdNot(email, id)) {
                throw new IllegalArgumentException("Já existe um leiloeiro com este email.");
            }
            leiloeiro.setEmail(email);
        }

        if (request.getTelefone() != null) {
            leiloeiro.setTelefone(normalizarOpcional(request.getTelefone()));
        }

        if (request.getLocal() != null) {
            leiloeiro.setLocal(normalizarOpcional(request.getLocal()));
        }

        if (request.getAtivoPlataforma() != null) {
            leiloeiro.setAtivoPlataforma(request.getAtivoPlataforma());
        }

        leiloeiro = leiloeiroRepository.save(leiloeiro);
        long totalLeiloes = leiloeiroRepository.contarLeiloesPorLeiloeiroId(leiloeiro.getId());

        return leiloeiroMapper.toResponse(leiloeiro, totalLeiloes);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void excluir(UUID id) {
        Leiloeiro leiloeiro = buscarEntidade(id);

        long totalLeiloes = leiloeiroRepository.contarLeiloesPorLeiloeiroId(id);
        if (totalLeiloes > 0) {
            throw new IllegalStateException("Não é possível excluir um leiloeiro vinculado a leilões.");
        }

        leiloeiroRepository.delete(leiloeiro);
    }

    private Leiloeiro buscarEntidade(UUID id) {
        return leiloeiroRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Leiloeiro não encontrado."));
    }

    private void validarDuplicidadesCriacao(CriarLeiloeiroRequest request) {
        String registro = normalizarObrigatorio(request.getRegistroProfissional(), "Registro profissional é obrigatório.");
        String cpf = normalizarCpf(request.getCpf());
        String email = normalizarEmail(request.getEmail());

        if (leiloeiroRepository.existsByRegistroProfissional(registro)) {
            throw new IllegalArgumentException("Já existe um leiloeiro com este registro profissional.");
        }

        if (leiloeiroRepository.existsByCpf(cpf)) {
            throw new IllegalArgumentException("Já existe um leiloeiro com este CPF.");
        }

        if (leiloeiroRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Já existe um leiloeiro com este email.");
        }
    }

    private String normalizarObrigatorio(String valor, String mensagemErro) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(mensagemErro);
        }
        return valor.trim();
    }

    private String normalizarOpcional(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        return valor.trim();
    }

    private String normalizarEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email é obrigatório.");
        }
        return email.trim().toLowerCase();
    }

    private String normalizarCpf(String cpf) {
        if (cpf == null || cpf.isBlank()) {
            throw new IllegalArgumentException("CPF é obrigatório.");
        }

        String somenteDigitos = cpf.replaceAll("\\D", "");
        if (somenteDigitos.length() != 11) {
            throw new IllegalArgumentException("CPF inválido.");
        }

        return somenteDigitos;
    }
}