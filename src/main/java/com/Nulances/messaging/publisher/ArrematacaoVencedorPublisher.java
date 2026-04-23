package com.Nulances.messaging.publisher;

import com.Nulances.config.AppProperties;
import com.Nulances.dto.messaging.ArrematacaoVencedorMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ArrematacaoVencedorPublisher {

    private final JmsTemplate jmsTemplate;
    private final AppProperties appProperties;

    public void publicar(ArrematacaoVencedorMessage message) {
        jmsTemplate.convertAndSend(
                appProperties.jms().queueArrematacaoVencedor(),
                message
        );
    }
}
