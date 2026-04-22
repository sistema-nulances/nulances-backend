package com.Nulances.controller;

import com.Nulances.payment.service.FaturamentoPlanoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/payment/webhook")
@RequiredArgsConstructor
public class PaymentWebhookController {

    private final FaturamentoPlanoService faturamentoPlanoService;

    @PostMapping("/mercadopago")
    public ResponseEntity<Map<String, String>> receberWebhookMercadoPago(
            @RequestBody(required = false) Map<String, Object> payload,
            @RequestParam(name = "type", required = false) String type,
            @RequestParam(name = "data.id", required = false) String dataId,
            @RequestHeader(name = "x-signature", required = false) String signature,
            @RequestHeader(name = "x-request-id", required = false) String requestId
    ) {
        faturamentoPlanoService.processarWebhookMercadoPago(
                payload != null ? payload : Map.of(),
                type,
                signature,
                requestId,
                dataId
        );

        return ResponseEntity.ok(Map.of("status", "received"));
    }
}
