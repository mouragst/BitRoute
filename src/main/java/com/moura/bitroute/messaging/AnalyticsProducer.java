package com.moura.bitroute.messaging;

import com.moura.bitroute.config.RabbitMQConfig;
import com.moura.bitroute.dto.PasteViewEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AnalyticsProducer {
    
    private final RabbitTemplate rabbitTemplate;

    public void publishViewEvent(PasteViewEvent event) {
        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.ANALYTICS_EXCHANGE,
                    RabbitMQConfig.ANALYTICS_ROUTING_KEY,
                    event
            );
        } catch (Exception e) {
            log.error("Failed to publish view event for paste: {}", event.getShortlink(), e);
        }
    }
}
