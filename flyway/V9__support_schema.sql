-- Ticket Categories
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
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);

-- Support Tickets
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
    assigned_at TIMESTAMP WITH TIME ZONE,
    resolved_at TIMESTAMP WITH TIME ZONE,
    closed_at TIMESTAMP WITH TIME ZONE,
    first_response_at TIMESTAMP WITH TIME ZONE,
    last_response_at TIMESTAMP WITH TIME ZONE,
    customer_rating INT CHECK (customer_rating BETWEEN 1 AND 5),
    customer_feedback TEXT,
    is_deleted BOOLEAN DEFAULT FALSE,
    version BIGINT DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);

CREATE INDEX idx_support_tickets_customer ON support_tickets(customer_id);
CREATE INDEX idx_support_tickets_vendor ON support_tickets(vendor_id);
CREATE INDEX idx_support_tickets_status ON support_tickets(status);
CREATE INDEX idx_support_tickets_assigned ON support_tickets(assigned_to);
CREATE INDEX idx_support_tickets_created ON support_tickets(created_at);

-- Ticket Messages
CREATE TABLE IF NOT EXISTS support_ticket_messages (
    id UUID PRIMARY KEY,
    ticket_id UUID NOT NULL REFERENCES support_tickets(id) ON DELETE CASCADE,
    sender_id UUID NOT NULL,
    sender_type VARCHAR(20) NOT NULL,
    message TEXT NOT NULL,
    is_internal BOOLEAN DEFAULT FALSE,
    attachments TEXT,
    read_at TIMESTAMP WITH TIME ZONE,
    version BIGINT DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_ticket_messages_ticket ON support_ticket_messages(ticket_id, created_at);

-- Disputes
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
    resolved_at TIMESTAMP WITH TIME ZONE,
    evidence_urls TEXT,
    admin_notes TEXT,
    is_deleted BOOLEAN DEFAULT FALSE,
    version BIGINT DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);

CREATE INDEX idx_support_disputes_order ON support_disputes(order_id);
CREATE INDEX idx_support_disputes_customer ON support_disputes(customer_id);
CREATE INDEX idx_support_disputes_vendor ON support_disputes(vendor_id);
CREATE INDEX idx_support_disputes_status ON support_disputes(status);

-- Dispute Resolution Steps
CREATE TABLE IF NOT EXISTS support_dispute_resolutions (
    id UUID PRIMARY KEY,
    dispute_id UUID NOT NULL REFERENCES support_disputes(id) ON DELETE CASCADE,
    action VARCHAR(50) NOT NULL,
    message TEXT,
    proposed_amount DECIMAL(19,4),
    actor_id UUID,
    actor_type VARCHAR(20),
    version BIGINT DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

-- Chat Sessions
CREATE TABLE IF NOT EXISTS support_chat_sessions (
    id UUID PRIMARY KEY,
    customer_id UUID,
    vendor_id UUID,
    agent_id UUID,
    ticket_id UUID,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    started_at TIMESTAMP WITH TIME ZONE NOT NULL,
    ended_at TIMESTAMP WITH TIME ZONE,
    customer_rating INT,
    transcript TEXT,
    version BIGINT DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE
);

CREATE INDEX idx_chat_sessions_customer ON support_chat_sessions(customer_id) WHERE status = 'ACTIVE';
CREATE INDEX idx_chat_sessions_vendor ON support_chat_sessions(vendor_id) WHERE status = 'ACTIVE';
CREATE INDEX idx_chat_sessions_agent ON support_chat_sessions(agent_id) WHERE status = 'ACTIVE';

-- Chat Messages
CREATE TABLE IF NOT EXISTS support_chat_messages (
    id UUID PRIMARY KEY,
    session_id UUID NOT NULL REFERENCES support_chat_sessions(id) ON DELETE CASCADE,
    sender_id UUID NOT NULL,
    sender_type VARCHAR(20) NOT NULL,
    message TEXT NOT NULL,
    sent_at TIMESTAMP WITH TIME ZONE NOT NULL,
    delivered_at TIMESTAMP WITH TIME ZONE,
    read_at TIMESTAMP WITH TIME ZONE,
    version BIGINT DEFAULT 0
);

CREATE INDEX idx_chat_messages_session ON support_chat_messages(session_id, sent_at);

-- Seed default ticket categories
INSERT INTO support_ticket_categories (id, name, description, icon, sort_order, is_active) VALUES
    (gen_random_uuid(), 'Order Issue', 'Problems with order delivery, tracking, or status', 'package', 1, TRUE),
    (gen_random_uuid(), 'Payment Problem', 'Issues with payment, refunds, or billing', 'credit-card', 2, TRUE),
    (gen_random_uuid(), 'Product Quality', 'Damaged, defective, or wrong product received', 'alert-circle', 3, TRUE),
    (gen_random_uuid(), 'Return & Refund', 'Request return or track refund status', 'rotate-ccw', 4, TRUE),
    (gen_random_uuid(), 'Vendor Question', 'Questions about a specific vendor', 'store', 5, TRUE),
    (gen_random_uuid(), 'Account Issue', 'Login, profile, or security concerns', 'user', 6, TRUE),
    (gen_random_uuid(), 'Technical Support', 'Website or app technical problems', 'monitor', 7, TRUE),
    (gen_random_uuid(), 'General Inquiry', 'Other questions or feedback', 'help-circle', 8, TRUE);