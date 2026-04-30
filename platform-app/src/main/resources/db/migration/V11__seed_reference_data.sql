-- =============================================
-- V17: Seed reference data for all modules
-- =============================================

-- ---------------------------------------------
-- 1. Roles (already seeded, but safe to repeat)
-- ---------------------------------------------
INSERT INTO roles (id, name, description, created_at, created_by) VALUES
    (gen_random_uuid(), 'ADMIN', 'Platform Administrator', now(), 'Mughal'),
    (gen_random_uuid(), 'VENDOR', 'Vendor/Seller', now(), 'Mughal'),
    (gen_random_uuid(), 'CUSTOMER', 'Customer/Buyer', now(), 'Mughal'),
    (gen_random_uuid(), 'SUPPORT', 'Customer Support Agent', now(), 'Mughal')
ON CONFLICT (name) DO NOTHING;

-- ---------------------------------------------
-- 2. Permissions (extended for new modules)
-- ---------------------------------------------
INSERT INTO permissions (id, name, description, module, created_at, created_by) VALUES
    -- Catalog
    (gen_random_uuid(), 'PRODUCT_CREATE', 'Create products', 'CATALOG', now(), 'Mughal'),
    (gen_random_uuid(), 'PRODUCT_UPDATE', 'Update products', 'CATALOG', now(), 'Mughal'),
    (gen_random_uuid(), 'PRODUCT_DELETE', 'Delete products', 'CATALOG', now(), 'Mughal'),
    (gen_random_uuid(), 'PRODUCT_APPROVE', 'Approve products', 'CATALOG', now(), 'Mughal'),

    -- Order
    (gen_random_uuid(), 'ORDER_VIEW', 'View orders', 'ORDER', now(), 'Mughal'),
    (gen_random_uuid(), 'ORDER_MANAGE', 'Manage orders', 'ORDER', now(), 'Mughal'),

    -- Vendor
    (gen_random_uuid(), 'VENDOR_MANAGE', 'Manage vendors', 'IAM', now(), 'Mughal'),

    -- Settlement
    (gen_random_uuid(), 'SETTLEMENT_VIEW', 'View settlements', 'SETTLEMENT', now(), 'Mughal'),
    (gen_random_uuid(), 'SETTLEMENT_MANAGE', 'Manage settlements', 'SETTLEMENT', now(), 'Mughal'),

    -- Analytics
    (gen_random_uuid(), 'ANALYTICS_VIEW', 'View analytics', 'ANALYTICS', now(), 'Mughal'),

    -- CMS
    (gen_random_uuid(), 'CMS_MANAGE', 'Manage CMS content', 'CMS', now(), 'Mughal'),

    -- Support
    (gen_random_uuid(), 'SUPPORT_MANAGE', 'Manage support tickets', 'SUPPORT', now(), 'Mughal'),

    -- Reviews
    (gen_random_uuid(), 'REVIEW_MANAGE', 'Moderate reviews', 'REVIEW', now(), 'Mughal'),

    -- Search
    (gen_random_uuid(), 'SEARCH_ADMIN', 'Manage search index', 'SEARCH', now(), 'Mughal'),

    -- Wishlist (customer only, but admin might also view)
    (gen_random_uuid(), 'WISHLIST_VIEW', 'View wishlist', 'WISHLIST', now(), 'Mughal'),

    -- Media
    (gen_random_uuid(), 'MEDIA_MANAGE', 'Manage media assets', 'MEDIA', now(), 'Mughal'),

    -- Loyalty
    (gen_random_uuid(), 'LOYALTY_MANAGE', 'Manage loyalty rules', 'LOYALTY', now(), 'Mughal'),

    -- Fraud
    (gen_random_uuid(), 'FRAUD_MANAGE', 'Manage fraud rules', 'FRAUD', now(), 'Mughal')
ON CONFLICT (name) DO NOTHING;

-- ---------------------------------------------
-- 3. Role-Permission assignments
-- ---------------------------------------------
-- ADMIN gets all permissions
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p WHERE r.name = 'ADMIN'
ON CONFLICT DO NOTHING;

-- VENDOR gets catalog CRUD, order view/manage, settlement view, analytics view
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.name = 'VENDOR' AND p.name IN (
    'PRODUCT_CREATE', 'PRODUCT_UPDATE', 'PRODUCT_DELETE',
    'ORDER_VIEW', 'ORDER_MANAGE',
    'SETTLEMENT_VIEW', 'ANALYTICS_VIEW',
    'MEDIA_MANAGE'  -- vendors can upload product images
)
ON CONFLICT DO NOTHING;

