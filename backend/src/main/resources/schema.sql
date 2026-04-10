CREATE TABLE IF NOT EXISTS tenants (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    org_number VARCHAR(20) NOT NULL UNIQUE,
    address VARCHAR(500),
    city VARCHAR(100),
    country VARCHAR(100) NOT NULL,
    contact_email VARCHAR(255),
    contact_phone VARCHAR(30),
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);


CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'STAFF',
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    contact_phone VARCHAR(30),
    active BOOLEAN DEFAULT TRUE,
    username VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (tenant_id) REFERENCES tenants(id)
);

CREATE TABLE IF NOT EXISTS invitations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    email VARCHAR(255) NOT NULL,
    token VARCHAR(1024) NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    accepted BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (tenant_id) REFERENCES tenants(id)
);

CREATE TABLE IF NOT EXISTS temperature_zones (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    name VARCHAR(120) NOT NULL,
    lower_limit_celsius DOUBLE NOT NULL,
    upper_limit_celsius DOUBLE NOT NULL,
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE (tenant_id, name, active),
    FOREIGN KEY (tenant_id) REFERENCES tenants(id)
);

CREATE TABLE IF NOT EXISTS temperature_compliance_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    recorded_by BIGINT NOT NULL,
    module VARCHAR(20) NOT NULL DEFAULT 'IK_FOOD' CHECK (module = 'IK_FOOD'),
    title VARCHAR(255) NOT NULL,
    description VARCHAR(2000),
    log_status VARCHAR(20),
    recorded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    temperature_zone_id BIGINT NOT NULL,
    temperature_celsius DOUBLE NOT NULL,
    FOREIGN KEY (tenant_id) REFERENCES tenants(id),
    FOREIGN KEY (recorded_by) REFERENCES users(id),
    FOREIGN KEY (temperature_zone_id) REFERENCES temperature_zones(id)
);

CREATE TABLE IF NOT EXISTS alcohol_compliance_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    recorded_by BIGINT NOT NULL,
    module VARCHAR(20) NOT NULL DEFAULT 'IK_ALCOHOL' CHECK (module = 'IK_ALCOHOL'),
    title VARCHAR(255) NOT NULL,
    description VARCHAR(2000),
    log_status VARCHAR(20),
    recorded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    log_type VARCHAR(50) NOT NULL,
    id_checked BOOLEAN,
    service_refused BOOLEAN,
    estimated_age INT,
    FOREIGN KEY (tenant_id) REFERENCES tenants(id),
    FOREIGN KEY (recorded_by) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS deviations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    module VARCHAR(20) NOT NULL,
    title VARCHAR(255) NOT NULL,
    reported_date DATE,
    discovered_by VARCHAR(255),
    reported_to VARCHAR(255),
    assigned_to VARCHAR(255),
    issue_description VARCHAR(2000),
    immediate_action VARCHAR(2000),
    root_cause VARCHAR(2000),
    corrective_action VARCHAR(2000),
    completion_notes VARCHAR(2000),
    severity VARCHAR(20),
    category VARCHAR(20),
    status VARCHAR(20),
    created_by BIGINT,
    log_id BIGINT,
    created_at TIMESTAMP,
    resolved_at TIMESTAMP,
    FOREIGN KEY (tenant_id) REFERENCES tenants(id),
    FOREIGN KEY (created_by) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS documents (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    area VARCHAR(20) NOT NULL CHECK (area IN ('GENERAL', 'IK_FOOD', 'IK_ALCOHOL')),
    title VARCHAR(255) NOT NULL,
    description VARCHAR(2000),
    original_filename VARCHAR(255) NOT NULL,
    mime_type VARCHAR(150) NOT NULL,
    size_bytes BIGINT NOT NULL,
    uploaded_by BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (tenant_id) REFERENCES tenants(id),
    FOREIGN KEY (uploaded_by) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS document_tags (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    document_id BIGINT NOT NULL,
    tag VARCHAR(80) NOT NULL,
    UNIQUE (document_id, tag),
    FOREIGN KEY (document_id) REFERENCES documents(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS document_blob_contents (
    document_id BIGINT PRIMARY KEY,
    content BLOB NOT NULL,
    FOREIGN KEY (document_id) REFERENCES documents(id) ON DELETE CASCADE
);
