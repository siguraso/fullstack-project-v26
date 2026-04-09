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
    (2, 8, 'IK_FOOD', 'Temperature reading - Freezer 1',    'Closing check',   'OK',      CURRENT_TIMESTAMP,                     7, -20.1),
    (1, 2, 'IK_FOOD', 'Temperature reading - Cold room 1', 'Opening shift check', 'OK', CURRENT_TIMESTAMP, 1, 3.1),
    (1, 3, 'IK_FOOD', 'Temperature reading - Display fridge', 'Slightly above target', 'WARNING', CURRENT_TIMESTAMP, 3, 7.2);

-- ============================================================
-- DEVIATION TEST DATA
-- ============================================================

-- Deviations (tenant 1)
INSERT INTO deviations (
    tenant_id, module, title, reported_date,
    discovered_by, reported_to, assigned_to,
    issue_description, immediate_action, root_cause, corrective_action, completion_notes,
    severity, category, status, created_by, created_at, resolved_at
)
VALUES
(1, 'IK_FOOD', 'Freezer temperature exceeded limit', DATEADD('DAY', -14, CURRENT_DATE),
 'Jane Smith', 'John Doe', 'Ole Hansen',
 'Freezer room reached -10°C after door was left ajar.',
 'Discarded affected food immediately.',
 'Door not properly closed during shift change.',
 'Staff retrained on closing procedures.',
 'Temperature stable and procedures followed.',
 'CRITICAL', 'TEMPERATURE', 'RESOLVED', 3, DATEADD('DAY', -14, CURRENT_TIMESTAMP), DATEADD('DAY', -12, CURRENT_TIMESTAMP)),

(1, 'IK_FOOD', 'Expired dairy products found', DATEADD('DAY', -10, CURRENT_DATE),
 'Ole Hansen', 'John Doe', 'Kari Nordmann',
 'Expired dairy products found in fridge.',
 'Items removed and discarded.',
 'Routine expiry check missed.',
 'Checklist reinforced and reminders added.',
 'All items verified fresh.',
 'HIGH', 'HYGIENE', 'RESOLVED', 4, DATEADD('DAY', -10, CURRENT_TIMESTAMP), DATEADD('DAY', -9, CURRENT_TIMESTAMP)),

(1, 'IK_FOOD', 'Display fridge above 6°C', DATEADD('DAY', -6, CURRENT_DATE),
 'Jane Smith', 'John Doe', 'Ole Hansen',
 'Display fridge recorded at 7.2°C.',
 'Moved sensitive goods to backup fridge.',
 'Cooling unit degraded.',
 'Technician scheduled for repair.',
 NULL,
 'HIGH', 'TEMPERATURE', 'IN_PROGRESS', 3, DATEADD('DAY', -6, CURRENT_TIMESTAMP), NULL),

(1, 'IK_ALCOHOL', 'Age verification not performed', DATEADD('DAY', -2, CURRENT_DATE),
 'Sigurd Sigurdsson', 'John Doe', 'Jane Smith',
 'Customer served without ID check.',
 'Service stopped immediately.',
 'Staff skipped ID check under pressure.',
 'Refresher training scheduled.',
 NULL,
 'CRITICAL', 'ALCOHOL', 'IN_PROGRESS', 2, DATEADD('DAY', -2, CURRENT_TIMESTAMP), NULL),

(1, 'IK_ALCOHOL', 'Alcohol served outside licensed hours', DATEADD('DAY', -1, CURRENT_DATE),
 'Jane Smith', 'John Doe', 'Ole Hansen',
 'Alcohol served after closing time.',
 'Service stopped and logged.',
 'Miscommunication on closing time.',
 'Closing checklist updated.',
 NULL,
 'HIGH', 'ALCOHOL', 'OPEN', 3, DATEADD('DAY', -1, CURRENT_TIMESTAMP), NULL),

(1, 'IK_FOOD', 'Pest activity observed', CURRENT_DATE,
 'John Doe', 'Sigurd Sigurdsson', 'Kari Nordmann',
 'Mouse droppings found in storage.',
 'Area sealed and cleaned.',
 'Gaps in wall allowed entry.',
 'Pest control engaged.',
 NULL,
 'HIGH', 'SAFETY', 'OPEN', 2, CURRENT_TIMESTAMP, NULL);

