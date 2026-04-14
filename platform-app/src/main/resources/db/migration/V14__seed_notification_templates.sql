-- V14__seed_notification_templates.sql
INSERT INTO notification_templates (id, template_key, name, subject, body, channels, is_active, version, created_at)
VALUES
    (gen_random_uuid(), 'ORDER_PLACED', 'Order Placed', 'Order Confirmation #{{orderNumber}}', 'Dear customer, your order {{orderNumber}} has been placed. Total: {{totalAmount}}.', 'EMAIL,PUSH,IN_APP', true, 0, now()),
    (gen_random_uuid(), 'ORDER_SHIPPED', 'Order Shipped', 'Your order #{{orderNumber}} has been shipped', 'Tracking number: {{trackingNumber}} via {{carrier}}.', 'EMAIL,SMS,PUSH', true, 0, now()),
    (gen_random_uuid(), 'PAYMENT_CONFIRMED', 'Payment Confirmed', 'Payment received for order #{{orderId}}', 'Amount: {{amount}} has been charged.', 'EMAIL', true, 0, now())
ON CONFLICT (template_key) DO NOTHING;
