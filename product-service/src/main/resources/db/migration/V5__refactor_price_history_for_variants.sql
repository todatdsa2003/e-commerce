-- Add column variant_id
ALTER TABLE product_price_history 
ADD COLUMN variant_id BIGINT;

-- Add foreign key
ALTER TABLE product_price_history
ADD CONSTRAINT fk_price_history_variant 
FOREIGN KEY (variant_id) REFERENCES product_variants(id) ON DELETE CASCADE;

-- check constraint: variant_id can be null
ALTER TABLE product_price_history 
ALTER COLUMN product_id DROP NOT NULL;

-- Ensure either product_id or variant_id is set, but not both
ALTER TABLE product_price_history 
ADD CONSTRAINT check_product_or_variant 
CHECK (
    (product_id IS NOT NULL AND variant_id IS NULL) OR 
    (product_id IS NULL AND variant_id IS NOT NULL)
);

-- Index
CREATE INDEX idx_price_history_variant ON product_price_history(variant_id);

-- Comments
COMMENT ON COLUMN product_price_history.product_id IS 'Product ID (for products without variants)';
COMMENT ON COLUMN product_price_history.variant_id IS 'Variant ID (for products with variants)';
COMMENT ON CONSTRAINT check_product_or_variant ON product_price_history IS 'Ensures that either product_id or variant_id is set, but not both';