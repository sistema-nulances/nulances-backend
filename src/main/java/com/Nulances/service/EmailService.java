package com.Nulances.service;

import com.Nulances.config.ResendProperties;
import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.time.Year;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final ResendProperties resendProperties;

    public String enviarCodigoConfirmacao(String destinatario, String nomeUsuario, String codigo) {
        validarConfiguracao();

        String html = carregarTemplate("templates/confirmar-email.html")
                .replace("{{nome}}", escapeHtml(nomeUsuario))
                .replace("{{codigo}}", escapeHtml(codigo))
                .replace("{{ano}}", String.valueOf(Year.now().getValue()))
                .replace("{{empresa}}", "Nulances");

        Resend resend = new Resend(resendProperties.apiKey());

        CreateEmailOptions params = CreateEmailOptions.builder()
                .from(montarFrom())
                .to(destinatario)
                .subject("Confirmação de e-mail")
                .html(html)
                .build();

        try {
            CreateEmailResponse response = resend.emails().send(params);
            return response.getId();
        } catch (ResendException ex) {
            throw new IllegalStateException("Erro ao enviar e-mail de confirmação", ex);
        }
    }

    public String enviarCodigoRecuperacaoSenha(String destinatario, String nomeUsuario, String codigo) {
        validarConfiguracao();

        String html = carregarTemplate("templates/recuperar-senha.html")
                .replace("{{nome}}", escapeHtml(nomeUsuario))
                .replace("{{codigo}}", escapeHtml(codigo))
                .replace("{{ano}}", String.valueOf(java.time.Year.now().getValue()))
                .replace("{{empresa}}", "Nulances");

        Resend resend = new Resend(resendProperties.apiKey());

        CreateEmailOptions params = CreateEmailOptions.builder()
                .from(montarFrom())
                .to(destinatario)
                .subject("Recuperação de senha")
                .html(html)
                .build();

        try {
            CreateEmailResponse response = resend.emails().send(params);
            return response.getId();
        } catch (ResendException ex) {
            throw new IllegalStateException("Erro ao enviar e-mail de recuperação de senha", ex);
        }
    }

    public String enviarCobrancaPlano(
            String destinatario,
            String nomeUsuario,
            String nomePlano,
            BigDecimal valor,
            Instant vencimento,
            String checkoutUrl
    ) {
        validarConfiguracao();

        String html = carregarTemplate("templates/cobranca-plano.html")
                .replace("{{nome}}", escapeHtml(nomeUsuario))
                .replace("{{plano}}", escapeHtml(nomePlano))
                .replace("{{valor}}", escapeHtml(formatarMoeda(valor)))
                .replace("{{vencimento}}", escapeHtml(formatarData(vencimento)))
                .replace("{{checkoutUrl}}", escapeHtml(checkoutUrl))
                .replace("{{ano}}", String.valueOf(Year.now().getValue()))
                .replace("{{empresa}}", "Nulances");

        Resend resend = new Resend(resendProperties.apiKey());

        CreateEmailOptions params = CreateEmailOptions.builder()
                .from(montarFrom())
                .to(destinatario)
                .subject("Renovação do plano de anúncios")
                .html(html)
                .build();

        try {
            CreateEmailResponse response = resend.emails().send(params);
            return response.getId();
        } catch (ResendException ex) {
            throw new IllegalStateException("Erro ao enviar e-mail de cobrança do plano", ex);
        }
    }

    private String carregarTemplate(String path) {
        try {
            ClassPathResource resource = new ClassPathResource(path);
            byte[] bytes = resource.getInputStream().readAllBytes();
            return new String(bytes, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            throw new IllegalStateException("Não foi possível carregar o template de e-mail: " + path, ex);
        }
    }

    private void validarConfiguracao() {
        if (resendProperties.apiKey() == null || resendProperties.apiKey().isBlank()) {
            throw new IllegalStateException("RESEND_API_KEY não configurada");
        }

        if (resendProperties.from() == null
                || resendProperties.from().email() == null
                || resendProperties.from().email().isBlank()) {
            throw new IllegalStateException("RESEND_FROM_EMAIL não configurado");
        }
    }

    private String montarFrom() {
        String email = resendProperties.from().email();
        String nome = resendProperties.from().name();

        if (nome == null || nome.isBlank()) {
            return email;
        }

        return nome + " <" + email + ">";
    }

    private String escapeHtml(String value) {
        if (value == null) {
            return "";
        }

        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    private String formatarMoeda(BigDecimal valor) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        return formatter.format(valor != null ? valor : BigDecimal.ZERO);
    }

    private String formatarData(Instant data) {
        if (data == null) {
            return "-";
        }

        return DateTimeFormatter.ofPattern("dd/MM/yyyy")
                .withZone(ZoneId.of("America/Sao_Paulo"))
                .format(data);
    }
}