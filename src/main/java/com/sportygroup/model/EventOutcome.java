package com.sportygroup.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

/**
 * Represents a sports event outcome
 */
@Data
@NoArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class EventOutcome {
    
    @EqualsAndHashCode.Include
    private String eventId;
    private String eventName;
    private String eventWinnerId;

    @JsonCreator
    public EventOutcome(
            @JsonProperty("eventId") String eventId,
            @JsonProperty("eventName") String eventName,
            @JsonProperty("eventWinnerId") String eventWinnerId) {
        this.eventId = eventId;
        this.eventName = eventName;
        this.eventWinnerId = eventWinnerId;
    }
}
