-- ============================================
-- V3: Order, Payment, Inventory, Cart Schemas
-- ============================================

-- Inventory
CREATE TABLE IF NOT EXISTS inventory_items (
    id UUID PRIMARY KEY,
    product_id UUID NOT NULL,
    variant_id UUID,
    warehouse_id UUID,
    quantity INT NOT NULL DEFAULT 0,
    reserved_quantity INT NOT NULL DEFAULT 0,
    reorder_point INT DEFAULT 10,
    is_deleted BOOLEAN DEFAULT FALSE,
    version BIGINT DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    UNIQUE(product_id, variant_id, warehouse_id)
);

CREATE TABLE IF NOT EXISTS stock_movements (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    inventory_item_id UUID NOT NULL REFERENCES inventory_items(id),
    type VARCHAR(30) NOT NULL,
    quantity INT NOT NULL,
    reason VARCHAR(500),
    reference_id UUID,
    version BIGINT DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS stock_reservations (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    inventory_item_id UUID NOT NULL REFERENCES inventory_items(id),
    quantity INT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    expires_at TIMESTAMP WITH TIME ZONE,
    order_id UUID
);

-- Cart
CREATE TABLE IF NOT EXISTS carts (
    id UUID PRIMARY KEY,
    customer_id UUID NOT NULL UNIQUE,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    is_deleted BOOLEAN DEFAULT FALSE,
    version BIGINT DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS cart_items (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    cart_id UUID NOT NULL REFERENCES carts(id) ON DELETE CASCADE,
    product_id UUID NOT NULL,
    variant_id UUID,
    quantity INT NOT NULL DEFAULT 1,
    version BIGINT DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE
);

-- Orders
CREATE TABLE IF NOT EXISTS orders (
    id UUID PRIMARY KEY,
    order_number VARCHAR(50) NOT NULL UNIQUE,
    customer_id UUID NOT NULL,
    status VARCHAR(30) NOT NULL,
    subtotal DECIMAL(19,4),
    subtotal_currency VARCHAR(3) DEFAULT 'BDT',
    shipping_cost DECIMAL(19,4) DEFAULT 0,
    shipping_currency VARCHAR(3) DEFAULT 'BDT',
    discount_amount DECIMAL(19,4) DEFAULT 0,
    discount_currency VARCHAR(3) DEFAULT 'BDT',
    tax_amount DECIMAL(19,4) DEFAULT 0,
    tax_currency VARCHAR(3) DEFAULT 'BDT',
    total_amount DECIMAL(19,4),
    total_currency VARCHAR(3) DEFAULT 'BDT',
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

CREATE INDEX IF NOT EXISTS idx_order_customer ON orders(customer_id);
CREATE INDEX IF NOT EXISTS idx_order_status ON orders(status);
CREATE INDEX IF NOT EXISTS idx_order_created ON orders(created_at DESC);

CREATE TABLE IF NOT EXISTS sub_orders (
    id UUID PRIMARY KEY,
    sub_order_number VARCHAR(50) NOT NULL UNIQUE,
    order_id UUID NOT NULL REFERENCES orders(id),
    vendor_id UUID NOT NULL,
    status VARCHAR(30) NOT NULL,
    subtotal DECIMAL(19,4),
    subtotal_currency VARCHAR(3) DEFAULT 'BDT',
    shipping_cost DECIMAL(19,4) DEFAULT 0,
    shipping_currency VARCHAR(3) DEFAULT 'BDT',
    commission_amount DECIMAL(19,4),
    commission_currency VARCHAR(3) DEFAULT 'BDT',
    shipment_id UUID,
    tracking_number VARCHAR(100),
    is_deleted BOOLEAN DEFAULT FALSE,
    version BIGINT DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);

CREATE INDEX IF NOT EXISTS idx_suborder_order ON sub_orders(order_id);
CREATE INDEX IF NOT EXISTS idx_suborder_vendor ON sub_orders(vendor_id);

CREATE TABLE IF NOT EXISTS order_items (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    sub_order_id UUID NOT NULL REFERENCES sub_orders(id),
    product_id UUID NOT NULL,
    variant_id UUID,
    product_name VARCHAR(500) NOT NULL,
    product_image VARCHAR(500),
    sku VARCHAR(100),
    quantity INT NOT NULL,
    unit_price DECIMAL(19,4),
    unit_price_currency VARCHAR(3) DEFAULT 'BDT',
    total_price DECIMAL(19,4),
    total_price_currency VARCHAR(3) DEFAULT 'BDT',
    version BIGINT DEFAULT 0
);

CREATE TABLE IF NOT EXISTS order_timeline (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_id UUID NOT NULL REFERENCES orders(id),
    status VARCHAR(50) NOT NULL,
    description TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS return_requests (
    id UUID PRIMARY KEY,
    order_id UUID NOT NULL REFERENCES orders(id),
    sub_order_id UUID REFERENCES sub_orders(id),
    customer_id UUID NOT NULL,
    reason VARCHAR(500),
    description TEXT,
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    refund_amount DECIMAL(19,4),
    refund_currency VARCHAR(3),
    admin_notes TEXT,
    is_deleted BOOLEAN DEFAULT FALSE,
    version BIGINT DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);

-- Payments
CREATE TABLE IF NOT EXISTS payments (
    id UUID PRIMARY KEY,
    order_id UUID NOT NULL,
    customer_id UUID NOT NULL,
    amount DECIMAL(19,4) NOT NULL,
    currency VARCHAR(3) DEFAULT 'BDT',
    gateway VARCHAR(30) NOT NULL,
    payment_method VARCHAR(30) NOT NULL,
    status VARCHAR(30) NOT NULL,
    gateway_transaction_id VARCHAR(255),
    gateway_redirect_url VARCHAR(1000),
    failure_reason TEXT,
    is_deleted BOOLEAN DEFAULT FALSE,
    version BIGINT DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS payment_transactions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    payment_id UUID NOT NULL REFERENCES payments(id),
    type VARCHAR(30) NOT NULL,
    amount DECIMAL(19,4),
    currency VARCHAR(3) DEFAULT 'BDT',
    status VARCHAR(30),
    gateway_response TEXT,
    version BIGINT DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS refunds (
    id UUID PRIMARY KEY,
    payment_id UUID NOT NULL REFERENCES payments(id),
    order_id UUID NOT NULL,
    amount DECIMAL(19,4) NOT NULL,
    currency VARCHAR(3) DEFAULT 'BDT',
    reason VARCHAR(500),
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    gateway_refund_id VARCHAR(255),
    is_deleted BOOLEAN DEFAULT FALSE,
    version BIGINT DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);

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
    start_date TIMESTAMP WITH TIME ZONE,
    end_date TIMESTAMP WITH TIME ZONE,
    is_active BOOLEAN DEFAULT TRUE,
    is_deleted BOOLEAN DEFAULT FALSE,
    version BIGINT DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS coupon_usages (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    coupon_id UUID NOT NULL REFERENCES coupons(id),
    customer_id UUID NOT NULL,
    order_id UUID NOT NULL,
    discount_amount DECIMAL(19,4),
    used_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

-- Shipping
CREATE TABLE IF NOT EXISTS shipments (
    id UUID PRIMARY KEY,
    sub_order_id UUID NOT NULL,
    tracking_number VARCHAR(100),
    carrier VARCHAR(100),
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    estimated_delivery TIMESTAMP WITH TIME ZONE,
    actual_delivery TIMESTAMP WITH TIME ZONE,
    shipping_cost DECIMAL(19,4),
    currency VARCHAR(3) DEFAULT 'BDT',
    is_deleted BOOLEAN DEFAULT FALSE,
    version BIGINT DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);

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
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE
);

CREATE TABLE IF NOT EXISTS settlements (
    id UUID PRIMARY KEY,
    vendor_id UUID NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    total_sales DECIMAL(19,4),
    total_commission DECIMAL(19,4),
    total_payout DECIMAL(19,4),
    currency VARCHAR(3) DEFAULT 'BDT',
    period_start TIMESTAMP WITH TIME ZONE,
    period_end TIMESTAMP WITH TIME ZONE,
    paid_at TIMESTAMP WITH TIME ZONE,
    is_deleted BOOLEAN DEFAULT FALSE,
    version BIGINT DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);

-- Notifications
CREATE TABLE IF NOT EXISTS notifications (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    title VARCHAR(255) NOT NULL,
    message TEXT,
    type VARCHAR(50),
    channel VARCHAR(30),
    status VARCHAR(20) DEFAULT 'PENDING',
    read_at TIMESTAMP WITH TIME ZONE,
    metadata TEXT,
    is_deleted BOOLEAN DEFAULT FALSE,
    version BIGINT DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE
);

CREATE INDEX IF NOT EXISTS idx_notification_user ON notifications(user_id, created_at DESC);

-- CMS
CREATE TABLE IF NOT EXISTS banners (
    id UUID PRIMARY KEY,
    title VARCHAR(255),
    image_url VARCHAR(500) NOT NULL,
    link_url VARCHAR(500),
    position VARCHAR(50),
    sort_order INT DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE,
    start_date TIMESTAMP WITH TIME ZONE,
    end_date TIMESTAMP WITH TIME ZONE,
    is_deleted BOOLEAN DEFAULT FALSE,
    version BIGINT DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);

-- Support
CREATE TABLE IF NOT EXISTS support_tickets (
    id UUID PRIMARY KEY,
    ticket_number VARCHAR(50) NOT NULL UNIQUE,
    customer_id UUID NOT NULL,
    order_id UUID,
    category VARCHAR(50) NOT NULL,
    subject VARCHAR(255) NOT NULL,
    description TEXT,
    status VARCHAR(30) NOT NULL DEFAULT 'OPEN',
    priority VARCHAR(20) DEFAULT 'MEDIUM',
    assigned_to UUID,
    is_deleted BOOLEAN DEFAULT FALSE,
    version BIGINT DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS ticket_messages (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    ticket_id UUID NOT NULL REFERENCES support_tickets(id),
    sender_id UUID NOT NULL,
    message TEXT NOT NULL,
    is_internal BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);