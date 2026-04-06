CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE events (
    id          UUID            PRIMARY KEY DEFAULT gen_random_uuid(),
    title       VARCHAR(255)    NOT NULL,
    description TEXT,
    event_date  DATE            NOT NULL,
    event_time  TIME            NOT NULL,
    location    VARCHAR(255)    NOT NULL,
    category    VARCHAR(100)    NOT NULL,
    capacity    INTEGER         NOT NULL CONSTRAINT capacity_non_negative CHECK (capacity >= 0),
    price       NUMERIC(10, 2)  NOT NULL DEFAULT 0 CONSTRAINT price_non_negative CHECK (price >= 0),
    organizer   VARCHAR(255)    NOT NULL,
    status      VARCHAR(20)     NOT NULL DEFAULT 'draft'
                                CONSTRAINT valid_status CHECK (status IN ('draft', 'published', 'cancelled')),
    created_at  TIMESTAMPTZ     NOT NULL DEFAULT now(),
    updated_at  TIMESTAMPTZ     NOT NULL DEFAULT now()
);

CREATE INDEX idx_events_status   ON events(status);
CREATE INDEX idx_events_category ON events(category);
CREATE INDEX idx_events_date     ON events(event_date);
