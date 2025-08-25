package com.sportygroup.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sportygroup.model.BetSettlement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Service for handling bet settlements via RocketMQ
 * Supports both real RocketMQ and mock implementation
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BetSettlementService {
    
    private final ObjectMapper objectMapper;
    private final RocketMQTemplate rocketMQTemplate;
    
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
     * Real RocketMQ implementation using Spring Boot RocketMQ Starter
     */
    private void realPublishBetSettlement(BetSettlement betSettlement) {
        try {
            log.info("Publishing bet settlement to RocketMQ topic '{}': {}", betSettlementsTopic, betSettlement);
            
            // Send message to RocketMQ
            rocketMQTemplate.convertAndSend(betSettlementsTopic, betSettlement);
            
            log.info("Successfully published bet settlement to RocketMQ for bet ID: {}", betSettlement.getBetId());
            
        } catch (Exception e) {
            log.error("Failed to publish bet settlement to RocketMQ for bet ID: {}", betSettlement.getBetId(), e);
            // In production, you might want to retry or send to a dead letter queue
            throw new RuntimeException("Failed to publish bet settlement", e);
        }
    }
    
    /**
     * Process bet settlement (handles settlement logic)
     */
    public void processBetSettlement(BetSettlement betSettlement) {
        log.info("Processing bet settlement: {}", betSettlement);
        
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
    
    /**
     * RocketMQ Consumer for bet settlements
     * This is a separate component that listens to the bet-settlements topic
     */
    @Service
    @RocketMQMessageListener(
        topic = "${app.rocketmq.topics.bet-settlements}",
        consumerGroup = "${rocketmq.consumer.group}"
    )
    @Slf4j
    public static class BetSettlementConsumer implements RocketMQListener<BetSettlement> {
        
        private final BetSettlementService betSettlementService;
        
        public BetSettlementConsumer(BetSettlementService betSettlementService) {
            this.betSettlementService = betSettlementService;
        }
        
        @Override
        public void onMessage(BetSettlement betSettlement) {
            log.info("RocketMQ Consumer - Received bet settlement: {}", betSettlement);
            
            try {
                betSettlementService.processBetSettlement(betSettlement);
            } catch (Exception e) {
                log.error("Error processing bet settlement from RocketMQ: {}", betSettlement, e);
                // In production, you might want to implement retry logic or dead letter queue
            }
        }
    }
}