-- Deviations (tenant 2)
INSERT INTO deviations (
    tenant_id, module, title, reported_date,
    discovered_by, reported_to, assigned_to,
    issue_description, immediate_action, root_cause, corrective_action, completion_notes,
    severity, category, status, created_by, created_at, resolved_at
)
VALUES
(2, 'IK_FOOD', 'Fish storage above limit', DATEADD('DAY', -2, CURRENT_DATE),
 'Priya Sharma', 'Raj Patel', 'Anita Kumar',
 'Fish storage recorded above safe temperature.',
 'Stock inspected and unsafe items discarded.',
 'Door left open during delivery.',
 'Procedure updated for deliveries.',
 'Temperature stabilized.',
 'CRITICAL', 'TEMPERATURE', 'RESOLVED', 7, DATEADD('DAY', -2, CURRENT_TIMESTAMP), DATEADD('DAY', -1, CURRENT_TIMESTAMP)),

(2, 'IK_FOOD', 'Allergen cross-contact risk', CURRENT_DATE,
 'Raj Patel', 'Priya Sharma', 'Anita Kumar',
 'Nut sauce stored near allergen-free food.',
 'Items separated immediately.',
 'Improper storage labeling.',
 'Storage zones reorganized.',
 NULL,
 'HIGH', 'HYGIENE', 'OPEN', 7, CURRENT_TIMESTAMP, NULL);

-- Deviations (tenant 3)
INSERT INTO deviations (
    tenant_id, module, title, reported_date,
    discovered_by, reported_to, assigned_to,
    issue_description, immediate_action, root_cause, corrective_action, completion_notes,
    severity, category, status, created_by, created_at, resolved_at
)
VALUES
(3, 'IK_ALCOHOL', 'Underage customer served', DATEADD('DAY', -5, CURRENT_DATE),
 'Lars Berg', 'Nina Dahl', 'Morten Lie',
 'Underage customer was served alcohol.',
 'Drink removed immediately.',
 'ID check not performed.',
 'Strict ID enforcement implemented.',
 NULL,
 'CRITICAL', 'ALCOHOL', 'IN_PROGRESS', 10, DATEADD('DAY', -5, CURRENT_TIMESTAMP), NULL),

(3, 'IK_ALCOHOL', 'Intoxicated customer served', DATEADD('DAY', -1, CURRENT_DATE),
 'Nina Dahl', 'Lars Berg', 'Morten Lie',
 'Customer visibly intoxicated was served.',
 'Service stopped immediately.',
 'Staff failed to assess intoxication.',
 'Training scheduled for staff.',
 NULL,
 'CRITICAL', 'ALCOHOL', 'OPEN', 10, DATEADD('DAY', -1, CURRENT_TIMESTAMP), NULL);

-- ============================================================
-- ALCOHOL LOG TEST DATA
-- ============================================================

