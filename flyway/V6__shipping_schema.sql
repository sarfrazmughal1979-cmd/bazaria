
-- Delivery Zones
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
    version BIGINT DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE
);

-- Shipping Rates (cached)
CREATE TABLE IF NOT EXISTS shipping_rates (
    id UUID PRIMARY KEY,
    from_postal VARCHAR(20),
    to_postal VARCHAR(20),
    weight_kg DECIMAL(10,2),
    method VARCHAR(30),
    carrier VARCHAR(50),
    cost DECIMAL(19,4) NOT NULL,
    estimated_days INT,
    expires_at TIMESTAMP WITH TIME ZONE,
    version BIGINT DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_shipping_rates_lookup ON shipping_rates(from_postal, to_postal, weight_kg);

-- Shipments
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
    estimated_delivery_date TIMESTAMP WITH TIME ZONE,
    actual_delivery_date TIMESTAMP WITH TIME ZONE,
    carrier_response TEXT,
    last_tracking_update TIMESTAMP WITH TIME ZONE,
    is_deleted BOOLEAN DEFAULT FALSE,
    version BIGINT DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);

CREATE INDEX idx_shipments_sub_order ON shipments(sub_order_id);
CREATE INDEX idx_shipments_tracking ON shipments(tracking_number);
CREATE INDEX idx_shipments_status ON shipments(status);
CREATE INDEX idx_shipments_vendor ON shipments(vendor_id);