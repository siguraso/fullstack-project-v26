-- Insert test tenant
INSERT INTO tenants (name, org_number, address, city, country, contact_email, contact_phone, active)
VALUES ('Kule kulinger AS', '123456789', 'Høgskoleringen 67', 'Trondheim', 'NO', 'contact@test.no', '+4798765432', TRUE);

-- Insert test users
-- Note: password is a bcrypt hash placeholder (replace with a real bcrypt hash if you want to login with these)
INSERT INTO users (tenant_id, role, first_name, last_name, contact_phone, active, username, email, password)
VALUES
    (1, 'ADMIN', 'Sigurd', 'Sigurdsson', '+4798765432', TRUE, 'sigurd', 'sigurd@test.no', '$2a$10$9q3.3q3q3q3q3q3q3q3q3q3q3q3q3q3q3q3q3q3q3q3q3q3q3q3q3q3'),
    (1, 'MANAGER', 'John', 'Doe', '+4798765432', TRUE, 'john', 'john@test.no', '$2a$10$9q3.3q3q3q3q3q3q3q3q3q3q3q3q3q3q3q3q3q3q3q3q3q3q3q3q3q3'),
    (1, 'STAFF', 'Jane', 'Smith', '+4798765433', TRUE, 'jane', 'jane@test.no', '$2a$10$9q3.3q3q3q3q3q3q3q3q3q3q3q3q3q3q3q3q3q3q3q3q3q3q3q3q3q3');
