package com.Nulances.service.disponibilidade;

import com.Nulances.dto.response.DisponibilidadeCadastroResponse;
import com.Nulances.helpers.CpfHelper;
import com.Nulances.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UsuarioDisponibilidadeService {

    private final UsuarioRepository usuarioRepository;

    @Transactional(readOnly = true)
    public DisponibilidadeCadastroResponse verificarDisponibilidade(String email, String cpf, String telefone) {
        String emailNormalizado = normalizarEmail(email);
        String cpfNormalizado = normalizarCpf(cpf);
        String telefoneNormalizado = normalizarTelefone(telefone);

        boolean emailDisponivel = true;
        boolean cpfDisponivel = true;
        boolean telefoneDisponivel = true;

        String mensagemEmail = null;
        String mensagemCpf = null;
        String mensagemTelefone = null;

        if (emailNormalizado != null) {
            emailDisponivel = !usuarioRepository.existsByEmail(emailNormalizado);
            if (!emailDisponivel) {
                mensagemEmail = "E-mail já registrado";
            }
        }

        if (cpfNormalizado != null) {
            cpfDisponivel = !usuarioRepository.existsByCpf(cpfNormalizado);
            if (!cpfDisponivel) {
                mensagemCpf = "CPF já registrado";
            }
        }

        if (telefoneNormalizado != null) {
            telefoneDisponivel = !usuarioRepository.existsByTelefone(telefoneNormalizado);
            if (!telefoneDisponivel) {
                mensagemTelefone = "Telefone já registrado";
            }
        }

        return new DisponibilidadeCadastroResponse(
                emailDisponivel,
                cpfDisponivel,
                telefoneDisponivel,
                mensagemEmail,
                mensagemCpf,
                mensagemTelefone
        );
    }

    private String normalizarEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return null;
        }
        return email.trim().toLowerCase();
    }

    private String normalizarTelefone(String telefone) {
        if (telefone == null || telefone.trim().isEmpty()) {
            return null;
        }
        return telefone.replaceAll("\\D", "");
    }

    private String normalizarCpf(String cpf) {
        if (cpf == null || cpf.trim().isEmpty()) {
            return null;
        }
        return CpfHelper.normalizar(cpf);
    }
}