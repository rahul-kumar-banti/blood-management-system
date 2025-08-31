-- ============================================================================
-- BLOOD BANK MANAGEMENT SYSTEM - REQUESTS TABLE SCHEMA
-- ============================================================================

-- Create REQUESTS table
CREATE TABLE requests (
    id BIGSERIAL PRIMARY KEY,
    requester_id BIGINT NOT NULL,
    hospital_name VARCHAR(200) NOT NULL,
    patient_name VARCHAR(200) NOT NULL,
    blood_type VARCHAR(10) NOT NULL,
    quantity INTEGER NOT NULL,
    unit_of_measure VARCHAR(10) NOT NULL DEFAULT 'ml',
    request_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    required_date TIMESTAMP NOT NULL,
    priority VARCHAR(20) NOT NULL DEFAULT 'NORMAL',
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    reason TEXT,
    doctor_name VARCHAR(200),
    contact_number VARCHAR(20),
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for better performance
CREATE INDEX idx_requests_requester_id ON requests(requester_id);
CREATE INDEX idx_requests_blood_type ON requests(blood_type);
CREATE INDEX idx_requests_status ON requests(status);
CREATE INDEX idx_requests_priority ON requests(priority);
CREATE INDEX idx_requests_request_date ON requests(request_date);
CREATE INDEX idx_requests_required_date ON requests(required_date);
CREATE INDEX idx_requests_hospital_name ON requests(hospital_name);
CREATE INDEX idx_requests_created_at ON requests(created_at);

-- Create composite indexes for common queries
CREATE INDEX idx_requests_type_status ON requests(blood_type, status);
CREATE INDEX idx_requests_priority_status ON requests(priority, status);
CREATE INDEX idx_requests_date_status ON requests(request_date, status);

-- Create foreign key constraint
ALTER TABLE requests 
ADD CONSTRAINT fk_requests_requester_id 
FOREIGN KEY (requester_id) REFERENCES users(id) ON DELETE CASCADE;

-- Create check constraints
ALTER TABLE requests ADD CONSTRAINT chk_requests_quantity_positive 
    CHECK (quantity > 0);

ALTER TABLE requests ADD CONSTRAINT chk_requests_blood_type_valid 
    CHECK (blood_type IN ('A_POSITIVE', 'A_NEGATIVE', 'B_POSITIVE', 'B_NEGATIVE', 
                         'AB_POSITIVE', 'AB_NEGATIVE', 'O_POSITIVE', 'O_NEGATIVE'));

ALTER TABLE requests ADD CONSTRAINT chk_requests_priority_valid 
    CHECK (priority IN ('LOW', 'NORMAL', 'HIGH', 'URGENT'));

ALTER TABLE requests ADD CONSTRAINT chk_requests_status_valid 
    CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED', 'FULFILLED', 'CANCELLED'));

ALTER TABLE requests ADD CONSTRAINT chk_requests_required_after_request 
    CHECK (required_date >= request_date);

ALTER TABLE requests ADD CONSTRAINT chk_requests_hospital_name_length 
    CHECK (LENGTH(hospital_name) >= 2);

ALTER TABLE requests ADD CONSTRAINT chk_requests_patient_name_length 
    CHECK (LENGTH(patient_name) >= 2);

-- Create trigger function for updating updated_at timestamp
CREATE OR REPLACE FUNCTION update_requests_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Create trigger to automatically update updated_at
CREATE TRIGGER trigger_requests_updated_at
    BEFORE UPDATE ON requests
    FOR EACH ROW
    EXECUTE FUNCTION update_requests_updated_at();

-- Create function to automatically update status based on required date
CREATE OR REPLACE FUNCTION update_requests_required_date_status()
RETURNS TRIGGER AS $$
BEGIN
    -- If required date has passed and status is still PENDING, mark as CANCELLED
    IF NEW.required_date < CURRENT_TIMESTAMP AND NEW.status = 'PENDING' THEN
        NEW.status = 'CANCELLED';
    END IF;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Create trigger to automatically update status based on required date
CREATE TRIGGER trigger_requests_required_date_status
    BEFORE INSERT OR UPDATE ON requests
    FOR EACH ROW
    EXECUTE FUNCTION update_requests_required_date_status();

-- Insert sample request data
INSERT INTO requests (requester_id, hospital_name, patient_name, blood_type, quantity, unit_of_measure, required_date, priority, status, reason, doctor_name, contact_number, notes) 
VALUES 
    (1, 'City General Hospital', 'John Smith', 'O_POSITIVE', 1000, 'ml', CURRENT_TIMESTAMP + INTERVAL '2 days', 'HIGH', 'PENDING', 'Emergency surgery', 'Dr. Johnson', '555-0123', 'Patient needs blood for emergency procedure'),
    (2, 'Memorial Medical Center', 'Sarah Wilson', 'A_POSITIVE', 500, 'ml', CURRENT_TIMESTAMP + INTERVAL '5 days', 'NORMAL', 'PENDING', 'Elective surgery', 'Dr. Davis', '555-0456', 'Scheduled surgery next week'),
    (3, 'Regional Hospital', 'Mike Brown', 'B_NEGATIVE', 750, 'ml', CURRENT_TIMESTAMP + INTERVAL '1 day', 'URGENT', 'PENDING', 'Accident victim', 'Dr. Miller', '555-0789', 'Critical patient, rare blood type needed');

-- Create comments for documentation
COMMENT ON TABLE requests IS 'Stores blood request records from hospitals and medical facilities';
COMMENT ON COLUMN requests.id IS 'Primary key, auto-incrementing';
COMMENT ON COLUMN requests.requester_id IS 'Foreign key reference to users table (person making request)';
COMMENT ON COLUMN requests.hospital_name IS 'Name of the hospital or medical facility';
COMMENT ON COLUMN requests.patient_name IS 'Name of the patient needing blood';
COMMENT ON COLUMN requests.blood_type IS 'Required blood type for the patient';
COMMENT ON COLUMN requests.quantity IS 'Amount of blood requested in units';
COMMENT ON COLUMN requests.unit_of_measure IS 'Unit of measurement (ml, units, etc.)';
COMMENT ON COLUMN requests.request_date IS 'Date and time when request was submitted';
COMMENT ON COLUMN requests.required_date IS 'Date when blood is needed by';
COMMENT ON COLUMN requests.priority IS 'Priority level of the request';
COMMENT ON COLUMN requests.status IS 'Current status of the blood request';
COMMENT ON COLUMN requests.reason IS 'Medical reason for the blood request';
COMMENT ON COLUMN requests.doctor_name IS 'Name of the requesting doctor';
COMMENT ON COLUMN requests.contact_number IS 'Contact phone number for the request';
COMMENT ON COLUMN requests.notes IS 'Additional notes about the request';
COMMENT ON COLUMN requests.created_at IS 'Timestamp when request record was created';
COMMENT ON COLUMN requests.updated_at IS 'Timestamp when request record was last updated';

-- Create view for urgent requests
CREATE VIEW urgent_requests AS
SELECT 
    r.id,
    r.hospital_name,
    r.patient_name,
    r.blood_type,
    r.quantity,
    r.unit_of_measure,
    r.required_date,
    r.priority,
    r.status,
    r.reason,
    r.doctor_name,
    r.contact_number,
    r.request_date,
    u.username as requester_username,
    u.email as requester_email
FROM requests r
JOIN users u ON r.requester_id = u.id
WHERE r.priority IN ('HIGH', 'URGENT') 
    AND r.status = 'PENDING'
    AND r.required_date <= CURRENT_TIMESTAMP + INTERVAL '3 days'
ORDER BY r.priority DESC, r.required_date ASC;

-- Create view for requests by blood type
CREATE VIEW requests_by_blood_type AS
SELECT 
    blood_type,
    COUNT(*) as total_requests,
    COUNT(CASE WHEN status = 'PENDING' THEN 1 END) as pending_requests,
    COUNT(CASE WHEN status = 'APPROVED' THEN 1 END) as approved_requests,
    COUNT(CASE WHEN status = 'FULFILLED' THEN 1 END) as fulfilled_requests,
    SUM(quantity) as total_quantity_requested
FROM requests 
GROUP BY blood_type
ORDER BY blood_type;

-- Create view for pending requests summary
CREATE VIEW pending_requests_summary AS
SELECT 
    r.blood_type,
    r.priority,
    COUNT(*) as request_count,
    SUM(r.quantity) as total_quantity,
    MIN(r.required_date) as earliest_required,
    MAX(r.required_date) as latest_required
FROM requests r
WHERE r.status = 'PENDING'
GROUP BY r.blood_type, r.priority
ORDER BY r.blood_type, 
    CASE r.priority 
        WHEN 'URGENT' THEN 1 
        WHEN 'HIGH' THEN 2 
        WHEN 'NORMAL' THEN 3 
        WHEN 'LOW' THEN 4 
    END;

-- Grant permissions (adjust as needed for your database setup)
-- GRANT SELECT, INSERT, UPDATE, DELETE ON requests TO bloodbank_user;
-- GRANT USAGE, SELECT ON SEQUENCE requests_id_seq TO bloodbank_user;
-- GRANT SELECT ON urgent_requests TO bloodbank_user;
-- GRANT SELECT ON requests_by_blood_type TO bloodbank_user;
-- GRANT SELECT ON pending_requests_summary TO bloodbank_user;
