package com.sportygroup.service;

import com.sportygroup.model.EventOutcome;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

/**
 * Service for publishing event outcomes to Kafka
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EventOutcomeService {
    
    private final KafkaTemplate<String, EventOutcome> kafkaTemplate;
    
    @Value("${app.kafka.topics.event-outcomes}")
    private String eventOutcomesTopic;
    
    /**
     * Publish event outcome to Kafka
     */
    public void publishEventOutcome(EventOutcome eventOutcome) {
        log.info("Publishing event outcome to Kafka: {}", eventOutcome);
        
        CompletableFuture<SendResult<String, EventOutcome>> future = 
            kafkaTemplate.send(eventOutcomesTopic, eventOutcome.getEventId(), eventOutcome);
        
        future.whenComplete((result, exception) -> {
            if (exception == null) {
                log.info("Successfully published event outcome for event: {} to topic: {} at offset: {}",
                    eventOutcome.getEventId(), eventOutcomesTopic, result.getRecordMetadata().offset());
            } else {
                log.error("Failed to publish event outcome for event: {} to topic: {}",
                    eventOutcome.getEventId(), eventOutcomesTopic, exception);
            }
        });
    }
}