-- Alcohol compliance logs (tenant 1)
INSERT INTO alcohol_compliance_logs (tenant_id, recorded_by, module, title, description, log_status, recorded_at, log_type, id_checked, service_refused, estimated_age)
VALUES
    (1, 3, 'IK_ALCOHOL', 'Age verification - table 4',          'Customer appeared under 25. Valid ID confirmed (22 yrs).',           'OK',       DATEADD('DAY', -7, CURRENT_TIMESTAMP), 'AGE_VERIFICATION', TRUE,  FALSE, 22),
    (1, 4, 'IK_ALCOHOL', 'Service refusal - bar area',          'Customer refused service due to visible intoxication.',              'WARNING',  DATEADD('DAY', -6, CURRENT_TIMESTAMP), 'SERVICE_REFUSAL',  FALSE, TRUE,  NULL),
    (1, 3, 'IK_ALCOHOL', 'Age verification - walk-in',          'Customer appeared underage. No valid ID produced. Service refused.', 'CRITICAL', DATEADD('DAY', -5, CURRENT_TIMESTAMP), 'AGE_VERIFICATION', TRUE,  TRUE,  17),
    (1, 5, 'IK_ALCOHOL', 'Age verification - table 9',          'ID checked, customer confirmed 19 years old.',                      'OK',       DATEADD('DAY', -4, CURRENT_TIMESTAMP), 'AGE_VERIFICATION', TRUE,  FALSE, 19),
    (1, 3, 'IK_ALCOHOL', 'Closing stock count',                 'End-of-day spirits, wine and beer inventory recorded.',             'OK',       DATEADD('DAY', -4, CURRENT_TIMESTAMP), 'CLOSING_STOCK',    FALSE, FALSE, NULL),
    (1, 2, 'IK_ALCOHOL', 'Responsible service training session','All bar staff attended refresher course on Alkoholloven.',          'OK',       DATEADD('DAY', -3, CURRENT_TIMESTAMP), 'TRAINING',         FALSE, FALSE, NULL),
    (1, 4, 'IK_ALCOHOL', 'Age verification - group booking',    'Three members of group checked, all confirmed 18+.',                'OK',       DATEADD('DAY', -2, CURRENT_TIMESTAMP), 'AGE_VERIFICATION', TRUE,  FALSE, 18),
    (1, 5, 'IK_ALCOHOL', 'Service refusal - aggressive guest',  'Guest refused service after displaying aggressive behaviour.',      'WARNING',  DATEADD('DAY', -1, CURRENT_TIMESTAMP), 'SERVICE_REFUSAL',  FALSE, TRUE,  NULL),
    (1, 3, 'IK_ALCOHOL', 'Age verification - table 2',          'ID not requested as customer clearly above 25.',                   'OK',       CURRENT_TIMESTAMP,                       'AGE_VERIFICATION', FALSE, FALSE, NULL),
    (1, 4, 'IK_ALCOHOL', 'Closing stock count',                 'End-of-day inventory completed without discrepancies.',            'OK',       CURRENT_TIMESTAMP,                       'CLOSING_STOCK',    FALSE, FALSE, NULL);

-- Alcohol compliance logs (tenant 3)
INSERT INTO alcohol_compliance_logs (tenant_id, recorded_by, module, title, description, log_status, recorded_at, log_type, id_checked, service_refused, estimated_age)
VALUES
    (3, 11, 'IK_ALCOHOL', 'Age verification - bar stool 3',     'Customer checked and confirmed 21 years old.',                     'OK',       DATEADD('DAY', -5, CURRENT_TIMESTAMP), 'AGE_VERIFICATION', TRUE,  FALSE, 21),
    (3, 10, 'IK_ALCOHOL', 'Underage service incident',          'Customer served before age was verified. Service halted. Formal report filed.',  'CRITICAL', DATEADD('DAY', -5, CURRENT_TIMESTAMP), 'INCIDENT', TRUE, TRUE, 17),
    (3, 11, 'IK_ALCOHOL', 'Age verification - group of 4',      'Full group ID-checked. All confirmed 18+.',                        'OK',       DATEADD('DAY', -3, CURRENT_TIMESTAMP), 'AGE_VERIFICATION', TRUE,  FALSE, 20),
    (3, 10, 'IK_ALCOHOL', 'Service refusal - intoxicated',      'Guest visibly intoxicated on arrival. Entry and service refused.', 'WARNING',  DATEADD('DAY', -2, CURRENT_TIMESTAMP), 'SERVICE_REFUSAL',  FALSE, TRUE,  NULL),
    (3, 11, 'IK_ALCOHOL', 'Closing stock count',                'Weekly stock count completed. Minor variance in spirits noted.',   'WARNING',  DATEADD('DAY', -1, CURRENT_TIMESTAMP), 'CLOSING_STOCK',    FALSE, FALSE, NULL),
    (3, 10, 'IK_ALCOHOL', 'Age verification - late night entry','Customer appeared young. ID confirmed 23 years old.',             'OK',       CURRENT_TIMESTAMP,                       'AGE_VERIFICATION', TRUE,  FALSE, 23);

-- ============================================================
-- CHECKLIST TEST DATA
-- Two templates with instances for today so the UI is populated
-- on first run without needing to create anything manually.
-- ============================================================

