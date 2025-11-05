-- Add version and available_tickets to events table
ALTER TABLE events 
    ADD COLUMN IF NOT EXISTS version BIGINT DEFAULT 0,
    ADD COLUMN IF NOT EXISTS available_tickets INT DEFAULT 0;

-- Add status to orders table if it doesn't exist
ALTER TABLE orders 
    ADD COLUMN IF NOT EXISTS status VARCHAR(20) DEFAULT 'PENDING';

-- Update existing events to have available_tickets equal to the number of tickets
-- This is a one-time update for existing data
UPDATE events e
SET available_tickets = (
    SELECT COUNT(*)
    FROM tickets t
    WHERE t.event_id = e.id
);
