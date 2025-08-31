-- ============================================================================
-- BLOOD BANK MANAGEMENT SYSTEM - BLOOD_INVENTORY TABLE SCHEMA
-- ============================================================================

-- Create BLOOD_INVENTORY table
CREATE TABLE blood_inventory (
    id BIGSERIAL PRIMARY KEY,
    blood_type VARCHAR(10) NOT NULL,
    quantity INTEGER NOT NULL,
    unit_of_measure VARCHAR(10) NOT NULL DEFAULT 'ml',
    expiry_date TIMESTAMP,
    collection_date TIMESTAMP,
    donor_id BIGINT,
    batch_number VARCHAR(50),
    status VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE',
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for better performance
CREATE INDEX idx_blood_inventory_blood_type ON blood_inventory(blood_type);
CREATE INDEX idx_blood_inventory_status ON blood_inventory(status);
CREATE INDEX idx_blood_inventory_expiry_date ON blood_inventory(expiry_date);
CREATE INDEX idx_blood_inventory_collection_date ON blood_inventory(collection_date);
CREATE INDEX idx_blood_inventory_donor_id ON blood_inventory(donor_id);
CREATE INDEX idx_blood_inventory_batch_number ON blood_inventory(batch_number);
CREATE INDEX idx_blood_inventory_created_at ON blood_inventory(created_at);

-- Create composite index for common queries
CREATE INDEX idx_blood_inventory_type_status ON blood_inventory(blood_type, status);

-- Create foreign key constraint (optional, can be NULL)
ALTER TABLE blood_inventory 
ADD CONSTRAINT fk_blood_inventory_donor_id 
FOREIGN KEY (donor_id) REFERENCES users(id) ON DELETE SET NULL;

-- Create check constraints
ALTER TABLE blood_inventory ADD CONSTRAINT chk_blood_inventory_quantity_non_negative 
    CHECK (quantity >= 0);

ALTER TABLE blood_inventory ADD CONSTRAINT chk_blood_inventory_blood_type_valid 
    CHECK (blood_type IN ('A_POSITIVE', 'A_NEGATIVE', 'B_POSITIVE', 'B_NEGATIVE', 
                         'AB_POSITIVE', 'AB_NEGATIVE', 'O_POSITIVE', 'O_NEGATIVE'));

ALTER TABLE blood_inventory ADD CONSTRAINT chk_blood_inventory_status_valid 
    CHECK (status IN ('AVAILABLE', 'RESERVED', 'EXPIRED', 'DISCARDED', 'IN_TRANSIT'));

ALTER TABLE blood_inventory ADD CONSTRAINT chk_blood_inventory_expiry_after_collection 
    CHECK (expiry_date IS NULL OR collection_date IS NULL OR expiry_date > collection_date);

-- Create trigger function for updating updated_at timestamp
CREATE OR REPLACE FUNCTION update_blood_inventory_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Create trigger to automatically update updated_at
CREATE TRIGGER trigger_blood_inventory_updated_at
    BEFORE UPDATE ON blood_inventory
    FOR EACH ROW
    EXECUTE FUNCTION update_blood_inventory_updated_at();

-- Create function to automatically update status based on expiry date
CREATE OR REPLACE FUNCTION update_blood_inventory_expiry_status()
RETURNS TRIGGER AS $$
BEGIN
    -- If expiry date has passed, mark as EXPIRED
    IF NEW.expiry_date IS NOT NULL AND NEW.expiry_date < CURRENT_TIMESTAMP THEN
        NEW.status = 'EXPIRED';
    END IF;
    
    -- If quantity becomes 0, mark as DISCARDED
    IF NEW.quantity = 0 THEN
        NEW.status = 'DISCARDED';
    END IF;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Create trigger to automatically update status based on expiry
CREATE TRIGGER trigger_blood_inventory_expiry_status
    BEFORE INSERT OR UPDATE ON blood_inventory
    FOR EACH ROW
    EXECUTE FUNCTION update_blood_inventory_expiry_status();

-- Insert sample blood inventory data
INSERT INTO blood_inventory (blood_type, quantity, unit_of_measure, expiry_date, collection_date, donor_id, batch_number, status, notes) 
VALUES 
    ('O_POSITIVE', 2000, 'ml', CURRENT_TIMESTAMP + INTERVAL '30 days', CURRENT_TIMESTAMP - INTERVAL '5 days', 1, 'BATCH001', 'AVAILABLE', 'Fresh O+ blood'),
    ('A_POSITIVE', 1500, 'ml', CURRENT_TIMESTAMP + INTERVAL '25 days', CURRENT_TIMESTAMP - INTERVAL '3 days', 2, 'BATCH002', 'AVAILABLE', 'Fresh A+ blood'),
    ('B_NEGATIVE', 800, 'ml', CURRENT_TIMESTAMP + INTERVAL '20 days', CURRENT_TIMESTAMP - INTERVAL '7 days', 3, 'BATCH003', 'AVAILABLE', 'Rare B- blood type');

-- Create comments for documentation
COMMENT ON TABLE blood_inventory IS 'Stores current blood inventory levels and availability status';
COMMENT ON COLUMN blood_inventory.id IS 'Primary key, auto-incrementing';
COMMENT ON COLUMN blood_inventory.blood_type IS 'Blood type of the inventory item';
COMMENT ON COLUMN blood_inventory.quantity IS 'Available quantity in units';
COMMENT ON COLUMN blood_inventory.unit_of_measure IS 'Unit of measurement (ml, units, etc.)';
COMMENT ON COLUMN blood_inventory.expiry_date IS 'Date when blood expires (NULL if not applicable)';
COMMENT ON COLUMN blood_inventory.collection_date IS 'Date when blood was collected (NULL if not applicable)';
COMMENT ON COLUMN blood_inventory.donor_id IS 'Foreign key reference to users table (can be NULL for pooled blood)';
COMMENT ON COLUMN blood_inventory.batch_number IS 'Batch identifier for tracking (can be NULL)';
COMMENT ON COLUMN blood_inventory.status IS 'Current status of the blood inventory';
COMMENT ON COLUMN blood_inventory.notes IS 'Additional notes about the inventory item';
COMMENT ON COLUMN blood_inventory.created_at IS 'Timestamp when inventory record was created';
COMMENT ON COLUMN blood_inventory.updated_at IS 'Timestamp when inventory record was last updated';

-- Create view for available blood by type
CREATE VIEW available_blood_by_type AS
SELECT 
    blood_type,
    SUM(quantity) as total_available,
    COUNT(*) as number_of_batches,
    MIN(expiry_date) as earliest_expiry
FROM blood_inventory 
WHERE status = 'AVAILABLE' AND quantity > 0
GROUP BY blood_type
ORDER BY blood_type;

-- Create view for expiring blood (within 7 days)
CREATE VIEW expiring_blood_soon AS
SELECT 
    id,
    blood_type,
    quantity,
    unit_of_measure,
    expiry_date,
    batch_number,
    notes
FROM blood_inventory 
WHERE status = 'AVAILABLE' 
    AND expiry_date IS NOT NULL 
    AND expiry_date <= CURRENT_TIMESTAMP + INTERVAL '7 days'
ORDER BY expiry_date;

-- Grant permissions (adjust as needed for your database setup)
-- GRANT SELECT, INSERT, UPDATE, DELETE ON blood_inventory TO bloodbank_user;
-- GRANT USAGE, SELECT ON SEQUENCE blood_inventory_id_seq TO bloodbank_user;
-- GRANT SELECT ON available_blood_by_type TO bloodbank_user;
-- GRANT SELECT ON expiring_blood_soon TO bloodbank_user;
