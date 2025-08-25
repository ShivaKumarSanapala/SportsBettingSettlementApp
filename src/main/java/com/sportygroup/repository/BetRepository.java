package com.sportygroup.repository;

import com.sportygroup.model.Bet;
import com.sportygroup.model.BetStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for Bet entity operations
 */
@Repository
public interface BetRepository extends JpaRepository<Bet, Long> {
    
    /**
     * Find all bets for a specific event that are pending settlement
     */
    List<Bet> findByEventIdAndStatus(String eventId, BetStatus status);
    
    /**
     * Find all bets for a specific user
     */
    List<Bet> findByUserId(String userId);
    
    /**
     * Find all winning bets for a specific event outcome
     */
    @Query("SELECT b FROM Bet b WHERE b.eventId = :eventId AND b.eventWinnerId = :winnerId AND b.status = :status")
    List<Bet> findWinningBets(@Param("eventId") String eventId, 
                              @Param("winnerId") String winnerId, 
                              @Param("status") BetStatus status);
    
    /**
     * Find all losing bets for a specific event outcome
     */
    @Query("SELECT b FROM Bet b WHERE b.eventId = :eventId AND b.eventWinnerId != :winnerId AND b.status = :status")
    List<Bet> findLosingBets(@Param("eventId") String eventId, 
                             @Param("winnerId") String winnerId, 
                             @Param("status") BetStatus status);
}
