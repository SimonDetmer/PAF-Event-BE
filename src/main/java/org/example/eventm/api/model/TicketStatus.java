package org.example.eventm.api.model;

public enum TicketStatus {

    AVAILABLE {
        @Override
        public TicketStatus reserve() {
            return RESERVED;
        }

        @Override
        public TicketStatus purchase() {
            return PURCHASED;
        }
    },

    RESERVED {
        @Override
        public TicketStatus purchase() {
            return PURCHASED;
        }

        @Override
        public TicketStatus cancel() {
            return AVAILABLE;
        }

        @Override
        public TicketStatus expire() {
            return EXPIRED;
        }
    },

    PURCHASED {
        @Override
        public TicketStatus cancel() {
            return CANCELLED;
        }
    },

    CANCELLED,
    EXPIRED;

    public TicketStatus reserve() {
        throw new IllegalStateException("Cannot reserve ticket in state " + this);
    }

    public TicketStatus purchase() {
        throw new IllegalStateException("Cannot purchase ticket in state " + this);
    }

    public TicketStatus cancel() {
        throw new IllegalStateException("Cannot cancel ticket in state " + this);
    }

    public TicketStatus expire() {
        throw new IllegalStateException("Cannot expire ticket in state " + this);
    }
}