-- Library items (tenant 1)
INSERT INTO checklist_item_library (tenant_id, title, description, category, priority) VALUES
    (1, 'Refrigerator Temperature Log',   'Record temperature of all refrigerators. Must be ≤4°C. Note any deviations immediately.', 'IK_FOOD', 'HIGH'),
    (1, 'Freezer Temperature Log',        'Record temperature of all freezers. Must be ≤-18°C. Log deviations and discard food if threshold exceeded.', 'IK_FOOD', 'HIGH'),
    (1, 'Expiry Date Inspection',         'Check all refrigerated and dry goods for expired dates. Remove, log, and dispose of expired items.', 'IK_FOOD', 'HIGH'),
    (1, 'Hand Washing Compliance Check',  'Verify hand washing stations are stocked with soap and paper towels. Confirm staff compliance.', 'IK_FOOD', 'LOW'),
    (1, 'ID Check Policy Reminder',       'Brief all serving staff: Norwegian law requires ID check for anyone appearing under 25.', 'IK_ALCOHOL', 'HIGH'),
    (1, 'Refused Service Log',            'Document all refused service incidents including time, reason, and brief description.', 'IK_ALCOHOL', 'HIGH'),
    (1, 'Serving Hours Compliance',       'Verify alcohol is not served outside licensed hours. Log actual service start and end times.', 'IK_ALCOHOL', 'HIGH');

-- Templates (tenant 1)
INSERT INTO checklist_templates (tenant_id, module, name, frequency, active) VALUES
    (1, 'IK_FOOD',    'Morning Food Safety Checklist',    'DAILY', TRUE),
    (1, 'IK_ALCOHOL', 'Alcohol Service Daily Checklist',  'DAILY', TRUE);

-- Template items — snapshots of library item content at time of template creation
INSERT INTO checklist_item_templates (checklist_id, library_item_id, title, description, sort_order) VALUES
    (1, 1, 'Refrigerator Temperature Log',  'Record temperature of all refrigerators. Must be ≤4°C. Note any deviations immediately.', 1),
    (1, 2, 'Freezer Temperature Log',       'Record temperature of all freezers. Must be ≤-18°C. Log deviations and discard food if threshold exceeded.', 2),
    (1, 3, 'Expiry Date Inspection',        'Check all refrigerated and dry goods for expired dates. Remove, log, and dispose of expired items.', 3),
    (1, 4, 'Hand Washing Compliance Check', 'Verify hand washing stations are stocked with soap and paper towels. Confirm staff compliance.', 4),
    (2, 5, 'ID Check Policy Reminder',      'Brief all serving staff: Norwegian law requires ID check for anyone appearing under 25.', 1),
    (2, 6, 'Refused Service Log',           'Document all refused service incidents including time, reason, and brief description.', 2),
    (2, 7, 'Serving Hours Compliance',      'Verify alcohol is not served outside licensed hours. Log actual service start and end times.', 3);

-- Today's checklist instances (one per template)
INSERT INTO checklist_instances (tenant_id, template_id, date, status) VALUES
    (1, 1, CURRENT_DATE, 'PENDING'),
    (1, 2, CURRENT_DATE, 'PENDING');

-- Instance items (one per template item, all starting incomplete)
INSERT INTO checklist_item_instances (checklist_id, template_item_id, completed, comment, completed_at) VALUES
    (1, 1, FALSE, NULL, NULL),
    (1, 2, FALSE, NULL, NULL),
    (1, 3, FALSE, NULL, NULL),
    (1, 4, FALSE, NULL, NULL),
    (2, 5, FALSE, NULL, NULL),
    (2, 6, FALSE, NULL, NULL),
    (2, 7, FALSE, NULL, NULL);

-- ============================================================
-- CHECKLIST PRESET SEED DATA (global, not tenant-specific)
--
-- HOW TO ADD MORE PRESETS:
--   Copy any INSERT block below and add a new row with:
--     tab        -> IK_FOOD | IK_ALCOHOL | HACCP  (picker display tab)
--     category   -> IK_FOOD | IK_ALCOHOL           (library item category when added)
--     group_label -> free text sub-category heading
--     sort_order -> integer, lower = displayed first within the group
-- ============================================================

