package com.Nulances.controller;

import com.Nulances.dto.request.*;
import com.Nulances.dto.response.DisponibilidadeCadastroResponse;
import com.Nulances.dto.response.LoginResponse;
import com.Nulances.dto.response.MeResponse;
import com.Nulances.dto.response.RegisterResponse;
import com.Nulances.dto.response.UploadFotoPerfilResponse;
import com.Nulances.service.AuthService;
import com.Nulances.service.ConfirmacaoEmailService;
import com.Nulances.service.disponibilidade.UsuarioDisponibilidadeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final ConfirmacaoEmailService confirmacaoEmailService;
    private final UsuarioDisponibilidadeService usuarioDisponibilidadeService;

    @GetMapping("/disponibilidade")
    public DisponibilidadeCadastroResponse verificarDisponibilidade(
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String cpf,
            @RequestParam(required = false) String telefone
    ) {
        return usuarioDisponibilidadeService.verificarDisponibilidade(email, cpf, telefone);
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegistroRequest request) {
        RegisterResponse response = authService.register(request);
        return ResponseEntity.status(201).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(@RequestBody @Valid EsqueciSenhaRequest request) {
        authService.solicitarRecuperacaoSenha(request);
        return ResponseEntity.ok(Map.of(
                "message", "Código de recuperação enviado com sucesso"
        ));
    }

    @PostMapping("/verificar-codigo-recuperacao")
    public ResponseEntity<Map<String, String>> verificarCodigoRecuperacao(
            @RequestBody @Valid VerificarCodigoRecuperacaoRequest request
    ) {
        authService.verificarCodigoRecuperacaoSenha(request);
        return ResponseEntity.ok(Map.of(
                "message", "Código validado com sucesso"
        ));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(@RequestBody @Valid TrocaSenhaRequest request) {
        authService.resetarSenha(request);
        return ResponseEntity.ok(Map.of(
                "message", "Senha redefinida com sucesso"
        ));
    }

    @GetMapping("/me")
    public ResponseEntity<MeResponse> me(Authentication authentication) {
        MeResponse response = authService.me(authentication);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/me/foto-perfil/upload-url")
    public ResponseEntity<UploadFotoPerfilResponse> gerarUploadUrlFotoPerfil(
            Authentication authentication,
            @Valid @RequestBody GerarUploadFotoPerfilRequest request
    ) {
        UploadFotoPerfilResponse response = authService.gerarUploadUrlFotoPerfil(authentication, request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/me/perfil")
    public MeResponse atualizarPerfil(
            Authentication authentication,
            @RequestBody AtualizarPerfilRequest request
    ) {
        return authService.atualizarPerfil(authentication, request);
    }

    @PatchMapping("/me/senha")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void alterarSenha(
            Authentication authentication,
            @RequestBody AlterarSenhaRequest request
    ) {
        authService.alterarSenha(authentication, request);
    }

    @PostMapping("/confirmar-email")
    public ResponseEntity<Map<String, String>> confirmarEmail(
            @RequestBody @Valid ConfirmarEmailRequest request
    ) {
        confirmacaoEmailService.confirmarEmail(request.getEmail(), request.getCodigo());

        return ResponseEntity.ok(
                Map.of("message", "E-mail confirmado com sucesso")
        );
    }

    @PostMapping("/reenviar-codigo")
    public ResponseEntity<Map<String, String>> reenviarCodigo(
            @RequestBody @Valid ReenviarCodigoRequest request
    ) {
        confirmacaoEmailService.reenviarCodigo(request.getEmail());

        return ResponseEntity.ok(
                Map.of("message", "Código de confirmação reenviado com sucesso")
        );
    }
}