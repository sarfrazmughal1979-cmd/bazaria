-- ============================================
-- V1: Initial Schema - IAM Module
-- ============================================

-- Users
CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    phone_number VARCHAR(20),
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    avatar_url VARCHAR(500),
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    email_verified BOOLEAN DEFAULT FALSE,
    phone_verified BOOLEAN DEFAULT FALSE,
    last_login_at TIMESTAMP WITH TIME ZONE,
    oauth_provider VARCHAR(50),
    oauth_id VARCHAR(255),
    is_deleted BOOLEAN DEFAULT FALSE,
    version BIGINT DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);

CREATE INDEX IF NOT EXISTS idx_user_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_user_phone ON users(phone_number);
CREATE INDEX IF NOT EXISTS idx_user_status ON users(status);

-- Roles
CREATE TABLE IF NOT EXISTS roles (
    id UUID PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255),
    version BIGINT DEFAULT 0
);

-- Permissions
CREATE TABLE IF NOT EXISTS permissions (
    id UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(255),
    module VARCHAR(50),
    version BIGINT DEFAULT 0
);

-- Role-Permission mapping
CREATE TABLE IF NOT EXISTS role_permissions (
    role_id UUID NOT NULL REFERENCES roles(id),
    permission_id UUID NOT NULL REFERENCES permissions(id),
    PRIMARY KEY (role_id, permission_id)
);

-- User-Role mapping
CREATE TABLE IF NOT EXISTS user_roles (
    user_id UUID NOT NULL REFERENCES users(id),
    role_id UUID NOT NULL REFERENCES roles(id),
    PRIMARY KEY (user_id, role_id)
);

-- Vendors
CREATE TABLE IF NOT EXISTS vendors (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL UNIQUE REFERENCES users(id),
    shop_name VARCHAR(255) NOT NULL,
    slug VARCHAR(255) NOT NULL UNIQUE,
    shop_description TEXT,
    shop_logo_url VARCHAR(500),
    shop_banner_url VARCHAR(500),
    business_address_line1 VARCHAR(255),
    business_address_line2 VARCHAR(255),
    business_city VARCHAR(100),
    business_state VARCHAR(100),
    business_postal_code VARCHAR(20),
    business_country VARCHAR(100),
    business_latitude DOUBLE PRECISION,
    business_longitude DOUBLE PRECISION,
    business_registration_number VARCHAR(100),
    tax_id VARCHAR(100),
    bank_account_name VARCHAR(255),
    bank_account_number VARCHAR(100),
    bank_name VARCHAR(255),
    bank_routing_number VARCHAR(100),
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    commission_rate DECIMAL(5,2) DEFAULT 10.00,
    rating DECIMAL(3,2) DEFAULT 0.00,
    total_products INT DEFAULT 0,
    total_orders INT DEFAULT 0,
    approved_at TIMESTAMP WITH TIME ZONE,
    approved_by UUID,
    rejection_reason TEXT,
    is_deleted BOOLEAN DEFAULT FALSE,
    version BIGINT DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);

CREATE INDEX IF NOT EXISTS idx_vendor_slug ON vendors(slug);
CREATE INDEX IF NOT EXISTS idx_vendor_status ON vendors(status);
CREATE INDEX IF NOT EXISTS idx_vendor_user ON vendors(user_id);

-- OTP Tokens
CREATE TABLE IF NOT EXISTS otp_tokens (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    token VARCHAR(10) NOT NULL,
    purpose VARCHAR(30) NOT NULL,
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
    used BOOLEAN DEFAULT FALSE,
    version BIGINT DEFAULT 0
);

-- Domain Events Store
CREATE TABLE IF NOT EXISTS domain_events (
    event_id UUID PRIMARY KEY,
    event_type VARCHAR(255) NOT NULL,
    aggregate_type VARCHAR(255) NOT NULL,
    aggregate_id VARCHAR(255) NOT NULL,
    payload TEXT NOT NULL,
    occurred_on TIMESTAMP WITH TIME ZONE NOT NULL,
    processed BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_event_aggregate ON domain_events(aggregate_type, aggregate_id);
CREATE INDEX IF NOT EXISTS idx_event_type ON domain_events(event_type);
CREATE INDEX IF NOT EXISTS idx_event_occurred ON domain_events(occurred_on);

-- ============================================
-- Seed Data
-- ============================================

INSERT INTO roles (id, name, description) VALUES
    (gen_random_uuid(), 'ADMIN', 'Platform Administrator'),
    (gen_random_uuid(), 'VENDOR', 'Vendor/Seller'),
    (gen_random_uuid(), 'CUSTOMER', 'Customer/Buyer'),
    (gen_random_uuid(), 'SUPPORT', 'Customer Support Agent')
ON CONFLICT (name) DO NOTHING;

INSERT INTO permissions (id, name, description, module) VALUES
    (gen_random_uuid(), 'PRODUCT_CREATE', 'Create products', 'CATALOG'),
    (gen_random_uuid(), 'PRODUCT_UPDATE', 'Update products', 'CATALOG'),
    (gen_random_uuid(), 'PRODUCT_DELETE', 'Delete products', 'CATALOG'),
    (gen_random_uuid(), 'PRODUCT_APPROVE', 'Approve products', 'CATALOG'),
    (gen_random_uuid(), 'ORDER_VIEW', 'View orders', 'ORDER'),
    (gen_random_uuid(), 'ORDER_MANAGE', 'Manage orders', 'ORDER'),
    (gen_random_uuid(), 'VENDOR_MANAGE', 'Manage vendors', 'IAM'),
    (gen_random_uuid(), 'SETTLEMENT_VIEW', 'View settlements', 'SETTLEMENT'),
    (gen_random_uuid(), 'SETTLEMENT_MANAGE', 'Manage settlements', 'SETTLEMENT'),
    (gen_random_uuid(), 'ANALYTICS_VIEW', 'View analytics', 'ANALYTICS'),
    (gen_random_uuid(), 'CMS_MANAGE', 'Manage CMS content', 'CMS'),
    (gen_random_uuid(), 'SUPPORT_MANAGE', 'Manage support tickets', 'SUPPORT')
ON CONFLICT (name) DO NOTHING;

-- Assign all permissions to ADMIN role
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p WHERE r.name = 'ADMIN'
ON CONFLICT DO NOTHING;

-- Assign vendor permissions
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.name = 'VENDOR' AND p.name IN ('PRODUCT_CREATE', 'PRODUCT_UPDATE', 'PRODUCT_DELETE', 'ORDER_VIEW', 'ORDER_MANAGE', 'SETTLEMENT_VIEW', 'ANALYTICS_VIEW')
ON CONFLICT DO NOTHING;

-- Assign customer permissions
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.name = 'CUSTOMER' AND p.name IN ('ORDER_VIEW')
ON CONFLICT DO NOTHING;