package com.moura.bitroute.messaging;

import com.moura.bitroute.config.RabbitMQConfig;
import com.moura.bitroute.dto.PasteViewEvent;
import com.moura.bitroute.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AnalyticsConsumer {
    
    private final AnalyticsService analyticsService;
   
    @RabbitListener(queues = RabbitMQConfig.ANALYTICS_QUEUE)
    public void handleViewEvent(PasteViewEvent event) {
        try {
            analyticsService.recordView(event);
        } catch (Exception e) {
            log.error("Error processing view event for paste: {}", event.getShortlink(), e);
            throw e;
        }
    }
}
