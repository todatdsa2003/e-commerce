

-- product_variant_options


CREATE TABLE product_variant_options (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL,
    option_name VARCHAR(50) NOT NULL,
    option_values TEXT[] NOT NULL,
    display_order INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    
    -- Foreign key
    CONSTRAINT fk_variant_option_product 
        FOREIGN KEY (product_id) 
        REFERENCES products(id) 
        ON DELETE CASCADE,
    
    -- Unique constraint: 1 product không có 2 options cùng tên
    CONSTRAINT unique_product_option 
        UNIQUE(product_id, option_name),
    
    -- Check constraints
    CONSTRAINT check_option_name_not_empty 
        CHECK (LENGTH(TRIM(option_name)) > 0),
    
    CONSTRAINT check_option_values_not_empty 
        CHECK (array_length(option_values, 1) > 0)
);


-- product_variants

CREATE TABLE product_variants (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL,
    sku VARCHAR(100) NOT NULL,
    variant_name VARCHAR(200),
    
    -- Pricing
    price DECIMAL(12,2) NOT NULL,
    compare_at_price DECIMAL(12,2),
    
    -- Inventory
    stock_quantity INT NOT NULL DEFAULT 0,
    low_stock_threshold INT DEFAULT 5,
    option_values JSONB NOT NULL,
    
    -- Status flags
    is_default BOOLEAN DEFAULT FALSE,
    is_active BOOLEAN DEFAULT TRUE,
    display_order INT DEFAULT 0,
    
    -- Timestamps
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    deleted_at TIMESTAMP,
    
    -- Foreign key
    CONSTRAINT fk_variant_product 
        FOREIGN KEY (product_id) 
        REFERENCES products(id) 
        ON DELETE CASCADE,
    CONSTRAINT unique_variant_sku 
        UNIQUE(sku),
    
    -- Check constraints
    CONSTRAINT check_price_positive 
        CHECK (price > 0),
    
    CONSTRAINT check_stock_non_negative 
        CHECK (stock_quantity >= 0),
    
    CONSTRAINT check_compare_price_positive 
        CHECK (compare_at_price IS NULL OR compare_at_price > 0),
    
    CONSTRAINT check_low_stock_threshold_non_negative
        CHECK (low_stock_threshold IS NULL OR low_stock_threshold >= 0)
);


-- Indexes for Performance Optimization

-- Index cho product_variant_options
CREATE INDEX idx_variant_options_product 
    ON product_variant_options(product_id);

-- Indexes cho product_variants
CREATE INDEX idx_variants_product 
    ON product_variants(product_id) 
    WHERE deleted_at IS NULL;

CREATE INDEX idx_variants_sku 
    ON product_variants(sku) 
    WHERE deleted_at IS NULL;

CREATE INDEX idx_variants_active 
    ON product_variants(product_id, is_active) 
    WHERE deleted_at IS NULL;

CREATE INDEX idx_variants_default 
    ON product_variants(product_id, is_default) 
    WHERE deleted_at IS NULL AND is_default = TRUE;

-- JSONB index cho option_values (GIN index)
CREATE INDEX idx_variants_option_values 
    ON product_variants USING GIN(option_values);


-- Table Comments


COMMENT ON TABLE product_variant_options IS 
    'Định nghĩa các tùy chọn variant cho product (Size, Color, RAM, Storage...)';

COMMENT ON TABLE product_variants IS 
    'Lưu trữ từng SKU variant cụ thể với giá và stock riêng biệt';


-- Column Comments


-- product_variant_options
COMMENT ON COLUMN product_variant_options.option_name IS 
    'Tên tùy chọn: Size, Color, RAM, Storage...';

COMMENT ON COLUMN product_variant_options.option_values IS 
    'Mảng giá trị có thể chọn. Ví dụ: [S, M, L, XL] hoặc [8GB, 16GB, 32GB]';

COMMENT ON COLUMN product_variant_options.display_order IS 
    'Thứ tự hiển thị option (option nào hiển thị trước)';

-- product_variants
COMMENT ON COLUMN product_variants.sku IS 
    'Mã SKU duy nhất cho variant';

COMMENT ON COLUMN product_variants.variant_name IS 
    'Tên mô tả variant (auto-generate từ option values). Ví dụ: "Size M - Color Đỏ"';

COMMENT ON COLUMN product_variants.price IS 
    'Giá bán của variant này';

COMMENT ON COLUMN product_variants.compare_at_price IS 
    'Giá gốc (để tính % discount)';

COMMENT ON COLUMN product_variants.stock_quantity IS 
    'Số lượng tồn kho của variant';

COMMENT ON COLUMN product_variants.low_stock_threshold IS 
    'Ngưỡng cảnh báo hết hàng';

COMMENT ON COLUMN product_variants.option_values IS 
    'JSON lưu tổ hợp option values. Ví dụ: {"Size": "M", "Color": "Đỏ", "RAM": "16GB"}';

COMMENT ON COLUMN product_variants.is_default IS 
    'Đánh dấu variant mặc định (hiển thị đầu tiên khi user mở product page)';

COMMENT ON COLUMN product_variants.is_active IS 
    'Variant có đang active không (có thể tạm ngưng bán variant mà không xóa)';

COMMENT ON COLUMN product_variants.deleted_at IS 
    'Soft delete timestamp (null = chưa xóa)';
