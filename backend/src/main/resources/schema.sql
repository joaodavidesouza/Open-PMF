-- Create the table structure if it doesn't exist.
-- Using TIMESTAMPTZ ensures Timezone-agnostic data storage (Cloud Ready).
CREATE TABLE IF NOT EXISTS sensor_measurements (
    id BIGSERIAL,
    machine_id VARCHAR(255) NOT NULL,
    vibration DOUBLE PRECISION NOT NULL,
    timestamp TIMESTAMPTZ NOT NULL,
    PRIMARY KEY (id, timestamp)
);

-- TimescaleDB Optimization: 
-- We only apply hypertable partitioning if the extension is available.
-- This keeps the framework compatible with standard PostgreSQL (DB Agnostic).
DO $$ 
BEGIN
    IF EXISTS (SELECT 1 FROM pg_extension WHERE extname = 'timescaledb') THEN
        PERFORM create_hypertable('sensor_measurements', 'timestamp', if_not_exists => TRUE);
    END IF;
END $$;