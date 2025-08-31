-- ============================================================================
-- BLOOD BANK MANAGEMENT SYSTEM - DONATIONS TABLE SCHEMA
-- ============================================================================

-- Create DONATIONS table
CREATE TABLE donations (
    id BIGSERIAL PRIMARY KEY,
    donor_id BIGINT NOT NULL,
    blood_type VARCHAR(10) NOT NULL,
    quantity INTEGER NOT NULL,
    unit_of_measure VARCHAR(10) NOT NULL DEFAULT 'ml',
    donation_date TIMESTAMP NOT NULL,
    expiry_date TIMESTAMP NOT NULL,
    batch_number VARCHAR(50) UNIQUE NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    health_screening_passed BOOLEAN,
    hemoglobin_level DECIMAL(5,2),
    blood_pressure VARCHAR(20),
    pulse_rate INTEGER,
    temperature DECIMAL(4,2),
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for better performance
CREATE INDEX idx_donations_donor_id ON donations(donor_id);
CREATE INDEX idx_donations_blood_type ON donations(blood_type);
CREATE INDEX idx_donations_status ON donations(status);
CREATE INDEX idx_donations_donation_date ON donations(donation_date);
CREATE INDEX idx_donations_expiry_date ON donations(expiry_date);
CREATE INDEX idx_donations_batch_number ON donations(batch_number);
CREATE INDEX idx_donations_created_at ON donations(created_at);

-- Create foreign key constraint
ALTER TABLE donations 
ADD CONSTRAINT fk_donations_donor_id 
FOREIGN KEY (donor_id) REFERENCES users(id) ON DELETE CASCADE;

-- Create check constraints
ALTER TABLE donations ADD CONSTRAINT chk_donations_quantity_positive 
    CHECK (quantity > 0);

ALTER TABLE donations ADD CONSTRAINT chk_donations_blood_type_valid 
    CHECK (blood_type IN ('A_POSITIVE', 'A_NEGATIVE', 'B_POSITIVE', 'B_NEGATIVE', 
                         'AB_POSITIVE', 'AB_NEGATIVE', 'O_POSITIVE', 'O_NEGATIVE'));

ALTER TABLE donations ADD CONSTRAINT chk_donations_status_valid 
    CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED', 'COMPLETED', 'CANCELLED'));

ALTER TABLE donations ADD CONSTRAINT chk_donations_expiry_after_donation 
    CHECK (expiry_date > donation_date);

ALTER TABLE donations ADD CONSTRAINT chk_donations_hemoglobin_range 
    CHECK (hemoglobin_level IS NULL OR (hemoglobin_level >= 7.0 AND hemoglobin_level <= 20.0));

ALTER TABLE donations ADD CONSTRAINT chk_donations_pulse_rate_range 
    CHECK (pulse_rate IS NULL OR (pulse_rate >= 40 AND pulse_rate <= 200));

ALTER TABLE donations ADD CONSTRAINT chk_donations_temperature_range 
    CHECK (temperature IS NULL OR (temperature >= 35.0 AND temperature <= 42.0));

-- Create trigger function for updating updated_at timestamp
CREATE OR REPLACE FUNCTION update_donations_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Create trigger to automatically update updated_at
CREATE TRIGGER trigger_donations_updated_at
    BEFORE UPDATE ON donations
    FOR EACH ROW
    EXECUTE FUNCTION update_donations_updated_at();

-- Insert sample donation data
INSERT INTO donations (donor_id, blood_type, quantity, unit_of_measure, donation_date, expiry_date, batch_number, status, health_screening_passed, hemoglobin_level, blood_pressure, pulse_rate, temperature, notes) 
VALUES (1, 'O_POSITIVE', 450, 'ml', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '42 days', 'BATCH001', 'COMPLETED', true, 14.5, '120/80', 72, 36.8, 'Healthy donor, good hemoglobin level');

-- Create comments for documentation
COMMENT ON TABLE donations IS 'Stores blood donation records with health screening information';
COMMENT ON COLUMN donations.id IS 'Primary key, auto-incrementing';
COMMENT ON COLUMN donations.donor_id IS 'Foreign key reference to users table (donor)';
COMMENT ON COLUMN donations.blood_type IS 'Blood type of the donated blood';
COMMENT ON COLUMN donations.quantity IS 'Amount of blood donated in units';
COMMENT ON COLUMN donations.unit_of_measure IS 'Unit of measurement (ml, units, etc.)';
COMMENT ON COLUMN donations.donation_date IS 'Date and time when donation was made';
COMMENT ON COLUMN donations.expiry_date IS 'Date when donated blood expires';
COMMENT ON COLUMN donations.batch_number IS 'Unique batch identifier for tracking';
COMMENT ON COLUMN donations.status IS 'Current status of the donation';
COMMENT ON COLUMN donations.health_screening_passed IS 'Whether donor passed health screening';
COMMENT ON COLUMN donations.hemoglobin_level IS 'Donor hemoglobin level (g/dL)';
COMMENT ON COLUMN donations.blood_pressure IS 'Donor blood pressure reading';
COMMENT ON COLUMN donations.pulse_rate IS 'Donor pulse rate (beats per minute)';
COMMENT ON COLUMN donations.temperature IS 'Donor body temperature (°C)';
COMMENT ON COLUMN donations.notes IS 'Additional notes about the donation';
COMMENT ON COLUMN donations.created_at IS 'Timestamp when donation record was created';
COMMENT ON COLUMN donations.updated_at IS 'Timestamp when donation record was last updated';

-- Grant permissions (adjust as needed for your database setup)
-- GRANT SELECT, INSERT, UPDATE, DELETE ON donations TO bloodbank_user;
-- GRANT USAGE, SELECT ON SEQUENCE donations_id_seq TO bloodbank_user;
