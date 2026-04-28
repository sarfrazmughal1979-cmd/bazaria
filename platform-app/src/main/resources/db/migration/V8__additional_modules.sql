-- Shipping
CREATE TABLE IF NOT EXISTS delivery_zones (
    id UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    country VARCHAR(100) NOT NULL,
    region VARCHAR(100),
    city VARCHAR(100),
    postal_code_pattern VARCHAR(50),
    base_rate DECIMAL(19,4) NOT NULL,
    rate_per_kg DECIMAL(19,4),
    is_active BOOLEAN DEFAULT TRUE,
    is_deleted BOOLEAN DEFAULT FALSE,
    version BIGINT DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ
);

CREATE TABLE IF NOT EXISTS shipping_rates (
    id UUID PRIMARY KEY,
    from_postal VARCHAR(20),
    to_postal VARCHAR(20),
    weight_kg DECIMAL(10,2),
    method VARCHAR(30),
    carrier VARCHAR(50),
    cost DECIMAL(19,4) NOT NULL,
    estimated_days INT,
    expires_at TIMESTAMPTZ,
    version BIGINT DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
CREATE INDEX IF NOT EXISTS idx_shipping_rates_lookup ON shipping_rates(from_postal, to_postal, weight_kg);

CREATE TABLE IF NOT EXISTS shipments (
    id UUID PRIMARY KEY,
    sub_order_id UUID NOT NULL,
    vendor_id UUID NOT NULL,
    tracking_number VARCHAR(100),
    carrier VARCHAR(50),
    status VARCHAR(30) NOT NULL,
    method VARCHAR(30),
    pickup_address_line1 VARCHAR(255),
    pickup_address_line2 VARCHAR(255),
    pickup_city VARCHAR(100),
    pickup_state VARCHAR(100),
    pickup_postal_code VARCHAR(20),
    pickup_country VARCHAR(100),
    delivery_address_line1 VARCHAR(255),
    delivery_address_line2 VARCHAR(255),
    delivery_city VARCHAR(100),
    delivery_state VARCHAR(100),
    delivery_postal_code VARCHAR(20),
    delivery_country VARCHAR(100),
    total_weight_kg DECIMAL(10,2),
    shipping_cost DECIMAL(19,4),
    currency VARCHAR(3),
    label_url VARCHAR(500),
    estimated_delivery_date TIMESTAMPTZ,
    actual_delivery_date TIMESTAMPTZ,
    carrier_response TEXT,
    last_tracking_update TIMESTAMPTZ,
    is_deleted BOOLEAN DEFAULT FALSE,
    version BIGINT DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);
CREATE INDEX IF NOT EXISTS idx_shipments_sub_order ON shipments(sub_order_id);
CREATE INDEX IF NOT EXISTS idx_shipments_tracking ON shipments(tracking_number);
CREATE INDEX IF NOT EXISTS idx_shipments_status ON shipments(status);
CREATE INDEX IF NOT EXISTS idx_shipments_vendor ON shipments(vendor_id);

-- Promotions
CREATE TABLE IF NOT EXISTS coupons (
    id UUID PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(500),
    discount_type VARCHAR(20) NOT NULL,
    discount_value DECIMAL(19,4) NOT NULL,
    min_order_amount DECIMAL(19,4),
    max_discount_amount DECIMAL(19,4),
    usage_limit INT,
    used_count INT DEFAULT 0,
    per_user_limit INT DEFAULT 1,
    vendor_id UUID,
    start_date TIMESTAMPTZ,
    end_date TIMESTAMPTZ,
    is_active BOOLEAN DEFAULT TRUE,
    is_deleted BOOLEAN DEFAULT FALSE,
    version BIGINT DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);
CREATE INDEX IF NOT EXISTS idx_coupon_code ON coupons(code);

CREATE TABLE IF NOT EXISTS coupon_usages (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    coupon_id UUID NOT NULL REFERENCES coupons(id),
    customer_id UUID NOT NULL,
    order_id UUID NOT NULL,
    discount_amount DECIMAL(19,4),
    used_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS flash_sales (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    slug VARCHAR(255) NOT NULL UNIQUE,
    description TEXT,
    start_time TIMESTAMPTZ NOT NULL,
    end_time TIMESTAMPTZ NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT FALSE,
    is_deleted BOOLEAN DEFAULT FALSE,
    version BIGINT DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);
CREATE INDEX IF NOT EXISTS idx_flash_sale_active ON flash_sales(is_active);
CREATE INDEX IF NOT EXISTS idx_flash_sale_dates ON flash_sales(start_time, end_time);
CREATE INDEX IF NOT EXISTS idx_flash_sale_slug ON flash_sales(slug);

CREATE TABLE IF NOT EXISTS flash_sale_items (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    flash_sale_id UUID NOT NULL REFERENCES flash_sales(id),
    product_id UUID NOT NULL,
    variant_id UUID,
    flash_sale_price DECIMAL(19,4),
    price_currency VARCHAR(3),
    total_quantity INT NOT NULL,
    sold_quantity INT NOT NULL DEFAULT 0,
    limit_per_customer INT,
    is_active BOOLEAN DEFAULT TRUE,
    version BIGINT DEFAULT 0
);
CREATE INDEX IF NOT EXISTS idx_flash_sale_item_product ON flash_sale_items(product_id);
CREATE INDEX IF NOT EXISTS idx_flash_sale_item_sale ON flash_sale_items(flash_sale_id);

-- Settlement
CREATE TABLE IF NOT EXISTS vendor_accounts (
    id UUID PRIMARY KEY,
    vendor_id UUID NOT NULL UNIQUE,
    available_balance DECIMAL(19,4) DEFAULT 0,
    pending_balance DECIMAL(19,4) DEFAULT 0,
    total_earned DECIMAL(19,4) DEFAULT 0,
    total_withdrawn DECIMAL(19,4) DEFAULT 0,
    currency VARCHAR(3) DEFAULT 'BDT',
    version BIGINT DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ
);

CREATE TABLE IF NOT EXISTS settlements (
    id UUID PRIMARY KEY,
    vendor_id UUID NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    total_sales DECIMAL(19,4),
    total_commission DECIMAL(19,4),
    total_payout DECIMAL(19,4),
    currency VARCHAR(3) DEFAULT 'BDT',
    period_start TIMESTAMPTZ,
    period_end TIMESTAMPTZ,
    paid_at TIMESTAMPTZ,
    payment_reference VARCHAR(255),
    is_deleted BOOLEAN DEFAULT FALSE,
    version BIGINT DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);
CREATE INDEX IF NOT EXISTS idx_settlement_vendor ON settlements(vendor_id);
CREATE INDEX IF NOT EXISTS idx_settlement_status ON settlements(status);

CREATE TABLE IF NOT EXISTS settlement_items (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    settlement_id UUID NOT NULL REFERENCES settlements(id),
    sub_order_id UUID NOT NULL,
    order_amount DECIMAL(19,4),
    order_currency VARCHAR(3),
    commission_rate DECIMAL(5,2),
    commission_amount DECIMAL(19,4),
    vendor_earning DECIMAL(19,4),
    is_deleted BOOLEAN DEFAULT FALSE,
    version BIGINT DEFAULT 0,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS commission_rules (
    id UUID PRIMARY KEY,
    vendor_id UUID,
    category_id UUID,
    rate DECIMAL(5,2) NOT NULL,
    is_default BOOLEAN DEFAULT FALSE,
    version BIGINT DEFAULT 0,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- Loyalty
CREATE TABLE IF NOT EXISTS loyalty_accounts (
    id UUID PRIMARY KEY,
    customer_id UUID NOT NULL UNIQUE,
    total_points_earned BIGINT DEFAULT 0,
    available_points BIGINT DEFAULT 0,
    lifetime_spent DECIMAL(19,4) DEFAULT 0,
    tier VARCHAR(20) DEFAULT 'BRONZE',
    version BIGINT DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ
);

CREATE TABLE IF NOT EXISTS loyalty_transactions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    customer_id UUID NOT NULL,
    points BIGINT NOT NULL,
    type VARCHAR(20) NOT NULL,
    order_id UUID,
    description VARCHAR(500),
    version BIGINT DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Fraud
CREATE TABLE IF NOT EXISTS fraud_rules (
    id UUID PRIMARY KEY,
    rule_name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(500),
    points INT NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    rule_type VARCHAR(30),
    version BIGINT DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ
);

CREATE TABLE IF NOT EXISTS fraud_checks (
    id UUID PRIMARY KEY,
    order_id UUID,
    customer_id UUID,
    risk_score INT NOT NULL,
    status VARCHAR(20) NOT NULL,
    reasons TEXT,
    reviewed_by UUID,
    reviewed_at TIMESTAMPTZ,
    review_notes TEXT,
    checked_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    version BIGINT DEFAULT 0
);

-- Pricing
CREATE TABLE IF NOT EXISTS tax_rules (
    id UUID PRIMARY KEY,
    country_code VARCHAR(3) NOT NULL,
    state_code VARCHAR(10),
    category_id UUID,
    tax_type VARCHAR(20) NOT NULL,
    rate DECIMAL(5,2) NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    priority INT DEFAULT 1,
    version BIGINT DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ
);

CREATE TABLE IF NOT EXISTS currency_exchange_rates (
    id UUID PRIMARY KEY,
    from_currency VARCHAR(3) NOT NULL,
    to_currency VARCHAR(3) NOT NULL,
    rate DECIMAL(19,6) NOT NULL,
    source VARCHAR(50),
    version BIGINT DEFAULT 0,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- Wishlist
CREATE TABLE IF NOT EXISTS wishlist_items (
    id UUID PRIMARY KEY,
    customer_id UUID NOT NULL,
    product_id UUID NOT NULL,
    variant_id UUID,
    notes VARCHAR(500),
    added_from VARCHAR(50),
    is_deleted BOOLEAN DEFAULT FALSE,
    version BIGINT DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    UNIQUE(customer_id, product_id)
);

-- Media
CREATE TABLE IF NOT EXISTS media_assets (
    id UUID PRIMARY KEY,
    file_name VARCHAR(255) NOT NULL,
    original_file_name VARCHAR(500),
    content_type VARCHAR(100),
    size_in_bytes BIGINT,
    bucket_key VARCHAR(500) NOT NULL UNIQUE,
    url VARCHAR(2000),
    width INT,
    height INT,
    entity_type VARCHAR(50),
    entity_id UUID,
    tags VARCHAR(500),
    is_deleted BOOLEAN DEFAULT FALSE,
    version BIGINT DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);

-- Notification Templates
CREATE TABLE IF NOT EXISTS notification_templates (
    id UUID PRIMARY KEY,
    template_key VARCHAR(100) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    subject VARCHAR(255),
    body TEXT NOT NULL,
    channels VARCHAR(255),
    is_active BOOLEAN DEFAULT TRUE,
    is_deleted BOOLEAN DEFAULT FALSE,
    version BIGINT DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ
);