INSERT INTO checklist_presets (title, description, category, priority, tab, group_label, sort_order) VALUES

    -- IK_FOOD / Temperature Control
    ('Refrigerator Temperature Log',
     'Record temperature of all refrigerators. Must be ≤4°C. Note any deviations immediately and take corrective action.',
     'IK_FOOD', 'HIGH', 'IK_FOOD', 'Temperature Control', 1),

    ('Freezer Temperature Log',
     'Record temperature of all freezers. Must be ≤-18°C. Log deviations and discard food if threshold is exceeded.',
     'IK_FOOD', 'HIGH', 'IK_FOOD', 'Temperature Control', 2),

    ('Hot Food Holding Temperature',
     'Verify all hot food held for service is above 60°C. Log and discard if below threshold.',
     'IK_FOOD', 'HIGH', 'IK_FOOD', 'Temperature Control', 3),

    ('Cold Display Temperature Check',
     'Check cold buffet/display temperature (must be ≤4°C). Remove non-compliant items immediately.',
     'IK_FOOD', 'HIGH', 'IK_FOOD', 'Temperature Control', 4),

    -- IK_FOOD / Cleaning & Hygiene
    ('Kitchen Surface Cleaning',
     'Clean and disinfect all work surfaces, cutting boards, and utensils with approved cleaning agents per cleaning schedule.',
     'IK_FOOD', 'LOW', 'IK_FOOD', 'Cleaning & Hygiene', 5),

    ('Floor and Drain Cleaning',
     'Clean kitchen floor and drains with approved disinfectant. Remove debris and grease buildup.',
     'IK_FOOD', 'LOW', 'IK_FOOD', 'Cleaning & Hygiene', 6),

    ('Hand Washing Compliance Check',
     'Verify hand washing stations are stocked with soap and paper towels. Confirm staff follow handwashing routines.',
     'IK_FOOD', 'LOW', 'IK_FOOD', 'Cleaning & Hygiene', 7),

    ('Dishwasher Temperature Verification',
     'Confirm dishwasher reaches minimum 60°C wash cycle and 82°C rinse cycle for proper sanitization.',
     'IK_FOOD', 'LOW', 'IK_FOOD', 'Cleaning & Hygiene', 8),

    -- IK_FOOD / Food Safety
    ('Expiry Date Inspection',
     'Check all refrigerated and dry goods for expired dates. Remove, log, and dispose of expired items.',
     'IK_FOOD', 'HIGH', 'IK_FOOD', 'Food Safety', 9),

    ('Allergen Information Verification',
     'Confirm allergen labeling is accurate and visible for all menu items per EU regulation 1169/2011.',
     'IK_FOOD', 'HIGH', 'IK_FOOD', 'Food Safety', 10),

    ('Goods Delivery Inspection',
     'Inspect incoming deliveries for temperature, packaging integrity, and expiry dates. Reject non-compliant deliveries.',
     'IK_FOOD', 'LOW', 'IK_FOOD', 'Food Safety', 11),

    ('Pest Control Inspection',
     'Inspect kitchen and storage areas for signs of pests. Check all traps and document any findings.',
     'IK_FOOD', 'LOW', 'IK_FOOD', 'Food Safety', 12),

    ('Staff Personal Hygiene Check',
     'Confirm all food-handling staff follow requirements: clean uniforms, no jewelry, hair covered, no illness.',
     'IK_FOOD', 'LOW', 'IK_FOOD', 'Food Safety', 13),

    -- IK_ALCOHOL / Age Verification
    ('ID Check Policy Reminder',
     'Brief all serving staff: Norwegian law requires ID check for anyone appearing under 25. Beer/wine min. 18, spirits min. 20.',
     'IK_ALCOHOL', 'HIGH', 'IK_ALCOHOL', 'Age Verification', 1),

    ('ID Verification Log',
     'Record number of ID checks performed during shift and document any refused service incidents with time and reason.',
     'IK_ALCOHOL', 'HIGH', 'IK_ALCOHOL', 'Age Verification', 2),

    ('Refused Service Log',
     'Document all refused service incidents including time, reason (underage/intoxicated), and brief description.',
     'IK_ALCOHOL', 'HIGH', 'IK_ALCOHOL', 'Age Verification', 3),

    -- IK_ALCOHOL / Responsible Serving
    ('Intoxication Level Assessment',
     'Assess customers showing signs of intoxication. Refuse further service per Alkoholloven §8-11 and offer water/food.',
     'IK_ALCOHOL', 'HIGH', 'IK_ALCOHOL', 'Responsible Serving', 4),

    ('Serving Hours Compliance',
     'Verify alcohol is not served outside of licensed hours (skjenketid). Log actual service start and end times.',
     'IK_ALCOHOL', 'HIGH', 'IK_ALCOHOL', 'Responsible Serving', 5),

    ('Responsible Service Training Check',
     'Confirm all active serving staff have current responsible alcohol service certification (Skjenkekurs).',
     'IK_ALCOHOL', 'LOW', 'IK_ALCOHOL', 'Responsible Serving', 6),

    ('Minimum Age Compliance Spot Check',
     'Supervisor spot check: verify serving staff are actively checking age for customers appearing under 25.',
     'IK_ALCOHOL', 'LOW', 'IK_ALCOHOL', 'Responsible Serving', 7),

    -- IK_ALCOHOL / Documentation
    ('Alcohol License Display Check',
     'Verify the liquor license (skjenkebevilling) is displayed visibly and is current and valid.',
     'IK_ALCOHOL', 'LOW', 'IK_ALCOHOL', 'Documentation', 8),

    ('Daily Alcohol Sales Record',
     'Record total alcohol sales volumes by category (beer, wine, spirits) for daily compliance reporting.',
     'IK_ALCOHOL', 'LOW', 'IK_ALCOHOL', 'Documentation', 9),

    ('Alcohol Stock Inventory',
     'Count and record current spirits, wine, and beer inventory. Report any significant discrepancies.',
     'IK_ALCOHOL', 'LOW', 'IK_ALCOHOL', 'Documentation', 10),

    -- HACCP / Critical Control Points (category=IK_FOOD, HACCP is a subset of food safety)
    ('CCP 1 - Cooking Temperature',
     'Log internal cooking temperature for all meat and poultry per batch. Must reach >=70°C core temperature.',
     'IK_FOOD', 'HIGH', 'HACCP', 'Critical Control Points', 1),

    ('CCP 2 - Cooling Protocol',
     'Monitor rapid cooling of cooked food: from 60°C to below 8°C within 2 hours. Log time and temperature.',
     'IK_FOOD', 'HIGH', 'HACCP', 'Critical Control Points', 2),

    ('CCP 3 - Cold Storage',
     'Verify cold storage maintains <=4°C continuously. Confirm temperature alarms are functional.',
     'IK_FOOD', 'HIGH', 'HACCP', 'Critical Control Points', 3),

    ('CCP Deviation Report',
     'Document any critical limit exceedance: what happened, corrective action taken, and person responsible.',
     'IK_FOOD', 'HIGH', 'HACCP', 'Critical Control Points', 4),

    -- HACCP / Process Controls
    ('Cross-Contamination Prevention',
     'Verify colour-coded cutting boards and utensils are used correctly. Confirm raw and cooked food are separated.',
     'IK_FOOD', 'HIGH', 'HACCP', 'Process Controls', 5),

    ('Thermometer Calibration Verification',
     'Check that all food thermometers are calibrated and within acceptable accuracy range (+/-1°C). Log result.',
     'IK_FOOD', 'LOW', 'HACCP', 'Process Controls', 6),

    ('Hazard Identification Review',
     'Review new menu items or process changes for potential biological, chemical, or physical hazards.',
     'IK_FOOD', 'LOW', 'HACCP', 'Process Controls', 7),

    -- HACCP / Record Keeping
    ('HACCP Records Review',
     'Review, verify, and sign off all HACCP monitoring records for completeness and accuracy. File appropriately.',
     'IK_FOOD', 'LOW', 'HACCP', 'Record Keeping', 8),

    ('Supplier Certification Check',
     'Verify food supplier certificates and food safety documentation are current and on file.',
     'IK_FOOD', 'LOW', 'HACCP', 'Record Keeping', 9),

    ('Staff HACCP Training Log',
     'Confirm all food-handling staff have completed required HACCP awareness training. Update the training log.',
     'IK_FOOD', 'LOW', 'HACCP', 'Record Keeping', 10);
