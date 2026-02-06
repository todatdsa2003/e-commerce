-- ========================================
-- V3: PERFORMANCE INDEXES
-- ========================================
-- Purpose: Add composite and partial indexes to optimize common query patterns
-- Impact: Prevent full table scans, 10x-100x faster queries
-- Target: Products table (main bottleneck)
-- ========================================


-- STEP 1: DROP OLD BASIC INDEXES (will be replaced by better ones)
DROP INDEX IF EXISTS idx_products_category;
DROP INDEX IF EXISTS idx_products_brand;
DROP INDEX IF EXISTS idx_products_status;
DROP INDEX IF EXISTS idx_products_is_deleted;


-- STEP 2: PARTIAL INDEXES (Index only active products)
CREATE INDEX idx_products_category_active
    ON products(category_id)
    WHERE is_deleted = false;

CREATE INDEX idx_products_brand_active
    ON products(brand_id)
    WHERE is_deleted = false;

CREATE INDEX idx_products_status_active
    ON products(status_id)
    WHERE is_deleted = false;


-- STEP 3: COMPOSITE INDEXES (Multi-column filtering)
CREATE INDEX idx_products_category_brand_status
    ON products(category_id, brand_id, status_id, is_deleted)
    WHERE is_deleted = false;


CREATE INDEX idx_products_status_category
    ON products(status_id, category_id)
    WHERE is_deleted = false;


-- STEP 4: PRICE RANGE INDEXES (for filtering/sorting)
CREATE INDEX idx_products_price_active
    ON products(price)
    WHERE is_deleted = false;

-- Composite: category + price (for price sorting within category)
CREATE INDEX idx_products_category_price
    ON products(category_id, price)
    WHERE is_deleted = false;


-- STEP 5: TIMESTAMP INDEXES (for sorting and filtering)
CREATE INDEX idx_products_created_at_desc
    ON products(created_at DESC)
    WHERE is_deleted = false;

-- Updated at (for finding recently modified products)
CREATE INDEX idx_products_updated_at_desc
    ON products(updated_at DESC)
    WHERE is_deleted = false;


-- STEP 6: FULL-TEXT SEARCH INDEX (for name search)
CREATE INDEX idx_products_name_pattern
    ON products(name text_pattern_ops)
    WHERE is_deleted = false;

-- Slug index (already unique, but add partial for faster lookups)
CREATE INDEX idx_products_slug_active
    ON products(slug)
    WHERE is_deleted = false;


-- STEP 7: AVAILABILITY INDEX (for stock filtering)
CREATE INDEX idx_products_availability_active
    ON products(availability)
    WHERE is_deleted = false AND availability > 0;


ANALYZE products;
