package com.Nulances.service;

import com.Nulances.config.AppProperties;
import com.Nulances.domain.entity.Usuario;
import com.Nulances.domain.entity.UsuarioConfirmacaoEmail;
import com.Nulances.dto.messaging.EmailConfirmacaoMessage;
import com.Nulances.exception.BusinessException;
import com.Nulances.helpers.CodigoHelper;
import com.Nulances.messaging.publisher.EmailConfirmacaoPublisher;
import com.Nulances.repository.UsuarioConfirmacaoEmailRepository;
import com.Nulances.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class ConfirmacaoEmailService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioConfirmacaoEmailRepository confirmacaoEmailRepository;
    private final EmailConfirmacaoPublisher emailConfirmacaoPublisher;
    private final AppProperties appProperties;

    @Transactional
    public void gerarCodigoEEnfileirarEnvio(Usuario usuario) {
        if (Boolean.TRUE.equals(usuario.getEmailVerificado())) {
            return;
        }

        UsuarioConfirmacaoEmail confirmacaoExistente = buscarCodigoPendente(usuario);

        if (confirmacaoExistente != null && confirmacaoExistente.getExpiraEm().isAfter(Instant.now())) {
            return;
        }

        String codigo = CodigoHelper.gerarCodigo6Digitos();

        UsuarioConfirmacaoEmail confirmacao = new UsuarioConfirmacaoEmail();
        confirmacao.setUsuario(usuario);
        confirmacao.setCodigo(codigo);
        confirmacao.setExpiraEm(
                Instant.now().plusSeconds(appProperties.email().confirmacao().expiracaoMinutos() * 60L)
        );
        confirmacao.setUsado(false);
        confirmacao.setUsadoEm(null);
        confirmacao.setTentativas(0);

        confirmacaoEmailRepository.save(confirmacao);

        emailConfirmacaoPublisher.publicar(
                new EmailConfirmacaoMessage(
                        usuario.getId(),
                        usuario.getEmail(),
                        usuario.getNomeCompleto(),
                        codigo
                )
        );
    }

    @Transactional
    public void confirmarEmail(String email, String codigo) {
        String emailNormalizado = email.trim().toLowerCase();

        Usuario usuario = usuarioRepository.findByEmail(emailNormalizado)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado"));

        if (Boolean.TRUE.equals(usuario.getEmailVerificado())) {
            return;
        }

        UsuarioConfirmacaoEmail confirmacao = confirmacaoEmailRepository
                .findTopByUsuarioIdAndCodigoAndUsadoFalseOrderByCreatedAtDesc(usuario.getId(), codigo)
                .orElseThrow(() -> new BusinessException("Código inválido"));

        if (confirmacao.getExpiraEm().isBefore(Instant.now())) {
            throw new BusinessException("Código expirado");
        }

        confirmacao.setUsado(true);
        confirmacao.setUsadoEm(Instant.now());

        usuario.setEmailVerificado(true);
        usuario.setEmailVerificadoEm(Instant.now());

        confirmacaoEmailRepository.save(confirmacao);
        usuarioRepository.save(usuario);
    }

    @Transactional
    public void reenviarCodigo(String email) {
        String emailNormalizado = email.trim().toLowerCase();

        Usuario usuario = usuarioRepository.findByEmail(emailNormalizado)
                .orElseThrow(() -> new BadCredentialsException("Usuário não encontrado"));

        if (Boolean.TRUE.equals(usuario.getEmailVerificado())) {
            return;
        }

        UsuarioConfirmacaoEmail confirmacaoExistente = buscarCodigoPendente(usuario);

        if (confirmacaoExistente != null && confirmacaoExistente.getExpiraEm().isAfter(Instant.now())) {
            throw new BusinessException("Já existe um código de confirmação válido enviado para este e-mail. Aguarde a expiração para solicitar outro.");
        }

        String codigo = CodigoHelper.gerarCodigo6Digitos();

        UsuarioConfirmacaoEmail confirmacao = new UsuarioConfirmacaoEmail();
        confirmacao.setUsuario(usuario);
        confirmacao.setCodigo(codigo);
        confirmacao.setExpiraEm(
                Instant.now().plusSeconds(appProperties.email().confirmacao().expiracaoMinutos() * 60L)
        );
        confirmacao.setUsado(false);
        confirmacao.setUsadoEm(null);
        confirmacao.setTentativas(0);

        confirmacaoEmailRepository.save(confirmacao);

        emailConfirmacaoPublisher.publicar(
                new EmailConfirmacaoMessage(
                        usuario.getId(),
                        usuario.getEmail(),
                        usuario.getNomeCompleto(),
                        codigo
                )
        );
    }

    @Transactional(readOnly = true)
    public boolean possuiCodigoValidoPendente(Usuario usuario) {
        UsuarioConfirmacaoEmail confirmacaoExistente = buscarCodigoPendente(usuario);
        return confirmacaoExistente != null && confirmacaoExistente.getExpiraEm().isAfter(Instant.now());
    }

    @Transactional(readOnly = true)
    protected UsuarioConfirmacaoEmail buscarCodigoPendente(Usuario usuario) {
        return confirmacaoEmailRepository
                .findTopByUsuarioIdAndUsadoFalseOrderByCreatedAtDesc(usuario.getId())
                .orElse(null);
    }
}