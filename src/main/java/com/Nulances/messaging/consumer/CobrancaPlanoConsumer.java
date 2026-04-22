package com.Nulances.messaging.consumer;

import com.Nulances.dto.messaging.CobrancaPlanoMessage;
import com.Nulances.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CobrancaPlanoConsumer {

    private final EmailService emailService;

    @JmsListener(destination = "${app.jms.queue-cobranca-plano}")
    public void consumir(CobrancaPlanoMessage message) {
        log.info("Enviando cobrança do plano para {}", message.email());
        emailService.enviarCobrancaPlano(
                message.email(),
                message.nomeVendedor(),
                message.nomePlano(),
                message.valor(),
                message.vencimento(),
                message.checkoutUrl()
        );
    }
}
