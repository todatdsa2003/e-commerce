-- Add change_reason and changed_by columns to product_price_history table

ALTER TABLE product_price_history 
ADD COLUMN change_reason TEXT,
ADD COLUMN changed_by VARCHAR(100);

-- Add comments
COMMENT ON COLUMN product_price_history.change_reason IS 'Reason for the price change';
COMMENT ON COLUMN product_price_history.changed_by IS 'User who made the price change (e.g., ADMIN, SYSTEM)';
