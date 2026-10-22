USE campus_resolve;
INSERT INTO departments(name, description) VALUES
('Maintenance','Infrastructure, plumbing, electrical and facilities'),
('IT Support','Wi-Fi, systems and classroom technology'),
('Maintenance / Security','Safety incidents and urgent hazards'),
('Academic Affairs','Teaching and academic service concerns'),
('Administration','General campus services and cafeteria')
ON DUPLICATE KEY UPDATE description=VALUES(description);
-- Demo password for all accounts: Campus@123
INSERT INTO users(name,email,password_hash,role) VALUES
('System Administrator','admin@campusresolve.local','7258cf10f12c472bf65d024ade6a859bcda8ab8aecff9708c932f1866dc5621f','ADMIN'),
('Support Staff','staff@campusresolve.local','7258cf10f12c472bf65d024ade6a859bcda8ab8aecff9708c932f1866dc5621f','STAFF'),
('Demo Student','student@campusresolve.local','7258cf10f12c472bf65d024ade6a859bcda8ab8aecff9708c932f1866dc5621f','STUDENT')
ON DUPLICATE KEY UPDATE name=VALUES(name);
