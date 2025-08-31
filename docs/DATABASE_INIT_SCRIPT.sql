-- ============================================================================
-- BLOOD BANK MANAGEMENT SYSTEM - COMPLETE DATABASE INITIALIZATION SCRIPT
-- ============================================================================
-- This script creates the complete database structure for the Blood Bank Management System
-- Run this script in PostgreSQL to set up the entire database

-- Create database (run this separately if needed)
-- CREATE DATABASE bloodbank;
-- \c bloodbank;

-- ============================================================================
-- CREATE TABLES
-- ============================================================================

-- 1. USERS TABLE
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    phone_number VARCHAR(20),
    role VARCHAR(20) NOT NULL,
    blood_type VARCHAR(10),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 2. BLOOD_INVENTORY TABLE
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

-- 3. DONATIONS TABLE
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

-- 4. REQUESTS TABLE
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

-- ============================================================================
-- CREATE INDEXES
-- ============================================================================

-- Users table indexes
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_role ON users(role);
CREATE INDEX idx_users_blood_type ON users(blood_type);
CREATE INDEX idx_users_is_active ON users(is_active);
CREATE INDEX idx_users_created_at ON users(created_at);

-- Blood inventory table indexes
CREATE INDEX idx_blood_inventory_blood_type ON blood_inventory(blood_type);
CREATE INDEX idx_blood_inventory_status ON blood_inventory(status);
CREATE INDEX idx_blood_inventory_expiry_date ON blood_inventory(expiry_date);
CREATE INDEX idx_blood_inventory_collection_date ON blood_inventory(collection_date);
CREATE INDEX idx_blood_inventory_donor_id ON blood_inventory(donor_id);
CREATE INDEX idx_blood_inventory_batch_number ON blood_inventory(batch_number);
CREATE INDEX idx_blood_inventory_created_at ON blood_inventory(created_at);
CREATE INDEX idx_blood_inventory_type_status ON blood_inventory(blood_type, status);

-- Donations table indexes
CREATE INDEX idx_donations_donor_id ON donations(donor_id);
CREATE INDEX idx_donations_blood_type ON donations(blood_type);
CREATE INDEX idx_donations_status ON donations(status);
CREATE INDEX idx_donations_donation_date ON donations(donation_date);
CREATE INDEX idx_donations_expiry_date ON donations(expiry_date);
CREATE INDEX idx_donations_batch_number ON donations(batch_number);
CREATE INDEX idx_donations_created_at ON donations(created_at);

-- Requests table indexes
CREATE INDEX idx_requests_requester_id ON requests(requester_id);
CREATE INDEX idx_requests_blood_type ON requests(blood_type);
CREATE INDEX idx_requests_status ON requests(status);
CREATE INDEX idx_requests_priority ON requests(priority);
CREATE INDEX idx_requests_request_date ON requests(request_date);
CREATE INDEX idx_requests_required_date ON requests(required_date);
CREATE INDEX idx_requests_hospital_name ON requests(hospital_name);
CREATE INDEX idx_requests_created_at ON requests(created_at);
CREATE INDEX idx_requests_type_status ON requests(blood_type, status);
CREATE INDEX idx_requests_priority_status ON requests(priority, status);
CREATE INDEX idx_requests_date_status ON requests(request_date, status);

-- ============================================================================
-- CREATE FOREIGN KEY CONSTRAINTS
-- ============================================================================

-- Blood inventory foreign key
ALTER TABLE blood_inventory 
ADD CONSTRAINT fk_blood_inventory_donor_id 
FOREIGN KEY (donor_id) REFERENCES users(id) ON DELETE SET NULL;

-- Donations foreign key
ALTER TABLE donations 
ADD CONSTRAINT fk_donations_donor_id 
FOREIGN KEY (donor_id) REFERENCES users(id) ON DELETE CASCADE;

-- Requests foreign key
ALTER TABLE requests 
ADD CONSTRAINT fk_requests_requester_id 
FOREIGN KEY (requester_id) REFERENCES users(id) ON DELETE CASCADE;

