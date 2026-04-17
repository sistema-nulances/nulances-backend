package com.Nulances.service.disponibilidade;

import com.Nulances.dto.response.LeiloeiroDisponibilidadeResponse;
import com.Nulances.repository.LeiloeiroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LeiloeiroDisponibilidadeService {

    private final LeiloeiroRepository leiloeiroRepository;

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(readOnly = true)
    public LeiloeiroDisponibilidadeResponse verificarDisponibilidade(
            String registroProfissional,
            String cpf,
            String email
    ) {
        String registroNormalizado = normalizarObrigatorioOpcional(registroProfissional);
        String cpfNormalizado = normalizarCpfOpcional(cpf);
        String emailNormalizado = normalizarEmailOpcional(email);

        boolean registroDisponivel = true;
        boolean cpfDisponivel = true;
        boolean emailDisponivel = true;

        String mensagemRegistro = null;
        String mensagemCpf = null;
        String mensagemEmail = null;

        if (registroNormalizado != null) {
            registroDisponivel = !leiloeiroRepository.existsByRegistroProfissional(registroNormalizado);
            if (!registroDisponivel) {
                mensagemRegistro = "Registro profissional já registrado";
            }
        }

        if (cpfNormalizado != null) {
            cpfDisponivel = !leiloeiroRepository.existsByCpf(cpfNormalizado);
            if (!cpfDisponivel) {
                mensagemCpf = "CPF já registrado";
            }
        }

        if (emailNormalizado != null) {
            emailDisponivel = !leiloeiroRepository.existsByEmail(emailNormalizado);
            if (!emailDisponivel) {
                mensagemEmail = "E-mail já registrado";
            }
        }

        return new LeiloeiroDisponibilidadeResponse(
                registroDisponivel,
                cpfDisponivel,
                emailDisponivel,
                mensagemRegistro,
                mensagemCpf,
                mensagemEmail
        );
    }

    private String normalizarObrigatorioOpcional(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        return valor.trim();
    }

    private String normalizarEmailOpcional(String email) {
        if (email == null || email.isBlank()) {
            return null;
        }
        return email.trim().toLowerCase();
    }

    private String normalizarCpfOpcional(String cpf) {
        if (cpf == null || cpf.isBlank()) {
            return null;
        }

        String somenteDigitos = cpf.replaceAll("\\D", "");
        return somenteDigitos.isBlank() ? null : somenteDigitos;
    }
}