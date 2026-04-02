-- Insert test tenant
INSERT INTO tenants (name, org_number, address, city, country, contact_email, contact_phone, active)
VALUES ('Kule kulinger AS', '123456789', 'Høgskoleringen 67', 'Trondheim', 'NO', 'contact@test.no', '+4798765432', TRUE);

-- Insert test users
-- Note: password is a bcrypt hash placeholder (replace with a real bcrypt hash if you want to login with these)
INSERT INTO users (tenant_id, role, first_name, last_name, contact_phone, active, username, email, password)
VALUES
    (1, 'ADMIN', 'Sigurd', 'Sigurdsson', '+4798765432', TRUE, 'sigurd', 'sigurd@test.no', '$2a$12$0hWR2qbEgjJSg7X5u/TJUOGWz4XHw7rJHqznC1Z.Yx.2sDIw8E1.O'), -- Password123
    (1, 'MANAGER', 'John', 'Doe', '+4798765432', TRUE, 'john', 'john@test.no', '$2a$12$iskMl0/cnCHZeXpVr37hWOP.eVgNKjhm9YfDJs/U240nWPQBPkIim'), -- Sigurd123
    (1, 'STAFF', 'Jane', 'Smith', '+4798765433', TRUE, 'jane', 'jane@test.no', '$2a$12$xV6Xzzw4iTqUAcz//DAK.unAwAR455e5c2OlaUqoq8.drVFLWewWK'); --trygtpassord!

INSERT INTO temperature_zones (tenant_id, name, lower_limit_celsius, upper_limit_celsius, active)
VALUES
    (1, 'Cold room 1', 0.0, 4.0, TRUE),
    (1, 'Freezer room', -22.0, -18.0, TRUE),
    (1, 'Display fridge', 0.0, 6.0, TRUE);

INSERT INTO temperature_compliance_logs (tenant_id, recorded_by, module, title, description, log_status, recorded_at, temperature_zone_id, temperature_celsius)
VALUES
    (1, 2, 'IK_FOOD', 'Temperature reading - Cold room 1', 'Opening shift check', 'OK', CURRENT_TIMESTAMP, 1, 3.1),
    (1, 3, 'IK_FOOD', 'Temperature reading - Display fridge', 'Slightly above target', 'WARNING', CURRENT_TIMESTAMP, 3, 7.2);

INSERT INTO checklist_templates (tenant_id, module, name, frequency)
VALUES
(1, 'IK_FOOD', 'Daily Kitchen Checklist', 'DAILY'),
(1, 'IK_FOOD', 'Weekly Cleaning Checklist', 'WEEKLY');

-- Daily Kitchen Checklist (id = 1)
INSERT INTO checklist_item_templates (checklist_id, description, sort_order)
VALUES
(1, 'Clean all food contact surfaces', 0),
(1, 'Check refrigerator temperatures', 1),
(1, 'Dispose of waste properly', 2);

-- Weekly Cleaning Checklist (id = 2)
INSERT INTO checklist_item_templates (checklist_id, description, sort_order)
VALUES
(2, 'Deep clean kitchen floors', 0),
(2, 'Sanitize storage areas', 1);

INSERT INTO checklist_instances (tenant_id, template_id, date, status)
VALUES
(1, 1, CURRENT_DATE, 'PENDING');

INSERT INTO checklist_item_instances (checklist_id, template_item_id, completed, comment, completed_at)
VALUES
(1, 1, true, 'Done early', CURRENT_DATE),
(1, 2, false, null, null),
(1, 3, false, null, null);