-- CUSTOMER gets order view, wishlist view, review create ability (implicit via role)
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.name = 'CUSTOMER' AND p.name IN ('ORDER_VIEW', 'WISHLIST_VIEW')
ON CONFLICT DO NOTHING;

-- SUPPORT agent gets order view, support manage, review manage
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.name = 'SUPPORT' AND p.name IN ('ORDER_VIEW', 'SUPPORT_MANAGE', 'REVIEW_MANAGE')
ON CONFLICT DO NOTHING;

-- ---------------------------------------------
-- 4. Notification Templates
-- ---------------------------------------------
INSERT INTO notification_templates (id, template_key, name, subject, body, channels, is_active, version, created_at) VALUES
    (gen_random_uuid(), 'ORDER_PLACED', 'Order Placed',
     'Order Confirmation #{{orderNumber}}',
     'Dear {{customerName}}, your order {{orderNumber}} has been placed. Total: PKR {{totalAmount}}.',
     'EMAIL,PUSH,IN_APP', true, 0, now()),

    (gen_random_uuid(), 'ORDER_SHIPPED', 'Order Shipped',
     'Your order #{{orderNumber}} has been shipped',
     'Tracking number: {{trackingNumber}} via {{carrier}}.',
     'EMAIL,SMS,PUSH', true, 0, now()),

    (gen_random_uuid(), 'PAYMENT_CONFIRMED', 'Payment Confirmed',
     'Payment received for order #{{orderId}}',
     'Amount: PKR {{amount}} has been charged.',
     'EMAIL', true, 0, now()),

    (gen_random_uuid(), 'CART_ABANDONED', 'You left items in your cart',
     'Complete your purchase!',
     'Hi {{customerName}}, you left {{itemCount}} items in your cart. Complete your order before they go out of stock.',
     'EMAIL,PUSH', true, 0, now()),

    (gen_random_uuid(), 'VENDOR_APPROVED', 'Vendor Account Approved',
     'Your shop {{shopName}} is now live!',
     'Congratulations! Your vendor account has been approved. You can now start listing products.',
     'EMAIL', true, 0, now()),

    (gen_random_uuid(), 'PASSWORD_RESET', 'Reset your password',
     'Password reset request',
     'Use this link to reset your password: {{resetLink}}',
     'EMAIL', true, 0, now())
ON CONFLICT (template_key) DO NOTHING;

-- ---------------------------------------------
-- 5. Support Ticket Categories
-- ---------------------------------------------
INSERT INTO support_ticket_categories (id, name, description, icon, sort_order, is_active, created_at, created_by) VALUES
    (gen_random_uuid(), 'Order Issue', 'Problems with order delivery, tracking, or status', 'package', 1, true, now(), 'Mughal'),
    (gen_random_uuid(), 'Payment Problem', 'Issues with payment, refunds, or billing', 'credit-card', 2, TRUE, now(), 'Mughal'),
    (gen_random_uuid(), 'Product Quality', 'Damaged, defective, or wrong product received', 'alert-circle', 3, TRUE, now(), 'Mughal'),
    (gen_random_uuid(), 'Return & Refund', 'Request return or track refund status', 'rotate-ccw', 4, TRUE, now(), 'Mughal'),
    (gen_random_uuid(), 'Vendor Question', 'Questions about a specific vendor', 'store', 5, true, now(), 'Mughal'),
    (gen_random_uuid(), 'Account Issue', 'Login, profile, or security concerns', 'user', 6, TRUE, now(), 'Mughal'),
    (gen_random_uuid(), 'Technical Support', 'Website or app technical problems', 'monitor', 7, TRUE, now(), 'Mughal'),
    (gen_random_uuid(), 'General Inquiry', 'Other questions or feedback', 'help-circle', 8, TRUE, now(), 'Mughal')
ON CONFLICT DO NOTHING; -- no unique constraint on name; if already seeded with the same data, they will be duplicated but harmless. Ideally use ON CONFLICT (name) DO NOTHING, but the schema didn't define a unique constraint on name. We'll keep it safe by checking existence via a sub-select in a separate DO block if needed. For now, it's acceptable because the seeder runs only once.

-- ---------------------------------------------
-- 6. Default Commission Rule
-- ---------------------------------------------
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM commission_rules WHERE is_default = TRUE) THEN
        INSERT INTO commission_rules (id, vendor_id, category_id, rate, is_default, version)
        VALUES (gen_random_uuid(), NULL, NULL, 10.00, TRUE, 0);
    END IF;
