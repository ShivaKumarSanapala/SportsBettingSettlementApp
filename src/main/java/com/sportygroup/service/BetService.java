package com.sportygroup.service;

import com.sportygroup.dto.CreateBetRequest;
import com.sportygroup.model.Bet;
import com.sportygroup.model.BetStatus;
import com.sportygroup.repository.BetRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service class for managing bets
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BetService {
    
    private final BetRepository betRepository;
    
    /**
     * Create a new bet
     */
    public Bet createBet(CreateBetRequest request) {
        log.info("Creating new bet for user: {} on event: {}", request.getUserId(), request.getEventId());
        
        Bet bet = new Bet(
            request.getUserId(),
            request.getEventId(),
            request.getEventMarketId(),
            request.getEventWinnerId(),
            request.getBetAmount()
        );
        
        Bet savedBet = betRepository.save(bet);
        log.info("Created bet with ID: {}", savedBet.getBetId());
        
        return savedBet;
    }
    
    /**
     * Get all bets
     */
    public List<Bet> getAllBets() {
        return betRepository.findAll();
    }
    
    /**
     * Get bet by ID
     */
    public Optional<Bet> getBetById(Long betId) {
        return betRepository.findById(betId);
    }
    
    /**
     * Get bets for a specific user
     */
    public List<Bet> getBetsByUserId(String userId) {
        return betRepository.findByUserId(userId);
    }
    
    /**
     * Find all pending bets for a specific event
     */
    public List<Bet> getPendingBetsForEvent(String eventId) {
        return betRepository.findByEventIdAndStatus(eventId, BetStatus.PENDING);
    }
    
    /**
     * Find winning bets for a specific event outcome
     */
    public List<Bet> getWinningBets(String eventId, String winnerId) {
        return betRepository.findWinningBets(eventId, winnerId, BetStatus.PENDING);
    }
    
    /**
     * Find losing bets for a specific event outcome
     */
    public List<Bet> getLosingBets(String eventId, String winnerId) {
        return betRepository.findLosingBets(eventId, winnerId, BetStatus.PENDING);
    }
    
    /**
     * Update bet status
     */
    public Bet updateBetStatus(Long betId, BetStatus status) {
        Optional<Bet> betOptional = betRepository.findById(betId);
        if (betOptional.isPresent()) {
            Bet bet = betOptional.get();
            bet.setStatus(status);
            Bet updatedBet = betRepository.save(bet);
            log.info("Updated bet {} status to {}", betId, status);
            return updatedBet;
        } else {
            log.warn("Bet with ID {} not found", betId);
            throw new RuntimeException("Bet not found with ID: " + betId);
        }
    }
}
