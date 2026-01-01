
-- Add is_deleted column to categories
ALTER TABLE categories
ADD COLUMN is_deleted BOOLEAN DEFAULT FALSE NOT NULL;

-- Add is_deleted column to brands  
ALTER TABLE brands
ADD COLUMN is_deleted BOOLEAN DEFAULT FALSE NOT NULL;

-- Add is_deleted column to products
ALTER TABLE products
ADD COLUMN is_deleted BOOLEAN DEFAULT FALSE NOT NULL;

-- Index
CREATE INDEX idx_categories_is_deleted ON categories(is_deleted);
CREATE INDEX idx_brands_is_deleted ON brands(is_deleted);
CREATE INDEX idx_products_is_deleted ON products(is_deleted);
