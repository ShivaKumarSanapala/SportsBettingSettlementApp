package com.sportygroup.consumer;

import com.sportygroup.model.EventOutcome;
import com.sportygroup.service.BetMatchingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/**
 * Kafka consumer for event outcomes
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class EventOutcomeConsumer {
    
    private final BetMatchingService betMatchingService;
    
    /**
     * Listen to event-outcomes topic and process bet settlements
     */
    @KafkaListener(topics = "${app.kafka.topics.event-outcomes}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeEventOutcome(
            @Payload EventOutcome eventOutcome,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset) {
        
        log.info("Received event outcome from topic: {}, partition: {}, offset: {} - Event: {}", 
            topic, partition, offset, eventOutcome);
        
        try {
            // Process the event outcome and match it to bets
            betMatchingService.processEventOutcome(eventOutcome);
            
            log.info("Successfully processed event outcome for event: {}", eventOutcome.getEventId());
            
        } catch (Exception e) {
            log.error("Error processing event outcome for event: {}", eventOutcome.getEventId(), e);
            // In a production system, you might want to send to a dead letter queue
            // or implement retry logic here
        }
    }
}
