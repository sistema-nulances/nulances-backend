package com.Nulances.messaging.consumer;

import com.Nulances.dto.messaging.ArrematacaoVencedorMessage;
import com.Nulances.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ArrematacaoVencedorConsumer {

    private final EmailService emailService;

    @JmsListener(destination = "${app.jms.queue-arrematacao-vencedor}")
    public void consumir(ArrematacaoVencedorMessage message) {
        log.info("Enviando e-mail de arrematação para {}", message.email());

        emailService.enviarAvisoArrematacaoVencedora(
                message.email(),
                message.nomeUsuario(),
                message.tituloLeilao(),
                message.codigoLote(),
                message.nomeBem(),
                message.valorFinal()
        );
    }
}
