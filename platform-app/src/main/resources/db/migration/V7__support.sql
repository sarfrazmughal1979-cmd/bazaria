CREATE TABLE IF NOT EXISTS support_ticket_categories (
    id UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    icon VARCHAR(50),
    sort_order INT DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE,
    auto_assign_role VARCHAR(50),
    is_deleted BOOLEAN DEFAULT FALSE,
    version BIGINT DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS support_tickets (
    id UUID PRIMARY KEY,
    ticket_number VARCHAR(50) NOT NULL UNIQUE,
    customer_id UUID NOT NULL,
    vendor_id UUID,
    order_id UUID,
    category_id UUID REFERENCES support_ticket_categories(id),
    subject VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    status VARCHAR(30) NOT NULL,
    priority VARCHAR(20) NOT NULL,
    assigned_to UUID,
    assigned_at TIMESTAMPTZ,
    resolved_at TIMESTAMPTZ,
    closed_at TIMESTAMPTZ,
    first_response_at TIMESTAMPTZ,
    last_response_at TIMESTAMPTZ,
    customer_rating INT CHECK (customer_rating BETWEEN 1 AND 5),
    customer_feedback TEXT,
    is_deleted BOOLEAN DEFAULT FALSE,
    version BIGINT DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);
CREATE INDEX IF NOT EXISTS idx_ticket_customer ON support_tickets(customer_id);
CREATE INDEX IF NOT EXISTS idx_ticket_vendor ON support_tickets(vendor_id);
CREATE INDEX IF NOT EXISTS idx_ticket_status ON support_tickets(status);
CREATE INDEX IF NOT EXISTS idx_ticket_assigned ON support_tickets(assigned_to);
CREATE INDEX IF NOT EXISTS idx_ticket_created ON support_tickets(created_at);

CREATE TABLE IF NOT EXISTS support_ticket_messages (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    ticket_id UUID NOT NULL REFERENCES support_tickets(id) ON DELETE CASCADE,
    sender_id UUID NOT NULL,
    sender_type VARCHAR(20) NOT NULL,
    message TEXT NOT NULL,
    is_internal BOOLEAN DEFAULT FALSE,
    attachments TEXT,
    read_at TIMESTAMPTZ,
    version BIGINT DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
CREATE INDEX IF NOT EXISTS idx_ticket_messages_ticket ON support_ticket_messages(ticket_id, created_at);

CREATE TABLE IF NOT EXISTS support_disputes (
    id UUID PRIMARY KEY,
    dispute_number VARCHAR(50) NOT NULL UNIQUE,
    order_id UUID NOT NULL,
    sub_order_id UUID,
    customer_id UUID NOT NULL,
    vendor_id UUID NOT NULL,
    reason VARCHAR(100) NOT NULL,
    description TEXT,
    disputed_amount DECIMAL(19,4),
    dispute_currency VARCHAR(3) DEFAULT 'BDT',
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    resolution VARCHAR(50),
    resolution_amount DECIMAL(19,4),
    resolved_by UUID,
    resolved_at TIMESTAMPTZ,
    evidence_urls TEXT,
    admin_notes TEXT,
    is_deleted BOOLEAN DEFAULT FALSE,
    version BIGINT DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);
CREATE INDEX IF NOT EXISTS idx_dispute_order ON support_disputes(order_id);
CREATE INDEX IF NOT EXISTS idx_dispute_customer ON support_disputes(customer_id);
CREATE INDEX IF NOT EXISTS idx_dispute_vendor ON support_disputes(vendor_id);
CREATE INDEX IF NOT EXISTS idx_dispute_status ON support_disputes(status);

CREATE TABLE IF NOT EXISTS support_dispute_resolutions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    dispute_id UUID NOT NULL REFERENCES support_disputes(id) ON DELETE CASCADE,
    action VARCHAR(50) NOT NULL,
    message TEXT,
    proposed_amount DECIMAL(19,4),
    actor_id UUID,
    actor_type VARCHAR(20),
    version BIGINT DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS support_chat_sessions (
    id UUID PRIMARY KEY,
    customer_id UUID,
    vendor_id UUID,
    agent_id UUID,
    ticket_id UUID,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    started_at TIMESTAMPTZ NOT NULL,
    ended_at TIMESTAMPTZ,
    customer_rating INT,
    transcript TEXT,
    is_deleted BOOLEAN DEFAULT FALSE,
    version BIGINT DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);
CREATE INDEX IF NOT EXISTS idx_chat_customer ON support_chat_sessions(customer_id);
CREATE INDEX IF NOT EXISTS idx_chat_vendor ON support_chat_sessions(vendor_id);
CREATE INDEX IF NOT EXISTS idx_chat_agent ON support_chat_sessions(agent_id);
CREATE INDEX IF NOT EXISTS idx_chat_status ON support_chat_sessions(status);

CREATE TABLE IF NOT EXISTS support_chat_messages (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    session_id UUID NOT NULL REFERENCES support_chat_sessions(id) ON DELETE CASCADE,
    sender_id UUID NOT NULL,
    sender_type VARCHAR(20) NOT NULL,
    message TEXT NOT NULL,
    sent_at TIMESTAMPTZ NOT NULL,
    delivered_at TIMESTAMPTZ,
    read_at TIMESTAMPTZ,
    version BIGINT DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
CREATE INDEX IF NOT EXISTS idx_chat_messages_session ON support_chat_messages(session_id, sent_at);

-- Seed default ticket categories
INSERT INTO support_ticket_categories (id, name, description, icon, sort_order, is_active) VALUES
    (gen_random_uuid(), 'Order Issue', 'Problems with order delivery, tracking, or status', 'package', 1, TRUE),
    (gen_random_uuid(), 'Payment Problem', 'Issues with payment, refunds, or billing', 'credit-card', 2, TRUE),
    (gen_random_uuid(), 'Product Quality', 'Damaged, defective, or wrong product received', 'alert-circle', 3, TRUE),
    (gen_random_uuid(), 'Return & Refund', 'Request return or track refund status', 'rotate-ccw', 4, TRUE),
    (gen_random_uuid(), 'Vendor Question', 'Questions about a specific vendor', 'store', 5, TRUE),
    (gen_random_uuid(), 'Account Issue', 'Login, profile, or security concerns', 'user', 6, TRUE),
    (gen_random_uuid(), 'Technical Support', 'Website or app technical problems', 'monitor', 7, TRUE),
    (gen_random_uuid(), 'General Inquiry', 'Other questions or feedback', 'help-circle', 8, TRUE)
ON CONFLICT DO NOTHING;