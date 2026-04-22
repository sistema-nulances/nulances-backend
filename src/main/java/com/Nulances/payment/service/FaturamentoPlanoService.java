package com.Nulances.payment.service;

import com.Nulances.domain.entity.PagamentoPlano;
import com.Nulances.domain.entity.WebhookPagamento;
import com.Nulances.domain.enums.StatusPagamentoPlano;
import com.Nulances.dto.response.FaturaPlanoResponse;
import com.Nulances.payment.config.PaymentProperties;
import com.Nulances.repository.PagamentoPlanoRepository;
import com.Nulances.repository.WebhookPagamentoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FaturamentoPlanoService {

    private final PagamentoPlanoRepository pagamentoPlanoRepository;
    private final WebhookPagamentoRepository webhookPagamentoRepository;
    private final AssinaturaPlanoService assinaturaPlanoService;
    private final PaymentProperties paymentProperties;
    private final MercadoPagoCheckoutService mercadoPagoCheckoutService;

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(readOnly = true)
    public List<FaturaPlanoResponse> listarFaturamentoAdmin(UUID vendedorId) {
        List<PagamentoPlano> pagamentos = vendedorId == null
                ? pagamentoPlanoRepository.findAllByOrderByCreatedAtDesc()
                : pagamentoPlanoRepository.findByAssinaturaVendedorIdOrderByCreatedAtDesc(vendedorId);

        return pagamentos.stream().map(this::toResponse).toList();
    }

    @Transactional
    public void processarWebhookMercadoPago(
            Map<String, Object> payload,
            String evento,
            String assinaturaWebhook,
            String requestId,
            String dataId
    ) {
        WebhookPagamento webhook = new WebhookPagamento();
        webhook.setProvider("MERCADO_PAGO");
        webhook.setEvento(evento != null && !evento.isBlank() ? evento : "desconhecido");
        webhook.setPayload(paraJson(payload));
        webhook = webhookPagamentoRepository.save(webhook);

        String paymentId = dataId != null && !dataId.isBlank()
                ? dataId
                : extrairCampo(payload, "payment_id", "id");

        validarAssinaturaWebhook(assinaturaWebhook, requestId, paymentId);

        if (paymentId == null || paymentId.isBlank()) {
            throw new IllegalArgumentException("Webhook Mercado Pago sem payment id.");
        }

        MercadoPagoCheckoutService.MercadoPagoPaymentData pagamentoExterno =
                mercadoPagoCheckoutService.buscarPagamentoPorId(paymentId);

        webhook.setExternalId(pagamentoExterno.paymentId());
        webhook.setPayload(paraJson(Map.of(
                "webhook", payload != null ? payload : Map.of(),
                "mercadoPagoPayment", toMapPagamentoExterno(pagamentoExterno)
        )));
        webhook = webhookPagamentoRepository.save(webhook);

        Optional<PagamentoPlano> pagamentoOptional = buscarPagamento(
                pagamentoExterno.paymentId(),
                pagamentoExterno.externalReference(),
                extrairCampo(payload, "preference_id", "preferenceId")
        );
        if (pagamentoOptional.isEmpty()) {
            return;
        }

        PagamentoPlano pagamento = pagamentoOptional.get();
        pagamento.setMercadoPagoPaymentId(pagamentoExterno.paymentId());
        StatusPagamentoPlano statusInterno = mapearStatusPagamento(pagamentoExterno.status());
        pagamento.setStatus(statusInterno);
        pagamento.setRawWebhookPayload(webhook.getPayload());

        if (statusInterno == StatusPagamentoPlano.PAGO) {
            pagamento.setPagoEm(Instant.now());
            assinaturaPlanoService.ativarAssinaturaPorPagamento(pagamento);
        }

        pagamentoPlanoRepository.save(pagamento);
        webhook.setProcessado(true);
        webhookPagamentoRepository.save(webhook);
    }

    private Optional<PagamentoPlano> buscarPagamento(String paymentId, String referencia, String preferenceId) {
        if (paymentId != null && !paymentId.isBlank()) {
            Optional<PagamentoPlano> porPaymentId = pagamentoPlanoRepository.findByMercadoPagoPaymentId(paymentId);
            if (porPaymentId.isPresent()) {
                return porPaymentId;
            }
        }

        if (referencia != null && !referencia.isBlank()) {
            Optional<PagamentoPlano> porReferencia = pagamentoPlanoRepository.findByReferencia(referencia);
            if (porReferencia.isPresent()) {
                return porReferencia;
            }
        }

        if (preferenceId != null && !preferenceId.isBlank()) {
            return pagamentoPlanoRepository.findByMercadoPagoPreferenceId(preferenceId);
        }

        return Optional.empty();
    }

    private StatusPagamentoPlano mapearStatusPagamento(String statusExterno) {
        if (statusExterno == null || statusExterno.isBlank()) {
            return StatusPagamentoPlano.GERADO;
        }

        String status = statusExterno.trim().toLowerCase(Locale.ROOT);
        return switch (status) {
            case "approved", "paid" -> StatusPagamentoPlano.PAGO;
            case "pending", "in_process" -> StatusPagamentoPlano.GERADO;
            case "cancelled", "canceled" -> StatusPagamentoPlano.CANCELADO;
            case "rejected", "failed" -> StatusPagamentoPlano.FALHOU;
            case "expired" -> StatusPagamentoPlano.EXPIRADO;
            default -> StatusPagamentoPlano.GERADO;
        };
    }

    private String extrairCampo(Map<String, Object> payload, String... chaves) {
        if (payload == null) {
            return null;
        }

        for (String chave : chaves) {
            Object valor = payload.get(chave);
            if (valor != null) {
                return String.valueOf(valor);
            }
        }

        Object dataObj = payload.get("data");
        if (dataObj instanceof Map<?, ?> dataMap) {
            for (String chave : chaves) {
                Object valor = dataMap.get(chave);
                if (valor != null) {
                    return String.valueOf(valor);
                }
            }
        }

        return null;
    }

    private void validarAssinaturaWebhook(String assinaturaWebhook, String requestId, String dataId) {
        String secret = paymentProperties.mercadopago().webhookSecret();
        if (secret == null || secret.isBlank()) {
            return;
        }

        if (assinaturaWebhook == null || assinaturaWebhook.isBlank()) {
            throw new IllegalArgumentException("Webhook Mercado Pago inválido.");
        }

        String ts = extrairSegmento(assinaturaWebhook, "ts");
        String v1 = extrairSegmento(assinaturaWebhook, "v1");

        if (ts == null || v1 == null || requestId == null || requestId.isBlank() || dataId == null || dataId.isBlank()) {
            throw new IllegalArgumentException("Webhook Mercado Pago inválido.");
        }

        String manifest = "id:" + dataId + ";request-id:" + requestId + ";ts:" + ts + ";";
        String hash = gerarHmacSha256(secret, manifest);

        String signatureCalculada = hash.toLowerCase(Locale.ROOT);
        String signatureRecebida = v1.toLowerCase(Locale.ROOT);

        if (!MessageDigest.isEqual(
                signatureCalculada.getBytes(StandardCharsets.UTF_8),
                signatureRecebida.getBytes(StandardCharsets.UTF_8)
        )) {
            throw new IllegalArgumentException("Assinatura do webhook Mercado Pago inválida.");
        }
    }

    private String paraJson(Map<String, Object> payload) {
        if (payload == null) {
            return "{}";
        }
        try {
            return toJsonValue(payload);
        } catch (Exception ex) {
            return "{}";
        }
    }

    private String toJsonValue(Object value) {
        if (value == null) {
            return "null";
        }

        if (value instanceof String s) {
            return "\"" + escapeJson(s) + "\"";
        }

        if (value instanceof Number || value instanceof Boolean) {
            return String.valueOf(value);
        }

        if (value instanceof Map<?, ?> map) {
            StringBuilder sb = new StringBuilder();
            sb.append("{");

            boolean first = true;
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                if (!first) {
                    sb.append(",");
                }
                first = false;

                String key = String.valueOf(entry.getKey());
                sb.append("\"")
                        .append(escapeJson(key))
                        .append("\":")
                        .append(toJsonValue(entry.getValue()));
            }

            sb.append("}");
            return sb.toString();
        }

        if (value instanceof Iterable<?> iterable) {
            StringBuilder sb = new StringBuilder();
            sb.append("[");

            boolean first = true;
            for (Object item : iterable) {
                if (!first) {
                    sb.append(",");
                }
                first = false;
                sb.append(toJsonValue(item));
            }

            sb.append("]");
            return sb.toString();
        }

        if (value.getClass().isArray()) {
            StringBuilder sb = new StringBuilder();
            sb.append("[");

            int length = java.lang.reflect.Array.getLength(value);
            for (int i = 0; i < length; i++) {
                if (i > 0) {
                    sb.append(",");
                }
                sb.append(toJsonValue(java.lang.reflect.Array.get(value, i)));
            }

            sb.append("]");
            return sb.toString();
        }

        return "\"" + escapeJson(String.valueOf(value)) + "\"";
    }

    private String escapeJson(String value) {
        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\b", "\\b")
                .replace("\f", "\\f")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    private Map<String, Object> toMapPagamentoExterno(MercadoPagoCheckoutService.MercadoPagoPaymentData data) {
        Map<String, Object> map = new HashMap<>();
        map.put("paymentId", data.paymentId());
        map.put("status", data.status());
        map.put("externalReference", data.externalReference());
        return map;
    }

    private String extrairSegmento(String header, String chave) {
        String[] partes = header.split(",");
        for (String parte : partes) {
            String[] kv = parte.trim().split("=", 2);
            if (kv.length == 2 && chave.equalsIgnoreCase(kv[0].trim())) {
                return kv[1].trim();
            }
        }
        return null;
    }

    private String gerarHmacSha256(String secret, String message) {
        try {
            Mac sha256 = Mac.getInstance("HmacSHA256");
            SecretKeySpec keySpec = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            sha256.init(keySpec);
            byte[] hash = sha256.doFinal(message.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception ex) {
            throw new IllegalStateException("Erro ao validar assinatura do webhook.", ex);
        }
    }

    private FaturaPlanoResponse toResponse(PagamentoPlano pagamento) {
        return FaturaPlanoResponse.builder()
                .pagamentoId(pagamento.getId())
                .referencia(pagamento.getReferencia())
                .plano(pagamento.getAssinatura().getPlano().getNome())
                .valor(pagamento.getValor())
                .status(pagamento.getStatus())
                .tipo(pagamento.getTipo())
                .dataVencimento(pagamento.getDataVencimento())
                .pagoEm(pagamento.getPagoEm())
                .checkoutUrl(pagamento.getCheckoutUrl())
                .build();
    }
}