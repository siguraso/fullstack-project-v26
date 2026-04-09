-- Insert test tenants
INSERT INTO tenants (name, org_number, address, city, country, contact_email, contact_phone, active)
VALUES
    ('Kule kulinger AS', '123456789', 'Høgskoleringen 67', 'Trondheim', 'NO', 'contact@kulekuinger.no', '+4798765432', TRUE),
    ('Everest Sushi & Fusion AS', '937219997', 'Nedre Elvehavn 4', 'Trondheim', 'NO', 'post@everestsushi.no', '+4791234567', TRUE),
    ('Oslo Bar & Grill AS', '111222333', 'Karl Johans gate 12', 'Oslo', 'NO', 'contact@oslobar.no', '+4745678901', TRUE);

-- Insert test users (tenant 1)
INSERT INTO users (tenant_id, role, first_name, last_name, contact_phone, active, username, email, password)
VALUES
    (1, 'ADMIN',   'Sigurd', 'Sigurdsson', '+4798765432', TRUE, 'sigurd', 'sigurd@test.no', '$2a$12$0hWR2qbEgjJSg7X5u/TJUOGWz4XHw7rJHqznC1Z.Yx.2sDIw8E1.O'), -- Password123
    (1, 'MANAGER', 'John',   'Doe',        '+4798765432', TRUE, 'john',   'john@test.no',   '$2a$12$iskMl0/cnCHZeXpVr37hWOP.eVgNKjhm9YfDJs/U240nWPQBPkIim'), -- Sigurd123
    (1, 'STAFF',   'Jane',   'Smith',      '+4798765433', TRUE, 'jane',   'jane@test.no',   '$2a$12$xV6Xzzw4iTqUAcz//DAK.unAwAR455e5c2OlaUqoq8.drVFLWewWK'), -- trygtpassord!
    (1, 'STAFF',   'Ole',    'Hansen',     '+4798761111', TRUE, 'ole',    'ole@test.no',    '$2a$12$xV6Xzzw4iTqUAcz//DAK.unAwAR455e5c2OlaUqoq8.drVFLWewWK'), -- trygtpassord!
    (1, 'STAFF',   'Kari',   'Nordmann',   '+4798762222', TRUE, 'kari',  'kari@test.no',    '$2a$12$xV6Xzzw4iTqUAcz//DAK.unAwAR455e5c2OlaUqoq8.drVFLWewWK'); -- trygtpassord!

-- Insert test users (tenant 2)
INSERT INTO users (tenant_id, role, first_name, last_name, contact_phone, active, username, email, password)
VALUES
    (2, 'ADMIN',   'Priya', 'Sharma', '+4792345678', TRUE, 'priya', 'priya@everestsushi.no', '$2a$12$0hWR2qbEgjJSg7X5u/TJUOGWz4XHw7rJHqznC1Z.Yx.2sDIw8E1.O'), -- Password123
    (2, 'MANAGER', 'Raj',   'Patel',  '+4792345679', TRUE, 'raj',   'raj@everestsushi.no',   '$2a$12$xV6Xzzw4iTqUAcz//DAK.unAwAR455e5c2OlaUqoq8.drVFLWewWK'), -- trygtpassord!
    (2, 'STAFF',   'Anita', 'Kumar',  '+4792345680', TRUE, 'anita', 'anita@everestsushi.no', '$2a$12$xV6Xzzw4iTqUAcz//DAK.unAwAR455e5c2OlaUqoq8.drVFLWewWK'); -- trygtpassord!

-- Insert test users (tenant 3)
INSERT INTO users (tenant_id, role, first_name, last_name, contact_phone, active, username, email, password)
VALUES
    (3, 'ADMIN',   'Lars',   'Berg',  '+4745678902', TRUE, 'lars',   'lars@oslobar.no',   '$2a$12$0hWR2qbEgjJSg7X5u/TJUOGWz4XHw7rJHqznC1Z.Yx.2sDIw8E1.O'), -- Password123
    (3, 'MANAGER', 'Nina',   'Dahl',  '+4745678903', TRUE, 'nina',   'nina@oslobar.no',   '$2a$12$xV6Xzzw4iTqUAcz//DAK.unAwAR455e5c2OlaUqoq8.drVFLWewWK'), -- trygtpassord!
    (3, 'STAFF',   'Morten', 'Lie',   '+4745678904', TRUE, 'morten', 'morten@oslobar.no', '$2a$12$xV6Xzzw4iTqUAcz//DAK.unAwAR455e5c2OlaUqoq8.drVFLWewWK'); -- trygtpassord!