-- ============================================================================
-- CREATE CHECK CONSTRAINTS
-- ============================================================================

-- Users table constraints
ALTER TABLE users ADD CONSTRAINT chk_users_username_length 
    CHECK (LENGTH(username) >= 3 AND LENGTH(username) <= 50);
ALTER TABLE users ADD CONSTRAINT chk_users_password_length 
    CHECK (LENGTH(password) >= 6);
ALTER TABLE users ADD CONSTRAINT chk_users_role_valid 
    CHECK (role IN ('ADMIN', 'STAFF', 'DONOR', 'PATIENT'));
ALTER TABLE users ADD CONSTRAINT chk_users_blood_type_valid 
    CHECK (blood_type IN ('A_POSITIVE', 'A_NEGATIVE', 'B_POSITIVE', 'B_NEGATIVE', 
                         'AB_POSITIVE', 'AB_NEGATIVE', 'O_POSITIVE', 'O_NEGATIVE'));

-- Blood inventory table constraints
ALTER TABLE blood_inventory ADD CONSTRAINT chk_blood_inventory_quantity_non_negative 
    CHECK (quantity >= 0);
ALTER TABLE blood_inventory ADD CONSTRAINT chk_blood_inventory_blood_type_valid 
    CHECK (blood_type IN ('A_POSITIVE', 'A_NEGATIVE', 'B_POSITIVE', 'B_NEGATIVE', 
                         'AB_POSITIVE', 'AB_NEGATIVE', 'O_POSITIVE', 'O_NEGATIVE'));
ALTER TABLE blood_inventory ADD CONSTRAINT chk_blood_inventory_status_valid 
    CHECK (status IN ('AVAILABLE', 'RESERVED', 'EXPIRED', 'DISCARDED', 'IN_TRANSIT'));
ALTER TABLE blood_inventory ADD CONSTRAINT chk_blood_inventory_expiry_after_collection 
    CHECK (expiry_date IS NULL OR collection_date IS NULL OR expiry_date > collection_date);

-- Donations table constraints
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

-- Requests table constraints
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

-- ============================================================================
-- CREATE TRIGGER FUNCTIONS
-- ============================================================================

-- Function to update updated_at timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Function to update blood inventory status based on expiry
CREATE OR REPLACE FUNCTION update_blood_inventory_expiry_status()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.expiry_date IS NOT NULL AND NEW.expiry_date < CURRENT_TIMESTAMP THEN
        NEW.status = 'EXPIRED';
    END IF;
    
    IF NEW.quantity = 0 THEN
        NEW.status = 'DISCARDED';
    END IF;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Function to update request status based on required date
CREATE OR REPLACE FUNCTION update_requests_required_date_status()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.required_date < CURRENT_TIMESTAMP AND NEW.status = 'PENDING' THEN
        NEW.status = 'CANCELLED';
    END IF;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- ============================================================================
-- CREATE TRIGGERS
-- ============================================================================

-- Updated_at triggers for all tables
CREATE TRIGGER trigger_users_updated_at
    BEFORE UPDATE ON users FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER trigger_blood_inventory_updated_at
    BEFORE UPDATE ON blood_inventory FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER trigger_donations_updated_at
    BEFORE UPDATE ON donations FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER trigger_requests_updated_at
    BEFORE UPDATE ON requests FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- Business logic triggers
CREATE TRIGGER trigger_blood_inventory_expiry_status
    BEFORE INSERT OR UPDATE ON blood_inventory FOR EACH ROW 
    EXECUTE FUNCTION update_blood_inventory_expiry_status();

CREATE TRIGGER trigger_requests_required_date_status
    BEFORE INSERT OR UPDATE ON requests FOR EACH ROW 
    EXECUTE FUNCTION update_requests_required_date_status();

-- ============================================================================
-- INSERT SAMPLE DATA
-- ============================================================================

