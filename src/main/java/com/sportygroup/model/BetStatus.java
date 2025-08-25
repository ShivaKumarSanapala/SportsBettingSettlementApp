package com.sportygroup.model;

/**
 * Represents the status of a bet
 */
public enum BetStatus {
    PENDING,    // Bet placed but event not yet concluded
    WON,        // Bet won - user predicted correctly
    LOST,       // Bet lost - user predicted incorrectly
    VOID        // Bet voided - event cancelled or invalid
}