END $$;

-- ---------------------------------------------
-- 7. Fraud Detection Rules
-- ---------------------------------------------
INSERT INTO fraud_rules (id, rule_name, description, points, is_active, rule_type, version, created_at, created_by) VALUES
    (gen_random_uuid(), 'high_amount', 'Order amount exceeds PKR 50,000', 30, TRUE, 'AMOUNT', 0, now(), 'Mughal'),
    (gen_random_uuid(), 'velocity_check', 'More than 5 orders in one hour from same customer', 25, TRUE, 'VELOCITY', 0, now(), 'Mughal')
ON CONFLICT (rule_name) DO NOTHING;  -- rule_name column is unique

-- ---------------------------------------------
-- 8. Tax Rule (Pakistan – VAT 15% for all categories)
-- ---------------------------------------------
-- Safe insert with NOT EXISTS
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM tax_rules WHERE country_code = 'PAK' AND state_code IS NULL AND category_id IS NULL) THEN
        INSERT INTO tax_rules (id, country_code, state_code, category_id, tax_type, rate, is_active, priority, version, created_at, created_by)
        VALUES (gen_random_uuid(), 'PAK', NULL, NULL, 'VAT', 15.00, TRUE, 1, 0, now(), 'Mughal');
    END IF;
END $$;

-- ---------------------------------------------
-- 9. Delivery Zones (sample cities in Pakistan)
-- ---------------------------------------------
INSERT INTO delivery_zones (id, name, country, region, city, postal_code_pattern, base_rate, rate_per_kg, is_active, version) VALUES
    (gen_random_uuid(), 'Karachi Zone', 'PK', 'Sindh', 'Karachi', '75%', 0, 0, TRUE, 0),
    (gen_random_uuid(), 'Lahore Zone', 'PK', 'Punjab', 'Lahore', '54%', 0, 0, TRUE, 0),
    (gen_random_uuid(), 'Islamabad Zone', 'PK', 'ICT', 'Islamabad', '44%', 0, 0, TRUE, 0),
    (gen_random_uuid(), 'Rawalpindi Zone', 'PK', 'Punjab', 'Rawalpindi', '45%', 0, 0, TRUE, 0),
    (gen_random_uuid(), 'Faisalabad Zone', 'PK', 'Punjab', 'Faisalabad', '37%', 0, 0, TRUE, 0),
    (gen_random_uuid(), 'Peshawar Zone', 'PK', 'KPK', 'Peshawar', '25%', 0, 0, TRUE, 0),
    (gen_random_uuid(), 'Quetta Zone', 'PK', 'Balochistan', 'Quetta', '87%', 0, 0, TRUE, 0)
ON CONFLICT DO NOTHING;  -- assuming no conflict; if duplicates possible, wrap in DO block.

-- ---------------------------------------------
-- 10. Currency Exchange Rates (base PKR → USD, GBP)
-- ---------------------------------------------
INSERT INTO currency_exchange_rates (id, from_currency, to_currency, rate, source, version) VALUES
    (gen_random_uuid(), 'PKR', 'USD', 0.0036, 'manual', 0),
    (gen_random_uuid(), 'PKR', 'GBP', 0.0029, 'manual', 0)
ON CONFLICT DO NOTHING;  -- optional, same precaution.

-- ---------------------------------------------
-- 11. Default Admin User (optional, for initial login)
-- ---------------------------------------------
-- Uncomment the block below and provide a secure password hash (bcrypt) for a real admin account.
 DO $$
 BEGIN
     IF NOT EXISTS (SELECT 1 FROM users WHERE email = 'admin@platform.com') THEN
         INSERT INTO users (id, email, password_hash, first_name, last_name, status, email_verified, version, created_at)
         VALUES (gen_random_uuid(), 'admin@platform.com',
                 '$2a$12$MRHSMVRBQEYFs4qunNBbKeguu0ZRrzPKkgMrvA4NGjtf9itx2wZrW', -- generate with your password encoder
                 'Platform', 'Admin', 'ACTIVE', TRUE, 0, now());
         -- Assign admin role
         INSERT INTO user_roles (user_id, role_id)
         SELECT u.id, r.id FROM users u, roles r WHERE u.email = 'admin@platform.com' AND r.name = 'ADMIN';
     END IF;
 END $$;