CREATE TABLE IF NOT EXISTS sales_metrics (
    id UUID PRIMARY KEY,
    metric_date DATE NOT NULL,
    period_type VARCHAR(10) NOT NULL,
    vendor_id UUID,
    total_orders BIGINT DEFAULT 0,
    total_revenue DECIMAL(19,4) DEFAULT 0,
    total_commission DECIMAL(19,4) DEFAULT 0,
    total_tax DECIMAL(19,4) DEFAULT 0,
    average_order_value DECIMAL(19,4) DEFAULT 0,
    unique_customers BIGINT DEFAULT 0,
    items_sold BIGINT DEFAULT 0,
    version BIGINT DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
CREATE INDEX IF NOT EXISTS idx_sales_metrics_date ON sales_metrics(metric_date);
CREATE INDEX IF NOT EXISTS idx_sales_metrics_vendor ON sales_metrics(vendor_id);

CREATE TABLE IF NOT EXISTS vendor_metrics (
    id UUID PRIMARY KEY,
    vendor_id UUID NOT NULL,
    metric_date DATE NOT NULL,
    total_products INT DEFAULT 0,
    active_products INT DEFAULT 0,
    total_orders BIGINT DEFAULT 0,
    total_revenue DECIMAL(19,4) DEFAULT 0,
    total_commission DECIMAL(19,4) DEFAULT 0,
    average_rating DECIMAL(3,2) DEFAULT 0,
    on_time_delivery_rate DECIMAL(5,2) DEFAULT 0,
    dispute_rate DECIMAL(5,2) DEFAULT 0,
    version BIGINT DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
CREATE INDEX IF NOT EXISTS idx_vendor_metrics_vendor_date ON vendor_metrics(vendor_id, metric_date);

CREATE TABLE IF NOT EXISTS product_metrics (
    id UUID PRIMARY KEY,
    product_id UUID NOT NULL,
    vendor_id UUID NOT NULL,
    metric_date DATE NOT NULL,
    views BIGINT DEFAULT 0,
    add_to_carts BIGINT DEFAULT 0,
    orders BIGINT DEFAULT 0,
    quantity_sold BIGINT DEFAULT 0,
    revenue DECIMAL(19,4) DEFAULT 0,
    conversion_rate DECIMAL(5,2) DEFAULT 0,
    average_rating DECIMAL(3,2) DEFAULT 0,
    version BIGINT DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
CREATE INDEX IF NOT EXISTS idx_product_metrics_product ON product_metrics(product_id, metric_date);

CREATE TABLE IF NOT EXISTS customer_metrics (
    id UUID PRIMARY KEY,
    customer_id UUID NOT NULL,
    metric_date DATE NOT NULL,
    total_orders BIGINT DEFAULT 0,
    total_spent DECIMAL(19,4) DEFAULT 0,
    last_order_date DATE,
    is_active BOOLEAN DEFAULT TRUE,
    version BIGINT DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
CREATE INDEX IF NOT EXISTS idx_customer_metrics_customer ON customer_metrics(customer_id);

CREATE TABLE IF NOT EXISTS dashboard_widgets (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    widget_type VARCHAR(50) NOT NULL,
    title VARCHAR(255),
    position_x INT DEFAULT 0,
    position_y INT DEFAULT 0,
    width INT DEFAULT 2,
    height INT DEFAULT 2,
    configuration TEXT,
    is_deleted BOOLEAN DEFAULT FALSE,
    version BIGINT DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS daily_aggregations (
    id UUID PRIMARY KEY,
    aggregation_date DATE NOT NULL UNIQUE,
    platform_revenue DECIMAL(19,4) DEFAULT 0,
    platform_orders BIGINT DEFAULT 0,
    new_customers BIGINT DEFAULT 0,
    active_vendors BIGINT DEFAULT 0,
    total_products_sold BIGINT DEFAULT 0,
    version BIGINT DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
CREATE INDEX IF NOT EXISTS idx_daily_agg_date ON daily_aggregations(aggregation_date);