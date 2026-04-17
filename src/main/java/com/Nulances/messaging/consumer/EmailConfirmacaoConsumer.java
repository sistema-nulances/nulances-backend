package com.Nulances.messaging.consumer;

import com.Nulances.dto.messaging.EmailConfirmacaoMessage;
import com.Nulances.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailConfirmacaoConsumer {

    private final EmailService emailService;

    @JmsListener(destination = "${app.jms.queue-email-confirmacao}")
    public void consumir(EmailConfirmacaoMessage message) {
        log.info("Enviando e-mail de confirmação para {}", message.email());

        emailService.enviarCodigoConfirmacao(
                message.email(),
                message.nome(),
                message.codigo()
        );
    }
}