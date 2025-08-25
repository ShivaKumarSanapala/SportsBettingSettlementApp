package com.sportygroup.config;

import com.sportygroup.model.Bet;
import com.sportygroup.model.BetStatus;
import com.sportygroup.repository.BetRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Data loader to populate sample bets for testing - demonstrating Lombok usage
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataLoader implements CommandLineRunner {
    
    private final BetRepository betRepository;
    
    @Override
    public void run(String... args) throws Exception {
        if (betRepository.count() == 0) {
            loadSampleData();
        }
    }
    
    private void loadSampleData() {
        log.info("Loading sample bet data using Lombok builders...");
        
        // Using Lombok's @Builder pattern for cleaner object creation
        Bet bet1 = Bet.builder()
            .userId("user123")
            .eventId("event001")
            .eventMarketId("match-winner")
            .eventWinnerId("team1")
            .betAmount(new BigDecimal("100.00"))
            .status(BetStatus.PENDING)
            .build();
        
        Bet bet2 = Bet.builder()
            .userId("user456")
            .eventId("event001")
            .eventMarketId("match-winner")
            .eventWinnerId("team2")
            .betAmount(new BigDecimal("50.00"))
            .status(BetStatus.PENDING)
            .build();
        
        Bet bet3 = Bet.builder()
            .userId("user789")
            .eventId("event001")
            .eventMarketId("over-under")
            .eventWinnerId("team1")
            .betAmount(new BigDecimal("75.00"))
            .status(BetStatus.PENDING)
            .build();
        
        Bet bet4 = Bet.builder()
            .userId("user123")
            .eventId("event002")
            .eventMarketId("match-winner")
            .eventWinnerId("teamA")
            .betAmount(new BigDecimal("200.00"))
            .status(BetStatus.PENDING)
            .build();
        
        Bet bet5 = Bet.builder()
            .userId("user456")
            .eventId("event002")
            .eventMarketId("match-winner")
            .eventWinnerId("teamB")
            .betAmount(new BigDecimal("150.00"))
            .status(BetStatus.PENDING)
            .build();
        
        betRepository.save(bet1);
        betRepository.save(bet2);
        betRepository.save(bet3);
        betRepository.save(bet4);
        betRepository.save(bet5);
        
        log.info("Loaded {} sample bets", betRepository.count());
        
        // Log sample bets for reference using Lombok's toString
        betRepository.findAll().forEach(bet -> 
            log.info("Sample bet: {}", bet));
    }
}
