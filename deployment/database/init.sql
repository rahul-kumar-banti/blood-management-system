-- Blood Bank Management System Database Initialization Script
-- Run this script after creating the database

-- Create admin user (password: admin123)
INSERT INTO users (username, email, password, first_name, last_name, role, blood_type, is_active, created_at, updated_at)
VALUES (
    'admin',
    'admin@bloodbank.com',
    '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', -- admin123
    'System',
    'Administrator',
    'ADMIN',
    'O_POSITIVE',
    true,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- Create sample doctor user (password: doctor123)
INSERT INTO users (username, email, password, first_name, last_name, role, blood_type, is_active, created_at, updated_at)
VALUES (
    'doctor1',
    'doctor@hospital.com',
    '$2a$10$8K1p/a0dL1LXMIgoEDFrwOfgqwAGxwHfHhK3VqHqHqHqHqHqHqHqHq', -- doctor123
    'Dr. John',
    'Smith',
    'DOCTOR',
    'A_POSITIVE',
    true,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- Create sample nurse user (password: nurse123)
INSERT INTO users (username, email, password, first_name, last_name, role, blood_type, is_active, created_at, updated_at)
VALUES (
    'nurse1',
    'nurse@hospital.com',
    '$2a$10$8K1p/a0dL1LXMIgoEDFrwOfgqwAGxwHfHhK3VqHqHqHqHqHqHqHqHq', -- nurse123
    'Nurse',
    'Johnson',
    'NURSE',
    'B_POSITIVE',
    true,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- Create sample technician user (password: tech123)
INSERT INTO users (username, email, password, first_name, last_name, role, blood_type, is_active, created_at, updated_at)
VALUES (
    'tech1',
    'tech@bloodbank.com',
    '$2a$10$8K1p/a0dL1LXMIgoEDFrwOfgqwAGxwHfHhK3VqHqHqHqHqHqHqHqHq', -- tech123
    'Technician',
    'Wilson',
    'TECHNICIAN',
    'AB_POSITIVE',
    true,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- Create sample donor users (password: donor123)
INSERT INTO users (username, email, password, first_name, last_name, role, blood_type, is_active, created_at, updated_at)
VALUES 
    ('donor1', 'donor1@email.com', '$2a$10$8K1p/a0dL1LXMIgoEDFrwOfgqwAGxwHfHhK3VqHqHqHqHqHqHqHqHq', 'Alice', 'Brown', 'DONOR', 'O_POSITIVE', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('donor2', 'donor2@email.com', '$2a$10$8K1p/a0dL1LXMIgoEDFrwOfgqwAGxwHfHhK3VqHqHqHqHqHqHqHqHq', 'Bob', 'Davis', 'DONOR', 'A_NEGATIVE', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('donor3', 'donor3@email.com', '$2a$10$8K1p/a0dL1LXMIgoEDFrwOfgqwAGxwHfHhK3VqHqHqHqHqHqHqHqHq', 'Carol', 'Miller', 'DONOR', 'B_POSITIVE', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('donor4', 'donor4@email.com', '$2a$10$8K1p/a0dL1LXMIgoEDFrwOfgqwAGxwHfHhK3VqHqHqHqHqHqHqHqHq', 'David', 'Wilson', 'DONOR', 'AB_NEGATIVE', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Create sample blood inventory
INSERT INTO blood_inventory (blood_type, quantity, unit_of_measure, expiry_date, collection_date, donor_id, batch_number, status, notes, created_at, updated_at)
VALUES 
    ('O_POSITIVE', 500, 'ml', CURRENT_TIMESTAMP + INTERVAL '30 days', CURRENT_TIMESTAMP - INTERVAL '5 days', 5, 'BATCH001', 'AVAILABLE', 'Fresh donation', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('A_NEGATIVE', 300, 'ml', CURRENT_TIMESTAMP + INTERVAL '25 days', CURRENT_TIMESTAMP - INTERVAL '3 days', 6, 'BATCH002', 'AVAILABLE', 'Rare blood type', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('B_POSITIVE', 400, 'ml', CURRENT_TIMESTAMP + INTERVAL '28 days', CURRENT_TIMESTAMP - INTERVAL '4 days', 7, 'BATCH003', 'AVAILABLE', 'Regular donation', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('AB_NEGATIVE', 200, 'ml', CURRENT_TIMESTAMP + INTERVAL '20 days', CURRENT_TIMESTAMP - INTERVAL '2 days', 8, 'BATCH004', 'AVAILABLE', 'Very rare blood type', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('O_NEGATIVE', 350, 'ml', CURRENT_TIMESTAMP + INTERVAL '32 days', CURRENT_TIMESTAMP - INTERVAL '6 days', 5, 'BATCH005', 'AVAILABLE', 'Universal donor blood', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Create sample donation records
INSERT INTO donations (donor_id, blood_type, quantity, unit_of_measure, donation_date, expiry_date, batch_number, status, health_screening_passed, hemoglobin_level, blood_pressure, pulse_rate, temperature, created_at, updated_at)
VALUES 
    (5, 'O_POSITIVE', 500, 'ml', CURRENT_TIMESTAMP - INTERVAL '5 days', CURRENT_TIMESTAMP + INTERVAL '30 days', 'BATCH001', 'APPROVED', true, 14.2, '120/80', 72, 98.6, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (6, 'A_NEGATIVE', 300, 'ml', CURRENT_TIMESTAMP - INTERVAL '3 days', CURRENT_TIMESTAMP + INTERVAL '25 days', 'BATCH002', 'APPROVED', true, 13.8, '118/78', 68, 98.4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (7, 'B_POSITIVE', 400, 'ml', CURRENT_TIMESTAMP - INTERVAL '4 days', CURRENT_TIMESTAMP + INTERVAL '28 days', 'BATCH003', 'APPROVED', true, 14.5, '122/82', 70, 98.7, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (8, 'AB_NEGATIVE', 200, 'ml', CURRENT_TIMESTAMP - INTERVAL '2 days', CURRENT_TIMESTAMP + INTERVAL '20 days', 'BATCH004', 'APPROVED', true, 13.9, '119/79', 65, 98.3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (5, 'O_NEGATIVE', 350, 'ml', CURRENT_TIMESTAMP - INTERVAL '6 days', CURRENT_TIMESTAMP + INTERVAL '32 days', 'BATCH005', 'APPROVED', true, 14.1, '121/81', 71, 98.5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Create sample blood requests
INSERT INTO requests (requester_id, hospital_name, patient_name, blood_type, quantity, unit_of_measure, request_date, required_date, priority, status, reason, doctor_name, contact_number, created_at, updated_at)
VALUES 
    (2, 'City General Hospital', 'Patient A', 'O_POSITIVE', 200, 'ml', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '2 days', 'HIGH', 'PENDING', 'Emergency surgery', 'Dr. John Smith', '+1234567890', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (3, 'Memorial Hospital', 'Patient B', 'A_NEGATIVE', 150, 'ml', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '1 day', 'URGENT', 'PENDING', 'Blood transfusion needed', 'Dr. Johnson', '+1234567891', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (2, 'City General Hospital', 'Patient C', 'B_POSITIVE', 300, 'ml', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '3 days', 'NORMAL', 'PENDING', 'Scheduled surgery', 'Dr. John Smith', '+1234567890', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Display sample data
SELECT 'Users created:' as info;
SELECT username, email, role, blood_type FROM users;

SELECT 'Blood inventory created:' as info;
SELECT blood_type, quantity, status, expiry_date FROM blood_inventory;

SELECT 'Donations created:' as info;
SELECT donor_id, blood_type, quantity, status FROM donations;

SELECT 'Requests created:' as info;
SELECT hospital_name, patient_name, blood_type, priority, status FROM requests;
