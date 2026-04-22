package com.Nulances.payment.service;

import com.Nulances.payment.config.PaymentProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MercadoPagoCheckoutService {

    private final PaymentProperties paymentProperties;
    private final RestClient mercadoPagoRestClient;

    public CheckoutPreferenceData criarPreferenciaCheckout(
            String referencia,
            String titulo,
            BigDecimal valor,
            String emailComprador
    ) {
        validarConfiguracao();

        Map<String, Object> item = new HashMap<>();
        item.put("title", titulo);
        item.put("quantity", 1);
        item.put("currency_id", "BRL");
        item.put("unit_price", valor.doubleValue());

        Map<String, Object> backUrls = new HashMap<>();
        backUrls.put("success", paymentProperties.mercadopago().successUrl());
        backUrls.put("pending", paymentProperties.mercadopago().pendingUrl());
        backUrls.put("failure", paymentProperties.mercadopago().failureUrl());

        Map<String, Object> payer = new HashMap<>();
        payer.put("email", emailComprador);

        Map<String, Object> payload = new HashMap<>();
        payload.put("items", List.of(item));
        payload.put("external_reference", referencia);
        payload.put("back_urls", backUrls);
        payload.put("notification_url", paymentProperties.mercadopago().webhookUrl());
        payload.put("auto_return", "approved");
        payload.put("payer", payer);

        String idempotencyKey = UUID.randomUUID().toString();

        try {
            Map<String, Object> response = mercadoPagoRestClient.post()
                    .uri("/checkout/preferences")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("X-Idempotency-Key", idempotencyKey)
                    .body(payload)
                    .retrieve()
                    .body(Map.class);

            if (response == null) {
                throw new IllegalStateException("Mercado Pago retornou resposta vazia ao criar preferência.");
            }

            String preferenceId = toString(response.get("id"));
            String checkoutUrl = toString(response.get("init_point"));

            if (preferenceId == null || preferenceId.isBlank()) {
                throw new IllegalStateException("Mercado Pago não retornou preference id.");
            }

            if (checkoutUrl == null || checkoutUrl.isBlank()) {
                throw new IllegalStateException("Mercado Pago não retornou init_point de checkout.");
            }

            return new CheckoutPreferenceData(preferenceId, checkoutUrl);
        } catch (RestClientResponseException ex) {
            throw new IllegalStateException("Erro Mercado Pago ao criar preferência: " + ex.getResponseBodyAsString(), ex);
        }
    }

    public MercadoPagoPaymentData buscarPagamentoPorId(String paymentId) {
        if (paymentId == null || paymentId.isBlank()) {
            throw new IllegalArgumentException("paymentId é obrigatório para consulta no Mercado Pago.");
        }

        try {
            Map<String, Object> response = mercadoPagoRestClient.get()
                    .uri("/v1/payments/{id}", paymentId)
                    .retrieve()
                    .body(Map.class);

            if (response == null) {
                throw new IllegalStateException("Mercado Pago retornou pagamento vazio.");
            }

            return new MercadoPagoPaymentData(
                    toString(response.get("id")),
                    toString(response.get("status")),
                    toString(response.get("external_reference"))
            );
        } catch (RestClientResponseException ex) {
            throw new IllegalStateException("Erro Mercado Pago ao consultar pagamento: " + ex.getResponseBodyAsString(), ex);
        }
    }

    private void validarConfiguracao() {
        validarObrigatorio(paymentProperties.mercadopago().accessToken(), "app.payment.mercadopago.access-token");
        validarObrigatorio(paymentProperties.mercadopago().webhookUrl(), "app.payment.mercadopago.webhook-url");
        validarObrigatorio(paymentProperties.mercadopago().successUrl(), "app.payment.mercadopago.success-url");
        validarObrigatorio(paymentProperties.mercadopago().pendingUrl(), "app.payment.mercadopago.pending-url");
        validarObrigatorio(paymentProperties.mercadopago().failureUrl(), "app.payment.mercadopago.failure-url");
    }

    private void validarObrigatorio(String valor, String chave) {
        if (valor == null || valor.isBlank() || valor.startsWith("SEU_")) {
            throw new IllegalStateException("Propriedade obrigatória não configurada: " + chave);
        }
    }

    private String toString(Object value) {
        return value == null ? null : String.valueOf(value);
    }

    public record CheckoutPreferenceData(
            String preferenceId,
            String checkoutUrl
    ) {}

    public record MercadoPagoPaymentData(
            String paymentId,
            String status,
            String externalReference
    ) {}
}
