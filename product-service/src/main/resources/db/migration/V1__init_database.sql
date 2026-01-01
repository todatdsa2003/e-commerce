-- ============================
-- 1. TABLE: categories
-- ============================
CREATE TABLE categories (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    slug VARCHAR(255) UNIQUE NOT NULL,
    parent_id BIGINT,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()

);



-- ============================
-- 2. TABLE: brands
-- ============================
CREATE TABLE brands (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()

);

-- ============================
-- 3. TABLE: product_status
-- ============================
CREATE TABLE product_status (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(30) UNIQUE NOT NULL,
    label VARCHAR(100) NOT NULL,
    description TEXT,
    display_order INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

-- Insert default status
INSERT INTO product_status (code, label)
VALUES
('ACTIVE', 'Active'),
('INACTIVE', 'Inactive'),
('OUT_OF_STOCK', 'Out of stock'),
('DRAFT', 'Draft');

-- ============================
-- 4. TABLE: products
-- ============================
CREATE TABLE products (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    slug VARCHAR(255) UNIQUE NOT NULL,
    description TEXT,
    price NUMERIC(12,2) NOT NULL,
    availability INT DEFAULT 0,
    status_id BIGINT NOT NULL,
    category_id BIGINT,
    brand_id BIGINT,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

-- Foreign keys
ALTER TABLE products
    ADD CONSTRAINT fk_products_status
        FOREIGN KEY (status_id) REFERENCES product_status(id);

ALTER TABLE products
    ADD CONSTRAINT fk_products_category
        FOREIGN KEY (category_id) REFERENCES categories(id);

ALTER TABLE products
    ADD CONSTRAINT fk_products_brand
        FOREIGN KEY (brand_id) REFERENCES brands(id);

-- ============================
-- 5. TABLE: product_images
-- ============================
CREATE TABLE product_images (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL,
    image_url VARCHAR(500) NOT NULL,
    is_thumbnail BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    CONSTRAINT fk_images_product
        FOREIGN KEY (product_id) REFERENCES products(id)
);

-- ============================
-- 6. TABLE: product_attributes
-- ============================
CREATE TABLE product_attributes (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL,
    attribute_name VARCHAR(100) NOT NULL,
    attribute_value VARCHAR(255) NOT NULL,
    CONSTRAINT fk_attributes_product
        FOREIGN KEY (product_id) REFERENCES products(id),
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

-- ============================
-- 7. TABLE: product_price_history
-- ============================
CREATE TABLE product_price_history (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL,
    old_price NUMERIC(12,2),
    new_price NUMERIC(12,2),
    changed_at TIMESTAMP DEFAULT NOW(),
    CONSTRAINT fk_price_product
        FOREIGN KEY (product_id) REFERENCES products(id)
);

-- ============================
-- Indexes (Performance)
-- ============================
CREATE INDEX idx_products_name ON products(name);
CREATE INDEX idx_products_slug ON products(slug);
CREATE INDEX idx_products_category ON products(category_id);
CREATE INDEX idx_products_brand ON products(brand_id);

CREATE INDEX idx_images_product ON product_images(product_id);
CREATE INDEX idx_attr_product ON product_attributes(product_id);