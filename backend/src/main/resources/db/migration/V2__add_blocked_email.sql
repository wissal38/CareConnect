-- Add table to store blocked emails
CREATE TABLE blocked_email (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    blocked_at DATETIME DEFAULT CURRENT_TIMESTAMP
);
