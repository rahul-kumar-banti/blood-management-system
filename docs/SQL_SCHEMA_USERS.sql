-- ============================================================================
-- BLOOD BANK MANAGEMENT SYSTEM - USERS TABLE SCHEMA
-- ============================================================================

-- Create USERS table
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

-- Create indexes for better performance
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_role ON users(role);
CREATE INDEX idx_users_blood_type ON users(blood_type);
CREATE INDEX idx_users_is_active ON users(is_active);
CREATE INDEX idx_users_created_at ON users(created_at);

-- Create check constraints
ALTER TABLE users ADD CONSTRAINT chk_users_username_length 
    CHECK (LENGTH(username) >= 3 AND LENGTH(username) <= 50);

ALTER TABLE users ADD CONSTRAINT chk_users_password_length 
    CHECK (LENGTH(password) >= 6);

ALTER TABLE users ADD CONSTRAINT chk_users_role_valid 
    CHECK (role IN ('ADMIN', 'STAFF', 'DONOR', 'PATIENT'));

ALTER TABLE users ADD CONSTRAINT chk_users_blood_type_valid 
    CHECK (blood_type IN ('A_POSITIVE', 'A_NEGATIVE', 'B_POSITIVE', 'B_NEGATIVE', 
                         'AB_POSITIVE', 'AB_NEGATIVE', 'O_POSITIVE', 'O_NEGATIVE'));

-- Create trigger function for updating updated_at timestamp
CREATE OR REPLACE FUNCTION update_users_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Create trigger to automatically update updated_at
CREATE TRIGGER trigger_users_updated_at
    BEFORE UPDATE ON users
    FOR EACH ROW
    EXECUTE FUNCTION update_users_updated_at();

-- Insert sample admin user (password should be hashed in production)
INSERT INTO users (username, email, password, first_name, last_name, role, is_active) 
VALUES ('admin', 'admin@bloodbank.com', '$2a$10$encrypted_password_hash', 'System', 'Administrator', 'ADMIN', true);

-- Insert sample staff user
INSERT INTO users (username, email, password, first_name, last_name, role, is_active) 
VALUES ('staff', 'staff@bloodbank.com', '$2a$10$encrypted_password_hash', 'Staff', 'Member', 'STAFF', true);

-- Create comments for documentation
COMMENT ON TABLE users IS 'Stores user account information for the blood bank management system';
COMMENT ON COLUMN users.id IS 'Primary key, auto-incrementing';
COMMENT ON COLUMN users.username IS 'Unique username for login, 3-50 characters';
COMMENT ON COLUMN users.email IS 'Unique email address for user identification';
COMMENT ON COLUMN users.password IS 'Encrypted password, minimum 6 characters';
COMMENT ON COLUMN users.first_name IS 'User first name';
COMMENT ON COLUMN users.last_name IS 'User last name';
COMMENT ON COLUMN users.phone_number IS 'Contact phone number (optional)';
COMMENT ON COLUMN users.role IS 'User role: ADMIN, STAFF, DONOR, or PATIENT';
COMMENT ON COLUMN users.blood_type IS 'User blood type for donation/request matching';
COMMENT ON COLUMN users.is_active IS 'Whether the user account is active';
COMMENT ON COLUMN users.created_at IS 'Timestamp when user was created';
COMMENT ON COLUMN users.updated_at IS 'Timestamp when user was last updated';

-- Grant permissions (adjust as needed for your database setup)
-- GRANT SELECT, INSERT, UPDATE, DELETE ON users TO bloodbank_user;
-- GRANT USAGE, SELECT ON SEQUENCE users_id_seq TO bloodbank_user;
