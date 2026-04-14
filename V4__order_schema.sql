-- Orders
CREATE TABLE orders (
    id UUID PRIMARY KEY,
    order_number VARCHAR(50) NOT NULL UNIQUE,
    customer_id UUID NOT NULL,
    status VARCHAR(30) NOT NULL,
    subtotal DECIMAL(19,4),
    subtotal_currency VARCHAR(3),
    shipping_cost DECIMAL(19,4),
    shipping_currency VARCHAR(3),
    discount_amount DECIMAL(19,4),
    discount_currency VARCHAR(3),
    tax_amount DECIMAL(19,4),
    tax_currency VARCHAR(3),
    total_amount DECIMAL(19,4),
    total_currency VARCHAR(3),
    coupon_code VARCHAR(50),
    payment_method VARCHAR(50),
    payment_id UUID,
    shipping_address_line1 VARCHAR(255),
    shipping_address_line2 VARCHAR(255),
    shipping_city VARCHAR(100),
    shipping_state VARCHAR(100),
    shipping_postal_code VARCHAR(20),
    shipping_country VARCHAR(100),
    shipping_latitude DOUBLE PRECISION,
    shipping_longitude DOUBLE PRECISION,
    customer_note TEXT,
    is_deleted BOOLEAN DEFAULT FALSE,
    version BIGINT DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);

CREATE INDEX idx_order_customer ON orders(customer_id);
CREATE INDEX idx_order_status ON orders(status);
CREATE INDEX idx_order_created ON orders(created_at);

-- Sub Orders (per vendor)
CREATE TABLE sub_orders (
    id UUID PRIMARY KEY,
    sub_order_number VARCHAR(50) NOT NULL UNIQUE,
    order_id UUID NOT NULL REFERENCES orders(id),
    vendor_id UUID NOT NULL,
    status VARCHAR(30) NOT NULL,
    subtotal DECIMAL(19,4),
    subtotal_currency VARCHAR(3),
    shipping_cost DECIMAL(19,4),
    shipping_currency VARCHAR(3),
    commission_amount DECIMAL(19,4),
    commission_currency VARCHAR(3),
    shipment_id UUID,
    tracking_number VARCHAR(100),
    is_deleted BOOLEAN DEFAULT FALSE,
    version BIGINT DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);

CREATE INDEX idx_suborder_order ON sub_orders(order_id);
CREATE INDEX idx_suborder_vendor ON sub_orders(vendor_id);
CREATE INDEX idx_suborder_status ON sub_orders(status);

-- Order Items
CREATE TABLE order_items (
    id UUID PRIMARY KEY,
    sub_order_id UUID NOT NULL REFERENCES sub_orders(id),
    product_id UUID NOT NULL,
    variant_id UUID,
    product_name VARCHAR(500) NOT NULL,
    product_image VARCHAR(500),
    sku VARCHAR(100),
    quantity INT NOT NULL,
    unit_price DECIMAL(19,4),
    unit_price_currency VARCHAR(3),
    total_price DECIMAL(19,4),
    total_price_currency VARCHAR(3),
    version BIGINT DEFAULT 0
);

-- Order Timeline
CREATE TABLE order_timeline (
    id UUID PRIMARY KEY,
    order_id UUID NOT NULL REFERENCES orders(id),
    status VARCHAR(50) NOT NULL,
    description TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

-- Return Requests
CREATE TABLE return_requests (
    id UUID PRIMARY KEY,
    order_id UUID NOT NULL REFERENCES orders(id),
    sub_order_id UUID REFERENCES sub_orders(id),
    customer_id UUID NOT NULL,
    reason VARCHAR(500),
    description TEXT,
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    refund_amount DECIMAL(19,4),
    refund_currency VARCHAR(3),
    images TEXT[],
    admin_notes TEXT,
    is_deleted BOOLEAN DEFAULT FALSE,
    version BIGINT DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);
