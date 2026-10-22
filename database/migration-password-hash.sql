USE campus_resolve;

-- Safe, non-destructive migration for PBKDF2 password hashes.
-- Existing 64-character SHA-256 seed hashes remain valid and are upgraded
-- automatically after a successful login.
ALTER TABLE users MODIFY password_hash VARCHAR(255) NOT NULL;
