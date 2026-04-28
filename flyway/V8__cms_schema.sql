-- Banners
CREATE TABLE IF NOT EXISTS cms_banners (
    id UUID PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    subtitle VARCHAR(500),
    image_url VARCHAR(500) NOT NULL,
    mobile_image_url VARCHAR(500),
    link_url VARCHAR(500),
    link_type VARCHAR(50),
    link_value VARCHAR(500),
    position VARCHAR(50) NOT NULL,
    sort_order INT DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE,
    start_date TIMESTAMP WITH TIME ZONE,
    end_date TIMESTAMP WITH TIME ZONE,
    target_audience VARCHAR(50),
    click_count BIGINT DEFAULT 0,
    impression_count BIGINT DEFAULT 0,
    is_deleted BOOLEAN DEFAULT FALSE,
    version BIGINT DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);

-- Pages
CREATE TABLE IF NOT EXISTS cms_pages (
    id UUID PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    slug VARCHAR(255) NOT NULL UNIQUE,
    content TEXT,
    excerpt VARCHAR(500),
    featured_image VARCHAR(500),
    status VARCHAR(20) NOT NULL,
    meta_title VARCHAR(70),
    meta_description VARCHAR(160),
    meta_keywords VARCHAR(255),
    canonical_url VARCHAR(500),
    og_title VARCHAR(255),
    og_description VARCHAR(500),
    og_image VARCHAR(500),
    schema_markup TEXT,
    published_at TIMESTAMP WITH TIME ZONE,
    author_id VARCHAR(255),
    view_count BIGINT DEFAULT 0,
    show_in_footer BOOLEAN DEFAULT FALSE,
    show_in_header BOOLEAN DEFAULT FALSE,
    footer_column INT,
    footer_order INT,
    is_deleted BOOLEAN DEFAULT FALSE,
    version BIGINT DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);

-- Homepage Sections
CREATE TABLE IF NOT EXISTS cms_homepage_sections (
    id UUID PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    section_type VARCHAR(50) NOT NULL,
    section_key VARCHAR(100),
    subtitle VARCHAR(500),
    background_color VARCHAR(20),
    text_color VARCHAR(20),
    sort_order INT NOT NULL,
    is_visible BOOLEAN DEFAULT TRUE,
    max_items INT,
    layout VARCHAR(20),
    configuration TEXT,
    device_visibility VARCHAR(50),
    start_date TIMESTAMP WITH TIME ZONE,
    end_date TIMESTAMP WITH TIME ZONE,
    is_deleted BOOLEAN DEFAULT FALSE,
    version BIGINT DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS cms_homepage_section_items (
    id UUID PRIMARY KEY,
    section_id UUID NOT NULL REFERENCES cms_homepage_sections(id) ON DELETE CASCADE,
    item_type VARCHAR(50) NOT NULL,
    item_id UUID,
    custom_title VARCHAR(255),
    custom_image_url VARCHAR(500),
    custom_link_url VARCHAR(500),
    item_order INT DEFAULT 0,
    version BIGINT DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

-- FAQ
CREATE TABLE IF NOT EXISTS cms_faq_categories (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    slug VARCHAR(255) NOT NULL UNIQUE,
    description TEXT,
    sort_order INT DEFAULT 0,
    is_visible BOOLEAN DEFAULT TRUE,
    is_deleted BOOLEAN DEFAULT FALSE,
    version BIGINT DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS cms_faqs (
    id UUID PRIMARY KEY,
    category_id UUID REFERENCES cms_faq_categories(id),
    question TEXT NOT NULL,
    answer TEXT NOT NULL,
    sort_order INT DEFAULT 0,
    is_visible BOOLEAN DEFAULT TRUE,
    helpful_count INT DEFAULT 0,
    not_helpful_count INT DEFAULT 0,
    is_deleted BOOLEAN DEFAULT FALSE,
    version BIGINT DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);

-- Announcements
CREATE TABLE IF NOT EXISTS cms_announcements (
    id UUID PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    type VARCHAR(50) NOT NULL,
    link_url VARCHAR(500),
    link_text VARCHAR(255),
    icon VARCHAR(100),
    is_active BOOLEAN DEFAULT TRUE,
    is_dismissible BOOLEAN DEFAULT TRUE,
    start_date TIMESTAMP WITH TIME ZONE,
    end_date TIMESTAMP WITH TIME ZONE,
    target_pages VARCHAR(500),
    priority INT DEFAULT 0,
    is_deleted BOOLEAN DEFAULT FALSE,
    version BIGINT DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);

-- Menu Items
CREATE TABLE IF NOT EXISTS cms_menu_items (
    id UUID PRIMARY KEY,
    label VARCHAR(255) NOT NULL,
    url VARCHAR(500),
    link_type VARCHAR(50),
    link_value VARCHAR(500),
    icon VARCHAR(100),
    location VARCHAR(50) NOT NULL,
    parent_id UUID REFERENCES cms_menu_items(id),
    sort_order INT DEFAULT 0,
    is_visible BOOLEAN DEFAULT TRUE,
    open_in_new_tab BOOLEAN DEFAULT FALSE,
    requires_login BOOLEAN DEFAULT FALSE,
    roles_allowed VARCHAR(500),
    is_deleted BOOLEAN DEFAULT FALSE,
    version BIGINT DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);

-- Content Blocks
CREATE TABLE IF NOT EXISTS cms_content_blocks (
    id UUID PRIMARY KEY,
    block_key VARCHAR(100) NOT NULL UNIQUE,
    title VARCHAR(255),
    content TEXT,
    content_type VARCHAR(20) DEFAULT 'HTML',
    is_active BOOLEAN DEFAULT TRUE,
    cache_ttl_minutes INT,
    is_deleted BOOLEAN DEFAULT FALSE,
    version BIGINT DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);

-- Seed default content blocks
INSERT INTO cms_content_blocks (id, block_key, title, content, content_type, is_active) VALUES
    (gen_random_uuid(), 'footer_copyright', 'Footer Copyright', '© 2024 E-Commerce Platform. All rights reserved.', 'HTML', TRUE),
    (gen_random_uuid(), 'footer_about', 'About Us Text', 'We are the leading e-commerce platform...', 'HTML', TRUE),
    (gen_random_uuid(), 'homepage_hero_title', 'Homepage Hero Title', 'Welcome to Our Store', 'HTML', TRUE),
    (gen_random_uuid(), 'homepage_hero_subtitle', 'Homepage Hero Subtitle', 'Best deals on thousands of products', 'HTML', TRUE)
ON CONFLICT (block_key) DO NOTHING;