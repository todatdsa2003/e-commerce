
-- 1. TABLE: categories
CREATE TABLE categories (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    slug VARCHAR(255) UNIQUE NOT NULL,
    parent_id BIGINT,
    is_deleted BOOLEAN DEFAULT FALSE NOT NULL,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

-- 2. TABLE: brands

CREATE TABLE brands (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    is_deleted BOOLEAN DEFAULT FALSE NOT NULL,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);


-- 3. TABLE: product_status

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


-- 4. TABLE: products

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
    is_deleted BOOLEAN DEFAULT FALSE NOT NULL,
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


-- 5. TABLE: product_images

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


-- 6. TABLE: product_attributes

CREATE TABLE product_attributes (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL,
    attribute_name VARCHAR(100) NOT NULL,
    attribute_value VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    CONSTRAINT fk_attributes_product
        FOREIGN KEY (product_id) REFERENCES products(id)
);


-- 7. TABLE: product_variant_options

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

COMMENT ON TABLE product_variant_options IS 
    'Định nghĩa các tùy chọn variant cho product (Size, Color, RAM, Storage...)';

COMMENT ON COLUMN product_variant_options.option_name IS 
    'Tên tùy chọn: Size, Color, RAM, Storage...';

COMMENT ON COLUMN product_variant_options.option_values IS 
    'Mảng giá trị có thể chọn. Ví dụ: [S, M, L, XL] hoặc [8GB, 16GB, 32GB]';

COMMENT ON COLUMN product_variant_options.display_order IS 
    'Thứ tự hiển thị option (option nào hiển thị trước)';


-- 8. TABLE: product_variants

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

COMMENT ON TABLE product_variants IS 
    'Lưu trữ từng SKU variant cụ thể với giá và stock riêng biệt';

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


-- 9. TABLE: product_price_history

CREATE TABLE product_price_history (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT,
    variant_id BIGINT,
    old_price NUMERIC(12,2),
    new_price NUMERIC(12,2),
    change_reason TEXT,
    changed_by VARCHAR(100),
    changed_at TIMESTAMP DEFAULT NOW(),
    
    -- Foreign keys
    CONSTRAINT fk_price_product
        FOREIGN KEY (product_id) REFERENCES products(id),
    
    CONSTRAINT fk_price_history_variant 
        FOREIGN KEY (variant_id) REFERENCES product_variants(id) ON DELETE CASCADE,
    
    -- Ensure either product_id or variant_id is set, but not both
    CONSTRAINT check_product_or_variant 
        CHECK (
            (product_id IS NOT NULL AND variant_id IS NULL) OR 
            (product_id IS NULL AND variant_id IS NOT NULL)
        )
);

COMMENT ON COLUMN product_price_history.product_id IS 
    'Product ID (for products without variants)';

COMMENT ON COLUMN product_price_history.variant_id IS 
    'Variant ID (for products with variants)';

COMMENT ON COLUMN product_price_history.change_reason IS 
    'Reason for the price change';

COMMENT ON COLUMN product_price_history.changed_by IS 
    'User who made the price change (e.g., ADMIN, SYSTEM)';

COMMENT ON CONSTRAINT check_product_or_variant ON product_price_history IS 
    'Ensures that either product_id or variant_id is set, but not both';


-- INDEXES - Performance Optimization


-- Categories indexes
CREATE INDEX idx_categories_is_deleted ON categories(is_deleted);
CREATE INDEX idx_categories_parent ON categories(parent_id);

-- Brands indexes
CREATE INDEX idx_brands_is_deleted ON brands(is_deleted);

-- Products indexes
CREATE INDEX idx_products_name ON products(name);
CREATE INDEX idx_products_slug ON products(slug);
CREATE INDEX idx_products_category ON products(category_id);
CREATE INDEX idx_products_brand ON products(brand_id);
CREATE INDEX idx_products_is_deleted ON products(is_deleted);
CREATE INDEX idx_products_status ON products(status_id);

-- Product images indexes
CREATE INDEX idx_images_product ON product_images(product_id);

-- Product attributes indexes
CREATE INDEX idx_attr_product ON product_attributes(product_id);

-- Product variant options indexes
CREATE INDEX idx_variant_options_product ON product_variant_options(product_id);

-- Product variants indexes
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

-- JSONB index for option_values (GIN index)
CREATE INDEX idx_variants_option_values 
    ON product_variants USING GIN(option_values);

-- Price history indexes
CREATE INDEX idx_price_history_product ON product_price_history(product_id);
CREATE INDEX idx_price_history_variant ON product_price_history(variant_id);
CREATE INDEX idx_price_history_changed_at ON product_price_history(changed_at);