-- Insert sample users
INSERT INTO users (username, email, password, first_name, last_name, role, blood_type, is_active) VALUES 
('admin', 'admin@bloodbank.com', '$2a$10$encrypted_password_hash', 'System', 'Administrator', 'ADMIN', 'O_POSITIVE', true),
('staff1', 'staff1@bloodbank.com', '$2a$10$encrypted_password_hash', 'John', 'Staff', 'STAFF', 'A_POSITIVE', true),
('staff2', 'staff2@bloodbank.com', '$2a$10$encrypted_password_hash', 'Jane', 'Nurse', 'STAFF', 'B_POSITIVE', true),
('donor1', 'donor1@email.com', '$2a$10$encrypted_password_hash', 'Mike', 'Donor', 'DONOR', 'O_POSITIVE', true),
('donor2', 'donor2@email.com', '$2a$10$encrypted_password_hash', 'Sarah', 'Donor', 'DONOR', 'A_NEGATIVE', true),
('patient1', 'patient1@email.com', '$2a$10$encrypted_password_hash', 'Bob', 'Patient', 'PATIENT', 'B_POSITIVE', true);

-- Insert sample blood inventory
INSERT INTO blood_inventory (blood_type, quantity, unit_of_measure, expiry_date, collection_date, donor_id, batch_number, status, notes) VALUES 
('O_POSITIVE', 2000, 'ml', CURRENT_TIMESTAMP + INTERVAL '30 days', CURRENT_TIMESTAMP - INTERVAL '5 days', 4, 'BATCH001', 'AVAILABLE', 'Fresh O+ blood'),
('A_POSITIVE', 1500, 'ml', CURRENT_TIMESTAMP + INTERVAL '25 days', CURRENT_TIMESTAMP - INTERVAL '3 days', 5, 'BATCH002', 'AVAILABLE', 'Fresh A+ blood'),
('B_NEGATIVE', 800, 'ml', CURRENT_TIMESTAMP + INTERVAL '20 days', CURRENT_TIMESTAMP - INTERVAL '7 days', 4, 'BATCH003', 'AVAILABLE', 'Rare B- blood type'),
('AB_POSITIVE', 1200, 'ml', CURRENT_TIMESTAMP + INTERVAL '15 days', CURRENT_TIMESTAMP - INTERVAL '2 days', 5, 'BATCH004', 'AVAILABLE', 'AB+ blood available');

-- Insert sample donations
INSERT INTO donations (donor_id, blood_type, quantity, unit_of_measure, donation_date, expiry_date, batch_number, status, health_screening_passed, hemoglobin_level, blood_pressure, pulse_rate, temperature, notes) VALUES 
(4, 'O_POSITIVE', 450, 'ml', CURRENT_TIMESTAMP - INTERVAL '5 days', CURRENT_TIMESTAMP + INTERVAL '37 days', 'BATCH001', 'COMPLETED', true, 14.5, '120/80', 72, 36.8, 'Healthy donor, good hemoglobin level'),
(5, 'A_NEGATIVE', 450, 'ml', CURRENT_TIMESTAMP - INTERVAL '3 days', CURRENT_TIMESTAMP + INTERVAL '39 days', 'BATCH002', 'COMPLETED', true, 13.8, '118/75', 68, 36.9, 'Healthy donor, normal vitals'),
(4, 'B_NEGATIVE', 450, 'ml', CURRENT_TIMESTAMP - INTERVAL '7 days', CURRENT_TIMESTAMP + INTERVAL '35 days', 'BATCH003', 'COMPLETED', true, 15.2, '125/82', 75, 37.1, 'Rare blood type, excellent donor');