-- Temperature zones (tenant 1)
INSERT INTO temperature_zones (tenant_id, name, lower_limit_celsius, upper_limit_celsius, active)
VALUES
    (1, 'Cold room 1',    0.0,   4.0,  TRUE),
    (1, 'Freezer room', -22.0, -18.0,  TRUE),
    (1, 'Display fridge', 0.0,   6.0,  TRUE),
    (1, 'Wine fridge',    8.0,  12.0,  TRUE),
    (1, 'Hot holding',   63.0,  75.0,  TRUE);

-- Temperature zones (tenant 2)
INSERT INTO temperature_zones (tenant_id, name, lower_limit_celsius, upper_limit_celsius, active)
VALUES
    (2, 'Fish storage',  0.0,   2.0,  TRUE),
    (2, 'Freezer 1',   -22.0, -18.0,  TRUE),
    (2, 'Rice warmer',  60.0,  75.0,  TRUE);

-- Temperature zones (tenant 3)
INSERT INTO temperature_zones (tenant_id, name, lower_limit_celsius, upper_limit_celsius, active)
VALUES
    (3, 'Bar fridge',   2.0,   6.0,  TRUE),
    (3, 'Freezer bar', -22.0, -18.0,  TRUE);

-- Temperature logs (tenant 1)
INSERT INTO temperature_compliance_logs (tenant_id, recorded_by, module, title, description, log_status, recorded_at, temperature_zone_id, temperature_celsius)
VALUES
    (1, 2, 'IK_FOOD', 'Temperature reading - Cold room 1',    'Opening shift check',              'OK',       DATEADD('DAY', -7, CURRENT_TIMESTAMP), 1,  3.1),
    (1, 3, 'IK_FOOD', 'Temperature reading - Display fridge', 'Slightly above target',             'WARNING',  DATEADD('DAY', -6, CURRENT_TIMESTAMP), 3,  7.2),
    (1, 4, 'IK_FOOD', 'Temperature reading - Cold room 1',    'Midday check',                      'OK',       DATEADD('DAY', -5, CURRENT_TIMESTAMP), 1,  3.8),
    (1, 3, 'IK_FOOD', 'Temperature reading - Freezer room',   'Evening check',                     'OK',       DATEADD('DAY', -5, CURRENT_TIMESTAMP), 2, -19.5),
    (1, 4, 'IK_FOOD', 'Temperature reading - Freezer room',   'Door left open - temperature rose', 'CRITICAL', DATEADD('DAY', -4, CURRENT_TIMESTAMP), 2, -10.0),
    (1, 2, 'IK_FOOD', 'Temperature reading - Wine fridge',    'Opening shift check',               'OK',       DATEADD('DAY', -3, CURRENT_TIMESTAMP), 4,  10.1),
    (1, 5, 'IK_FOOD', 'Temperature reading - Hot holding',    'Lunch service check',               'OK',       DATEADD('DAY', -2, CURRENT_TIMESTAMP), 5,  68.0),
    (1, 5, 'IK_FOOD', 'Temperature reading - Hot holding',    'Below safe temp after rush',        'WARNING',  DATEADD('DAY', -1, CURRENT_TIMESTAMP), 5,  61.5),
    (1, 3, 'IK_FOOD', 'Temperature reading - Display fridge', 'Morning check',                     'OK',       CURRENT_TIMESTAMP,                     3,  5.1),
    (1, 4, 'IK_FOOD', 'Temperature reading - Cold room 1',    'Closing shift check',               'OK',       CURRENT_TIMESTAMP,                     1,  3.4);

-- Temperature logs (tenant 2)
INSERT INTO temperature_compliance_logs (tenant_id, recorded_by, module, title, description, log_status, recorded_at, temperature_zone_id, temperature_celsius)
VALUES
    (2, 7, 'IK_FOOD', 'Temperature reading - Fish storage', 'Opening check',   'OK',      DATEADD('DAY', -3, CURRENT_TIMESTAMP), 6,  1.5),
    (2, 8, 'IK_FOOD', 'Temperature reading - Fish storage', 'Afternoon check', 'WARNING', DATEADD('DAY', -2, CURRENT_TIMESTAMP), 6,  3.8),
    (2, 7, 'IK_FOOD', 'Temperature reading - Rice warmer',  'Lunch service',   'OK',      DATEADD('DAY', -1, CURRENT_TIMESTAMP), 8,  65.0),
    (2, 8, 'IK_FOOD', 'Temperature reading - Freezer 1',    'Closing check',   'OK',      CURRENT_TIMESTAMP,                     7, -20.1);