package org.example.eventm;

import org.example.eventm.api.model.TicketStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TicketStatusTest {

    @Test
    void available_canBePurchased_resultsInPurchased() {
        TicketStatus next = TicketStatus.AVAILABLE.purchase();
        assertEquals(TicketStatus.PURCHASED, next);
    }

    @Test
    void available_canBeReserved_resultsInReserved() {
        TicketStatus next = TicketStatus.AVAILABLE.reserve();
        assertEquals(TicketStatus.RESERVED, next);
    }

    @Test
    void reserved_canBePurchased_resultsInPurchased() {
        TicketStatus next = TicketStatus.RESERVED.purchase();
        assertEquals(TicketStatus.PURCHASED, next);
    }

    @Test
    void purchased_cannotBePurchasedAgain() {
        assertThrows(IllegalStateException.class, () -> TicketStatus.PURCHASED.purchase());
    }

    @Test
    void cancelled_cannotBePurchased() {
        assertThrows(IllegalStateException.class, () -> TicketStatus.CANCELLED.purchase());
    }
}
