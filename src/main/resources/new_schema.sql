CREATE DATABASE IF NOT EXISTS new_hope_bridge;
USE new_hope_bridge;

-- ================= USERS =================
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100),
    phone VARCHAR(20),
    role VARCHAR(50) NOT NULL,
    enabled BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- ================= ORPHANAGE =================
CREATE TABLE orphanage (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    admin_id BIGINT UNIQUE,
    name VARCHAR(150) NOT NULL,
    description TEXT,
    location VARCHAR(150),
    contact_person VARCHAR(100),
    contact_email VARCHAR(100),
    contact_phone VARCHAR(20),
    total_funds_received DECIMAL(12,2) DEFAULT 0,
    required_funds DECIMAL(12,2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (admin_id) REFERENCES users(id)
);

-- ================= DONORS =================
CREATE TABLE donors (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT UNIQUE,
    country VARCHAR(100),
    city VARCHAR(100),
    bio TEXT,
    total_donations DECIMAL(12,2) DEFAULT 0,
    impact_score INT DEFAULT 0,
    profile_image_url VARCHAR(255),
    is_verified BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- ================= DONATIONS =================
CREATE TABLE donations (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    donor_id BIGINT,
    orphanage_id BIGINT,
    amount DECIMAL(12,2) NOT NULL,
    purpose VARCHAR(150),
    description TEXT,
    donation_type VARCHAR(50),
    payment_method VARCHAR(50),
    transaction_id VARCHAR(255) UNIQUE,
    status VARCHAR(50),
    donation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (donor_id) REFERENCES donors(id),
    FOREIGN KEY (orphanage_id) REFERENCES orphanage(id)
);

-- ================= ALERTS =================
CREATE TABLE alerts (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT,
    orphanage_id BIGINT,
    message TEXT NOT NULL,
    alert_type VARCHAR(50),
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (orphanage_id) REFERENCES orphanage(id)
);

-- ================= RECEIPTS =================
CREATE TABLE receipts (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    donation_id BIGINT UNIQUE,
    receipt_number VARCHAR(50) UNIQUE,
    generated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (donation_id) REFERENCES donations(id)
);

-- ================= FUND USAGE =================
CREATE TABLE fund_usage (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(150),
    amount_used DECIMAL(12,2),
    used_on DATE
);