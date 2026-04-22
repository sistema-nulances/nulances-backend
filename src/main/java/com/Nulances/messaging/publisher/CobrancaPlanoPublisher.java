package com.Nulances.messaging.publisher;

import com.Nulances.config.AppProperties;
import com.Nulances.dto.messaging.CobrancaPlanoMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CobrancaPlanoPublisher {

    private final JmsTemplate jmsTemplate;
    private final AppProperties appProperties;

    public void publicar(CobrancaPlanoMessage message) {
        jmsTemplate.convertAndSend(
                appProperties.jms().queueCobrancaPlano(),
                message
        );
    }
}
