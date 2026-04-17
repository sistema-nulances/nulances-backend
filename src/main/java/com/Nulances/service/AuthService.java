package com.Nulances.service;

import com.Nulances.config.security.CustomUserDetails;
import com.Nulances.config.security.JwtService;
import com.Nulances.domain.entity.DocumentoValidacao;
import com.Nulances.domain.entity.DocumentoVendedor;
import com.Nulances.domain.entity.Usuario;
import com.Nulances.domain.entity.UsuarioRecuperacaoSenha;
import com.Nulances.domain.enums.UserRole;
import com.Nulances.dto.request.*;
import com.Nulances.dto.response.LoginResponse;
import com.Nulances.dto.response.MeResponse;
import com.Nulances.dto.response.RegisterResponse;
import com.Nulances.dto.response.UploadFotoPerfilResponse;
import com.Nulances.exception.CpfJaCadastradoException;
import com.Nulances.exception.EmailJaCadastradoException;
import com.Nulances.exception.EmailNaoVerificadoException;
import com.Nulances.helpers.CodigoHelper;
import com.Nulances.helpers.CpfHelper;
import com.Nulances.helpers.StringHelper;
import com.Nulances.mapper.AuthMapper;
import com.Nulances.repository.DocumentoValidacaoRepository;
import com.Nulances.repository.DocumentoVendedorRepository;
import com.Nulances.repository.UsuarioRecuperacaoSenhaRepository;
import com.Nulances.repository.UsuarioRepository;
import com.Nulances.storage.R2Service;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final Set<String> FOTO_PERFIL_CONTENT_TYPES = Set.of(
            "image/jpeg", "image/jpg", "image/png", "image/webp"
    );
    private static final long FOTO_PERFIL_UPLOAD_EXPIRES_SECONDS = 600L;   // 10 min
    private static final long FOTO_PERFIL_DOWNLOAD_EXPIRES_SECONDS = 3600L; // 1h

    private final JwtService jwtService;
    private final UsuarioRepository usuarioRepository;
    private final DocumentoVendedorRepository documentoVendedorRepository;
    private final DocumentoValidacaoRepository documentoValidacaoRepository;
    private final UsuarioRecuperacaoSenhaRepository usuarioRecuperacaoSenhaRepository;
    private final PasswordEncoder passwordEncoder;
    private final ConfirmacaoEmailService confirmacaoEmailService;
    private final EmailService emailService;
    private final AuthMapper authMapper;
    private final R2Service r2Service;

    @Value("${app.r2.bucket}")
    private String r2Bucket;

    public RegisterResponse register(RegistroRequest request) {
        String email = request.getEmail().trim().toLowerCase();
        String cpf = CpfHelper.normalizar(request.getCpf());

        if (usuarioRepository.existsByEmail(email)) {
            throw new EmailJaCadastradoException();
        }

        if (usuarioRepository.existsByCpf(cpf)) {
            throw new CpfJaCadastradoException();
        }

        Usuario usuario = new Usuario();
        usuario.setNomeCompleto(request.getNomeCompleto().trim());
        usuario.setCpf(cpf);
        usuario.setDataNascimento(request.getDataNascimento());
        usuario.setEmail(email);
        usuario.setSenha(passwordEncoder.encode(request.getSenha()));
        usuario.setRole(UserRole.COMUM);
        usuario.setEmailVerificado(false);

        Usuario usuarioSalvo = usuarioRepository.save(usuario);

        confirmacaoEmailService.gerarCodigoEEnfileirarEnvio(usuarioSalvo);

        return authMapper.toRegisterResponse(usuarioSalvo, "Usuário cadastrado com sucesso");
    }

    public LoginResponse login(LoginRequest request) {
        String email = request.getEmail().trim().toLowerCase();

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new BadCredentialsException("E-mail ou senha inválidos"));

        if (!passwordEncoder.matches(request.getSenha(), usuario.getSenha())) {
            throw new BadCredentialsException("E-mail ou senha inválidos");
        }

        if (!Boolean.TRUE.equals(usuario.getEmailVerificado())) {
            confirmacaoEmailService.gerarCodigoEEnfileirarEnvio(usuario);
            throw new EmailNaoVerificadoException(
                    "Seu e-mail ainda não foi confirmado. Caso não exista um código válido pendente, um novo código foi enviado."
            );
        }

        CustomUserDetails userDetails = new CustomUserDetails(usuario);
        String accessToken = jwtService.generateToken(userDetails);

        return new LoginResponse(
                accessToken,
                "Bearer",
                21600L,
                usuario.getId(),
                usuario.getNomeCompleto(),
                usuario.getEmail(),
                usuario.getRole().name(),
                usuario.getEmailVerificado()
        );
    }

    @Transactional
    public void solicitarRecuperacaoSenha(EsqueciSenhaRequest request) {
        String email = request.getEmail().trim().toLowerCase();

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        UsuarioRecuperacaoSenha recuperacaoExistente = usuarioRecuperacaoSenhaRepository
                .findTopByUsuarioIdAndUsadoFalseOrderByCreatedAtDesc(usuario.getId())
                .orElse(null);

        if (recuperacaoExistente != null && recuperacaoExistente.getExpiraEm().isAfter(Instant.now())) {
            emailService.enviarCodigoRecuperacaoSenha(
                    usuario.getEmail(),
                    usuario.getNomeCompleto(),
                    recuperacaoExistente.getCodigo()
            );
            return;
        }

        String codigo = CodigoHelper.gerarCodigo6Digitos();

        UsuarioRecuperacaoSenha recuperacao = new UsuarioRecuperacaoSenha();
        recuperacao.setUsuario(usuario);
        recuperacao.setCodigo(codigo);
        recuperacao.setExpiraEm(Instant.now().plusSeconds(15 * 60L));
        recuperacao.setUsado(false);
        recuperacao.setUsadoEm(null);
        recuperacao.setTentativas(0);

        usuarioRecuperacaoSenhaRepository.save(recuperacao);

        emailService.enviarCodigoRecuperacaoSenha(
                usuario.getEmail(),
                usuario.getNomeCompleto(),
                codigo
        );
    }

    @Transactional(readOnly = true)
    public void verificarCodigoRecuperacaoSenha(VerificarCodigoRecuperacaoRequest request) {
        String email = request.getEmail().trim().toLowerCase();
        String codigo = request.getCodigo().trim();

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        UsuarioRecuperacaoSenha recuperacao = usuarioRecuperacaoSenhaRepository
                .findTopByUsuarioIdAndUsadoFalseOrderByCreatedAtDesc(usuario.getId())
                .orElseThrow(() -> new IllegalArgumentException("Código inválido"));

        if (recuperacao.getExpiraEm().isBefore(Instant.now())) {
            throw new IllegalArgumentException("Código expirado");
        }

        if (!recuperacao.getCodigo().equals(codigo)) {
            throw new IllegalArgumentException("Código inválido");
        }
    }

    @Transactional
    public void resetarSenha(TrocaSenhaRequest request) {
        String email = request.getEmail().trim().toLowerCase();
        String codigo = request.getCodigo().trim();

        if (!request.getNovaSenha().equals(request.getConfirmarNovaSenha())) {
            throw new IllegalArgumentException("As senhas não coincidem");
        }

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        UsuarioRecuperacaoSenha recuperacao = usuarioRecuperacaoSenhaRepository
                .findTopByUsuarioIdAndCodigoAndUsadoFalseOrderByCreatedAtDesc(usuario.getId(), codigo)
                .orElseThrow(() -> new IllegalArgumentException("Código inválido"));

        if (recuperacao.getExpiraEm().isBefore(Instant.now())) {
            throw new IllegalArgumentException("Código expirado");
        }

        usuario.setSenha(passwordEncoder.encode(request.getNovaSenha()));

        recuperacao.setUsado(true);
        recuperacao.setUsadoEm(Instant.now());

        usuarioRepository.save(usuario);
        usuarioRecuperacaoSenhaRepository.save(recuperacao);
    }

    @Transactional(readOnly = true)
    public MeResponse me(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        Usuario usuario = usuarioRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new BadCredentialsException("Usuário não encontrado"));

        List<DocumentoValidacao> documentosValidacao =
                documentoValidacaoRepository.findByUsuarioId(usuario.getId());

        List<DocumentoVendedor> documentosVendedor =
                documentoVendedorRepository.findByUsuarioId(usuario.getId());

        return montarMeResponseComFotoAssinada(usuario, documentosValidacao, documentosVendedor);
    }

    @Transactional(readOnly = true)
    public UploadFotoPerfilResponse gerarUploadUrlFotoPerfil(
            Authentication authentication,
            GerarUploadFotoPerfilRequest request
    ) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        Usuario usuario = usuarioRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new BadCredentialsException("Usuário não encontrado"));

        String contentType = request.getContentType() == null ? "" : request.getContentType().trim().toLowerCase();
        if (!FOTO_PERFIL_CONTENT_TYPES.contains(contentType)) {
            throw new IllegalArgumentException("Tipo de arquivo inválido para foto de perfil.");
        }

        String nomeArquivoOriginal = request.getNomeArquivo() == null ? "" : request.getNomeArquivo().trim();
        if (nomeArquivoOriginal.isBlank()) {
            throw new IllegalArgumentException("Nome do arquivo é obrigatório.");
        }

        String extensao = extrairExtensao(nomeArquivoOriginal);
        if (extensao.isBlank()) {
            extensao = extensaoPorMime(contentType);
        }

        String objectKey = String.format(
                "usuarios/%s/foto-perfil/%d-%s.%s",
                usuario.getId(),
                Instant.now().toEpochMilli(),
                UUID.randomUUID().toString().replace("-", ""),
                extensao
        );

        String uploadUrl = r2Service.gerarUrlUpload(
                r2Bucket,
                objectKey,
                contentType,
                FOTO_PERFIL_UPLOAD_EXPIRES_SECONDS
        );

        return new UploadFotoPerfilResponse(
                objectKey,
                uploadUrl,
                FOTO_PERFIL_UPLOAD_EXPIRES_SECONDS
        );
    }

    @Transactional
    public MeResponse atualizarPerfil(Authentication authentication, AtualizarPerfilRequest request) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        Usuario usuario = usuarioRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new BadCredentialsException("Usuário não encontrado"));

        if (request.getTelefone() != null) {
            usuario.setTelefone(StringHelper.normalizar(request.getTelefone()));
        }

        // recebe objectKey confirmado pelo front após PUT no R2
        if (request.getFotoPerfil() != null) {
            usuario.setFotoPerfil(StringHelper.normalizar(request.getFotoPerfil()));
        }

        if (request.getCep() != null) {
            usuario.setCep(StringHelper.normalizar(request.getCep()));
        }

        if (request.getLogradouro() != null) {
            usuario.setLogradouro(StringHelper.normalizar(request.getLogradouro()));
        }

        if (request.getCidade() != null) {
            usuario.setCidade(StringHelper.normalizar(request.getCidade()));
        }

        if (request.getEstado() != null) {
            usuario.setEstado(StringHelper.normalizarEstado(request.getEstado()));
        }

        usuarioRepository.save(usuario);

        // retorna já com fotoPerfil assinada para o front atualizar imediatamente
        return me(authentication);
    }

    @Transactional
    public void alterarSenha(Authentication authentication, AlterarSenhaRequest request) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        Usuario usuario = usuarioRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new BadCredentialsException("Usuário não encontrado"));

        if (request.getSenhaAtual() == null || request.getSenhaAtual().isBlank()) {
            throw new IllegalArgumentException("A senha atual é obrigatória");
        }

        if (request.getNovaSenha() == null || request.getNovaSenha().isBlank()) {
            throw new IllegalArgumentException("A nova senha é obrigatória");
        }

        if (request.getConfirmarNovaSenha() == null || request.getConfirmarNovaSenha().isBlank()) {
            throw new IllegalArgumentException("A confirmação da nova senha é obrigatória");
        }

        if (!passwordEncoder.matches(request.getSenhaAtual(), usuario.getSenha())) {
            throw new IllegalArgumentException("A senha atual está incorreta");
        }

        if (!request.getNovaSenha().equals(request.getConfirmarNovaSenha())) {
            throw new IllegalArgumentException("A nova senha e a confirmação não coincidem");
        }

        if (passwordEncoder.matches(request.getNovaSenha(), usuario.getSenha())) {
            throw new IllegalArgumentException("A nova senha não pode ser igual à senha atual");
        }

        usuario.setSenha(passwordEncoder.encode(request.getNovaSenha()));
        usuarioRepository.save(usuario);
    }

    private MeResponse montarMeResponseComFotoAssinada(
            Usuario usuario,
            List<DocumentoValidacao> documentosValidacao,
            List<DocumentoVendedor> documentosVendedor
    ) {
        MeResponse base = authMapper.toMeResponse(usuario, documentosValidacao, documentosVendedor);
        String fotoPerfilResolvida = resolverFotoPerfilParaResposta(base.fotoPerfil());

        return new MeResponse(
                base.id(),
                base.nomeCompleto(),
                base.email(),
                base.telefone(),
                fotoPerfilResolvida,
                base.cpf(),
                base.cep(),
                base.logradouro(),
                base.cidade(),
                base.estado(),
                base.emailVerificado(),
                base.emailVerificadoEm(),
                base.role(),
                base.createdAt(),
                base.updatedAt(),
                base.documentosValidacao(),
                base.documentosVendedor()
        );
    }

    private String resolverFotoPerfilParaResposta(String fotoPerfil) {
        if (fotoPerfil == null || fotoPerfil.isBlank()) return null;

        String valor = fotoPerfil.trim();
        if (valor.startsWith("http://") || valor.startsWith("https://")) {
            return valor;
        }

        // Caso seja objectKey, gera URL de download assinada
        return r2Service.gerarUrlDownload(
                r2Bucket,
                valor,
                FOTO_PERFIL_DOWNLOAD_EXPIRES_SECONDS
        );
    }

    private String extrairExtensao(String nomeArquivo) {
        int idx = nomeArquivo.lastIndexOf('.');
        if (idx < 0 || idx == nomeArquivo.length() - 1) return "";
        return nomeArquivo.substring(idx + 1).trim().toLowerCase();
    }

    private String extensaoPorMime(String contentType) {
        return switch (contentType) {
            case "image/png" -> "png";
            case "image/webp" -> "webp";
            case "image/jpg", "image/jpeg" -> "jpg";
            default -> "jpg";
        };
    }
}