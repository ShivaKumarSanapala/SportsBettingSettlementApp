package com.sportygroup.controller;

import com.sportygroup.dto.EventOutcomeRequest;
import com.sportygroup.model.EventOutcome;
import com.sportygroup.service.EventOutcomeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for event outcomes
 */
@RestController
@RequestMapping("/api/events")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Slf4j
public class EventOutcomeController {
    
    private final EventOutcomeService eventOutcomeService;
    
    /**
     * Publish event outcome to Kafka
     * POST /api/events/outcomes
     */
    @PostMapping("/outcomes")
    public ResponseEntity<String> publishEventOutcome(@Valid @RequestBody EventOutcomeRequest request) {
        log.info("Received request to publish event outcome: {}", request);
        
        try {
            EventOutcome eventOutcome = new EventOutcome(
                request.getEventId(),
                request.getEventName(),
                request.getEventWinnerId()
            );
            
            eventOutcomeService.publishEventOutcome(eventOutcome);
            
            String response = String.format("Event outcome published successfully for event: %s", 
                request.getEventId());
            
            log.info("Successfully published event outcome for event: {}", request.getEventId());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error publishing event outcome for event: {}", request.getEventId(), e);
            return ResponseEntity.internalServerError()
                .body("Error publishing event outcome: " + e.getMessage());
        }
    }
}
