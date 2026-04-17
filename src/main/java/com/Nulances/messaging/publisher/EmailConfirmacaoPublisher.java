package com.Nulances.messaging.publisher;

import com.Nulances.config.AppProperties;
import com.Nulances.dto.messaging.EmailConfirmacaoMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailConfirmacaoPublisher {

    private final JmsTemplate jmsTemplate;
    private final AppProperties appProperties;

    public void publicar(EmailConfirmacaoMessage message) {
        jmsTemplate.convertAndSend(
                appProperties.jms().queueEmailConfirmacao(),
                message
        );
    }
}