-- Run interactively as a MySQL administrator. Replace the placeholder before execution;
-- do not commit a real password. This does not alter application data.
CREATE USER IF NOT EXISTS 'campus_app'@'localhost' IDENTIFIED BY 'NEEV12@';
GRANT SELECT, INSERT, UPDATE ON campus_resolve.* TO 'campus_app'@'localhost';
FLUSH PRIVILEGES;
