package com.Nulances.messaging.consumer;

import com.Nulances.dto.messaging.LanceRecebidoMessage;
import com.Nulances.service.LanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LanceConsumer {

    private final LanceService lanceService;

    @JmsListener(destination = "${app.jms.queue-lance-recebido}")
    public void consumir(LanceRecebidoMessage message) {
        log.info("Processando lance do usuário {} para item {}", message.usuarioId(), message.leilaoLoteBemId());
        lanceService.processarLance(message);
    }
}