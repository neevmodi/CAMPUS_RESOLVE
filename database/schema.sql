CREATE DATABASE IF NOT EXISTS campus_resolve;
USE campus_resolve;

CREATE TABLE IF NOT EXISTS users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(120) NOT NULL,
    email VARCHAR(190) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role ENUM('STUDENT','ADMIN','STAFF') NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE TABLE IF NOT EXISTS departments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(120) NOT NULL UNIQUE,
    description VARCHAR(255)
);
CREATE TABLE IF NOT EXISTS complaints (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    student_id BIGINT NOT NULL,
    title VARCHAR(180) NOT NULL,
    description TEXT NOT NULL,
    location VARCHAR(180) NOT NULL,
    category VARCHAR(80) NOT NULL,
    priority ENUM('LOW','MEDIUM','HIGH','CRITICAL') NOT NULL,
    department VARCHAR(120) NOT NULL,
    status ENUM('SUBMITTED','ANALYZED','ASSIGNED','IN_PROGRESS','RESOLVED','CLOSED') NOT NULL DEFAULT 'SUBMITTED',
    assigned_staff_id BIGINT NULL,
    classification_explanation VARCHAR(600),
    resolution_notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_complaint_student FOREIGN KEY (student_id) REFERENCES users(id),
    CONSTRAINT fk_complaint_staff FOREIGN KEY (assigned_staff_id) REFERENCES users(id)
);
CREATE TABLE IF NOT EXISTS complaint_history (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    complaint_id BIGINT NOT NULL,
    status ENUM('SUBMITTED','ANALYZED','ASSIGNED','IN_PROGRESS','RESOLVED','CLOSED') NOT NULL,
    changed_by BIGINT NOT NULL,
    note VARCHAR(500),
    changed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (complaint_id) REFERENCES complaints(id) ON DELETE CASCADE,
    FOREIGN KEY (changed_by) REFERENCES users(id)
);
CREATE INDEX idx_complaints_student ON complaints(student_id);
CREATE INDEX idx_complaints_status ON complaints(status);
CREATE INDEX idx_complaints_priority ON complaints(priority);