-- Insert sample requests
INSERT INTO requests (requester_id, hospital_name, patient_name, blood_type, quantity, unit_of_measure, required_date, priority, status, reason, doctor_name, contact_number, notes) VALUES 
(6, 'City General Hospital', 'John Smith', 'O_POSITIVE', 1000, 'ml', CURRENT_TIMESTAMP + INTERVAL '2 days', 'HIGH', 'PENDING', 'Emergency surgery', 'Dr. Johnson', '555-0123', 'Patient needs blood for emergency procedure'),
(6, 'Memorial Medical Center', 'Sarah Wilson', 'A_POSITIVE', 500, 'ml', CURRENT_TIMESTAMP + INTERVAL '5 days', 'NORMAL', 'PENDING', 'Elective surgery', 'Dr. Davis', '555-0456', 'Scheduled surgery next week'),
(6, 'Regional Hospital', 'Mike Brown', 'B_NEGATIVE', 750, 'ml', CURRENT_TIMESTAMP + INTERVAL '1 day', 'URGENT', 'PENDING', 'Accident victim', 'Dr. Miller', '555-0789', 'Critical patient, rare blood type needed');

-- ============================================================================
-- CREATE VIEWS
-- ============================================================================

-- Available blood by type
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

-- Expiring blood soon
CREATE VIEW expiring_blood_soon AS
SELECT 
    id, blood_type, quantity, unit_of_measure, expiry_date, batch_number, notes
FROM blood_inventory 
WHERE status = 'AVAILABLE' 
    AND expiry_date IS NOT NULL 
    AND expiry_date <= CURRENT_TIMESTAMP + INTERVAL '7 days'
ORDER BY expiry_date;

-- Urgent requests
CREATE VIEW urgent_requests AS
SELECT 
    r.id, r.hospital_name, r.patient_name, r.blood_type, r.quantity,
    r.unit_of_measure, r.required_date, r.priority, r.status, r.reason,
    r.doctor_name, r.contact_number, r.request_date,
    u.username as requester_username, u.email as requester_email
FROM requests r
JOIN users u ON r.requester_id = u.id
WHERE r.priority IN ('HIGH', 'URGENT') 
    AND r.status = 'PENDING'
    AND r.required_date <= CURRENT_TIMESTAMP + INTERVAL '3 days'
ORDER BY r.priority DESC, r.required_date ASC;

-- Requests by blood type
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

-- ============================================================================
-- GRANT PERMISSIONS (ADJUST AS NEEDED)
-- ============================================================================

-- Uncomment and modify these lines based on your database setup
-- CREATE USER bloodbank_user WITH PASSWORD 'your_password';
-- GRANT CONNECT ON DATABASE bloodbank TO bloodbank_user;
-- GRANT USAGE ON SCHEMA public TO bloodbank_user;
-- GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO bloodbank_user;
-- GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA public TO bloodbank_user;
-- GRANT SELECT ON ALL VIEWS IN SCHEMA public TO bloodbank_user;

-- ============================================================================
-- VERIFICATION QUERIES
-- ============================================================================

-- Verify tables were created
SELECT table_name FROM information_schema.tables WHERE table_schema = 'public' ORDER BY table_name;

-- Verify sample data was inserted
SELECT 'Users' as table_name, COUNT(*) as record_count FROM users
UNION ALL
SELECT 'Blood Inventory', COUNT(*) FROM blood_inventory
UNION ALL
SELECT 'Donations', COUNT(*) FROM donations
UNION ALL
SELECT 'Requests', COUNT(*) FROM requests;

-- Verify constraints
SELECT constraint_name, table_name, constraint_type 
FROM information_schema.table_constraints 
WHERE table_schema = 'public' 
ORDER BY table_name, constraint_type;

-- Verify indexes
SELECT indexname, tablename FROM pg_indexes WHERE schemaname = 'public' ORDER BY tablename, indexname;

-- ============================================================================
-- SCRIPT COMPLETION MESSAGE
-- ============================================================================
DO $$
BEGIN
    RAISE NOTICE 'Blood Bank Management System database initialization completed successfully!';
    RAISE NOTICE 'Tables created: users, blood_inventory, donations, requests';
    RAISE NOTICE 'Sample data inserted for testing';
    RAISE NOTICE 'Views created for common queries';
    RAISE NOTICE 'All constraints and triggers configured';
END $$;
