package com.Nulances.messaging.publisher;

import com.Nulances.config.AppProperties;
import com.Nulances.dto.messaging.LanceRecebidoMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LancePublisher {

    private final JmsTemplate jmsTemplate;
    private final AppProperties appProperties;

    public void publicar(LanceRecebidoMessage message) {
        jmsTemplate.convertAndSend(
                appProperties.jms().queueLanceRecebido(),
                message
        );
    }
}