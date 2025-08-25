package com.sportygroup.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sportygroup.model.BetSettlement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Service for handling bet settlements via RocketMQ
 * Uses mock implementation as suggested in assignment if RocketMQ setup is complex
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BetSettlementService {
    
    private final ObjectMapper objectMapper;
    
    @Value("${app.mock.rocketmq:true}")
    private boolean mockRocketMQ;
    
    @Value("${app.rocketmq.topics.bet-settlements}")
    private String betSettlementsTopic;
    
    /**
     * Publish bet settlement message to RocketMQ (or mock if configured)
     */
    public void publishBetSettlement(BetSettlement betSettlement) {
        if (mockRocketMQ) {
            mockPublishBetSettlement(betSettlement);
        } else {
            // Real RocketMQ implementation would go here
            realPublishBetSettlement(betSettlement);
        }
    }
    
    /**
     * Mock implementation - just logs the settlement payload
     */
    private void mockPublishBetSettlement(BetSettlement betSettlement) {
        try {
            String payload = objectMapper.writeValueAsString(betSettlement);
            log.info("MOCK RocketMQ Producer - Publishing bet settlement to topic '{}': {}", 
                betSettlementsTopic, payload);
            
            // Simulate processing the settlement immediately
            processBetSettlement(betSettlement);
            
        } catch (JsonProcessingException e) {
            log.error("Error serializing bet settlement: {}", betSettlement, e);
        }
    }
    
    /**
     * Real RocketMQ implementation (placeholder)
     */
    private void realPublishBetSettlement(BetSettlement betSettlement) {
        // Real RocketMQ producer implementation would go here
        // Example implementation:
        /*
        try {
            DefaultMQProducer producer = new DefaultMQProducer("sports-betting-producer-group");
            producer.setNamesrvAddr("localhost:9876");
            producer.start();
            
            Message message = new Message(
                betSettlementsTopic,
                "bet-settlement",
                objectMapper.writeValueAsString(betSettlement).getBytes()
            );
            
            SendResult result = producer.send(message);
            logger.info("RocketMQ message sent successfully: {}", result);
            
            producer.shutdown();
        } catch (Exception e) {
            logger.error("Failed to send RocketMQ message", e);
        }
        */
        
        // For now, fall back to mock
        log.info("RocketMQ not configured, falling back to mock implementation");
        mockPublishBetSettlement(betSettlement);
    }
    
    /**
     * Process bet settlement (simulates RocketMQ consumer)
     */
    public void processBetSettlement(BetSettlement betSettlement) {
        log.info("MOCK RocketMQ Consumer - Processing bet settlement: {}", betSettlement);
        
        switch (betSettlement.getSettlementStatus()) {
            case WON:
                log.info("Bet {} WON - User {} receives payout of {}", 
                    betSettlement.getBetId(), 
                    betSettlement.getUserId(), 
                    betSettlement.getPayoutAmount());
                // In a real system, this would credit the user's account
                break;
                
            case LOST:
                log.info("Bet {} LOST - User {} loses stake, no payout", 
                    betSettlement.getBetId(), 
                    betSettlement.getUserId());
                // In a real system, this would debit the user's account (already done when bet was placed)
                break;
                
            case VOID:
                log.info("Bet {} VOID - User {} stake refunded", 
                    betSettlement.getBetId(), 
                    betSettlement.getUserId());
                // In a real system, this would refund the original stake
                break;
                
            default:
                log.warn("Unknown settlement status for bet {}: {}", 
                    betSettlement.getBetId(), 
                    betSettlement.getSettlementStatus());
        }
        
        log.info("Successfully processed bet settlement for bet ID: {}", betSettlement.getBetId());
    }
}
