package com.sportygroup.controller;

import com.sportygroup.dto.CreateBetRequest;
import com.sportygroup.model.Bet;
import com.sportygroup.service.BetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * REST Controller for bet management
 */
@RestController
@RequestMapping("/api/bets")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Slf4j
public class BetController {
    
    private final BetService betService;
    
    /**
     * Create a new bet
     * POST /api/bets
     */
    @PostMapping
    public ResponseEntity<Bet> createBet(@Valid @RequestBody CreateBetRequest request) {
        log.info("Received request to create bet: {}", request);
        
        try {
            Bet bet = betService.createBet(request);
            log.info("Successfully created bet with ID: {}", bet.getBetId());
            return ResponseEntity.status(HttpStatus.CREATED).body(bet);
            
        } catch (Exception e) {
            log.error("Error creating bet", e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Get all bets
     * GET /api/bets
     */
    @GetMapping
    public ResponseEntity<List<Bet>> getAllBets() {
        log.info("Received request to get all bets");
        
        try {
            List<Bet> bets = betService.getAllBets();
            log.info("Retrieved {} bets", bets.size());
            return ResponseEntity.ok(bets);
            
        } catch (Exception e) {
            log.error("Error retrieving bets", e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Get bet by ID
     * GET /api/bets/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Bet> getBetById(@PathVariable Long id) {
        log.info("Received request to get bet with ID: {}", id);
        
        try {
            Optional<Bet> bet = betService.getBetById(id);
            if (bet.isPresent()) {
                log.info("Found bet with ID: {}", id);
                return ResponseEntity.ok(bet.get());
            } else {
                log.warn("Bet with ID {} not found", id);
                return ResponseEntity.notFound().build();
            }
            
        } catch (Exception e) {
            log.error("Error retrieving bet with ID: {}", id, e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Get bets by user ID
     * GET /api/bets/user/{userId}
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Bet>> getBetsByUserId(@PathVariable String userId) {
        log.info("Received request to get bets for user: {}", userId);
        
        try {
            List<Bet> bets = betService.getBetsByUserId(userId);
            log.info("Retrieved {} bets for user: {}", bets.size(), userId);
            return ResponseEntity.ok(bets);
            
        } catch (Exception e) {
            log.error("Error retrieving bets for user: {}", userId, e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
