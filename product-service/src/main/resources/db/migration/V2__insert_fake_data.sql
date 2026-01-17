-- ============================
-- 1. CATEGORIES (with soft delete support)
-- ============================
INSERT INTO categories (name, slug, parent_id, is_deleted) VALUES
-- Main Categories
('Laptop', 'laptop', NULL, FALSE),
('Gaming Laptop', 'gaming-laptop', 1, FALSE),
('Ultrabook', 'ultrabook', 1, FALSE),
('Business Laptop', 'business-laptop', 1, FALSE),
('Smartphone', 'smartphone', NULL, FALSE),
('Android Phones', 'android-phones', 5, FALSE),
('iPhone', 'iphone', 5, FALSE),
('Tablets', 'tablets', NULL, FALSE),
('iPad', 'ipad', 8, FALSE),
('Android Tablets', 'android-tablets', 8, FALSE),
('Smartwatch', 'smartwatch', NULL, FALSE),
('Fitness Band', 'fitness-band', 11, FALSE),
('Headphones', 'headphones', NULL, FALSE),
('Wireless Earbuds', 'wireless-earbuds', 13, FALSE),
('Over-ear Headphones', 'over-ear-headphones', 13, FALSE),
('Monitors', 'monitors', NULL, FALSE),
('4K Monitors', '4k-monitors', 16, FALSE),
('Gaming Monitors', 'gaming-monitors', 16, FALSE),
('Keyboards', 'keyboards', NULL, FALSE),
('Mechanical Keyboards', 'mechanical-keyboards', 19, FALSE),
('Mice', 'mice', NULL, FALSE),
('Gaming Mice', 'gaming-mice', 21, FALSE),
('Printers', 'printers', NULL, FALSE),
('Inkjet Printers', 'inkjet-printers', 23, FALSE),
('Laser Printers', 'laser-printers', 23, FALSE),
('PC Components', 'pc-components', NULL, FALSE),
('Graphics Cards', 'graphics-cards', 26, FALSE),
('Motherboards', 'motherboards', 26, FALSE),
('Power Supplies', 'power-supplies', 26, FALSE),
('Storage Devices', 'storage-devices', NULL, FALSE),
('SSD', 'ssd', 30, FALSE),
('HDD', 'hdd', 30, FALSE),
('Networking', 'networking', NULL, FALSE),
('Routers', 'routers', 33, FALSE),
('Mesh WiFi', 'mesh-wifi', 33, FALSE),
('Smart Home', 'smart-home', NULL, FALSE),
('Security Cameras', 'security-cameras', 36, FALSE),
('Smart Lighting', 'smart-lighting', 36, FALSE),
('TV', 'tv', NULL, FALSE),
('Smart TV', 'smart-tv', 39, FALSE),
('4K TV', '4k-tv', 39, FALSE),
('Speakers', 'speakers', NULL, FALSE),
('Bluetooth Speakers', 'bluetooth-speakers', 42, FALSE),
('Home Theater', 'home-theater', 42, FALSE),
('Consoles', 'consoles', NULL, FALSE),
('PlayStation', 'playstation', 45, FALSE),
('Xbox', 'xbox', 45, FALSE),
('Nintendo', 'nintendo', 45, FALSE),
('Drones', 'drones', NULL, FALSE),
('Action Cameras', 'action-cameras', NULL, FALSE);

-- ============================
-- 2. BRANDS (with soft delete support)
-- ============================
INSERT INTO brands (name, is_deleted) VALUES
('Apple', FALSE),
('Samsung', FALSE),
('Dell', FALSE),
('HP', FALSE),
('Lenovo', FALSE),
('Asus', FALSE),
('Acer', FALSE),
('MSI', FALSE),
('Razer', FALSE),
('LG', FALSE),
('Sony', FALSE),
('Huawei', FALSE),
('Xiaomi', FALSE),
('Oppo', FALSE),
('Vivo', FALSE),
('Nokia', FALSE),
('Realme', FALSE),
('Google', FALSE),
('Microsoft', FALSE),
('Logitech', FALSE),
('Corsair', FALSE),
('SteelSeries', FALSE),
('Kingston', FALSE),
('Sandisk', FALSE),
('Seagate', FALSE),
('Western Digital', FALSE),
('Gigabyte', FALSE),
('ASRock', FALSE),
('TP-Link', FALSE),
('Netgear', FALSE),
('JBL', FALSE),
('Bose', FALSE),
('Beats', FALSE),
('Marshall', FALSE),
('HyperX', FALSE),
('Canon', FALSE),
('Nikon', FALSE),
('GoPro', FALSE),
('DJI', FALSE),
('Philips', FALSE),
('Panasonic', FALSE),
('Toshiba', FALSE),
('BenQ', FALSE),
('ViewSonic', FALSE),
('Alienware', FALSE),
('Redmi', FALSE),
('OnePlus', FALSE),
('Nothing', FALSE),
('Infinix', FALSE);

-- ============================
-- 3. PRODUCTS (with soft delete support)
-- ============================
INSERT INTO products (name, slug, description, price, availability, status_id, category_id, brand_id, is_deleted)
VALUES
-- Laptops & Computers (Products với variants)
('MacBook Air M2 2024', 'macbook-air-m2-2024', 'Powerful laptop with M2 chip, stunning Retina display', 1299.00, 0, 1, 1, 1, FALSE),
('MacBook Pro 16 M3', 'macbook-pro-16-m3', 'High-end Macbook for professionals with M3 Pro/Max chip', 2799.00, 0, 1, 1, 1, FALSE),
('iPhone 15 Pro Max', 'iphone-15-pro-max', 'Flagship iPhone with titanium frame and A17 Pro chip', 1199.00, 0, 1, 7, 1, FALSE),
('Samsung Galaxy S24 Ultra', 'galaxy-s24-ultra', 'Samsung flagship phone with Galaxy AI and S Pen', 1099.00, 0, 1, 6, 2, FALSE),
('Dell XPS 13 Plus', 'dell-xps-13-plus', 'Ultrabook premium with InfinityEdge display', 1499.00, 0, 1, 3, 3, FALSE),

-- More products with variants
('HP Spectre x360', 'hp-spectre-x360', '2-in-1 premium ultrabook with 360-degree hinge', 1399.00, 0, 1, 3, 4, FALSE),
('Lenovo ThinkPad X1 Carbon', 'thinkpad-x1-carbon', 'Business class laptop with military-grade durability', 1599.00, 0, 1, 4, 5, FALSE),
('Asus ROG Strix G17', 'asus-rog-strix-g17', 'Gaming laptop with RTX 4070 GPU and RGB lighting', 1899.00, 0, 1, 2, 6, FALSE),
('iPad Pro 12.9 M2', 'ipad-pro-12-9-m2', 'Professional tablet with M2 chip and Liquid Retina XDR', 1099.00, 0, 1, 9, 1, FALSE),
('Samsung Galaxy Tab S9 Ultra', 'galaxy-tab-s9-ultra', 'Premium Android tablet with AMOLED display', 999.00, 0, 1, 10, 2, FALSE),

-- Products WITHOUT variants (simple products)
('Acer Predator Helios', 'predator-helios', 'Gaming laptop powerful with RTX graphics', 1699.00, 33, 1, 2, 7, FALSE),
('MSI Stealth 15', 'msi-stealth-15', 'Slim gaming laptop for professionals', 1799.00, 20, 1, 2, 8, FALSE),
('Razer Blade 15', 'razer-blade-15', 'Premium gaming laptop with QHD 240Hz display', 2499.00, 12, 1, 2, 9, FALSE),
('LG UltraFine 4K Monitor', 'lg-ultrafine-4k', 'High resolution 4K monitor with Thunderbolt 3', 699.00, 80, 1, 16, 10, FALSE),
('Sony WH-1000XM5', 'sony-wh-1000xm5', 'Industry-leading noise-cancelling headphones', 399.00, 140, 1, 15, 11, FALSE),

('Xiaomi 14 Ultra', 'xiaomi-14-ultra', 'Flagship camera phone with Leica optics', 899.00, 110, 1, 6, 13, FALSE),
('Oppo Find X7', 'oppo-find-x7', 'High-end smartphone with Hasselblad camera', 799.00, 120, 1, 6, 14, FALSE),
('Vivo X100 Pro', 'vivo-x100-pro', 'Camera phone with ZEISS optics and AI features', 899.00, 115, 1, 6, 15, FALSE),
('Google Pixel 8 Pro', 'google-pixel-8-pro', 'Best AI smartphone with Tensor G3 chip', 999.00, 100, 1, 6, 18, FALSE),
('Microsoft Surface Laptop 6', 'surface-laptop-6', 'Premium laptop from Microsoft with Alcantara keyboard', 1599.00, 40, 1, 3, 19, FALSE),

('Logitech MX Master 3S', 'mx-master-3s', 'Top productivity mouse with MagSpeed scroll wheel', 119.00, 300, 1, 21, 20, FALSE),
('Corsair K95 RGB', 'corsair-k95-rgb', 'Mechanical keyboard with Cherry MX switches', 199.00, 160, 1, 20, 21, FALSE),
('SteelSeries Apex Pro', 'steelseries-apex-pro', 'High-end mechanical keyboard with adjustable switches', 229.00, 120, 1, 20, 22, FALSE),
('Kingston NV2 1TB', 'kingston-nv2-1tb', 'High-speed NVMe SSD with PCIe 4.0', 59.00, 500, 1, 31, 23, FALSE),
('Sandisk Extreme Portable', 'sandisk-extreme-portable', 'Portable SSD with ruggedized design', 99.00, 350, 1, 31, 24, FALSE),

('Seagate Barracuda 2TB', 'seagate-2tb', 'Reliable hard drive for desktop PCs', 49.00, 400, 1, 32, 25, FALSE),
('WD Blue 1TB', 'wd-blue-1tb', 'Standard HDD for everyday computing', 45.00, 300, 1, 32, 26, FALSE),
('Gigabyte RTX 4070', 'gigabyte-rtx-4070', 'High-end graphics card with DLSS 3.0', 599.00, 50, 1, 27, 27, FALSE),
('ASRock B550M Steel Legend', 'asrock-b550m', 'Motherboard AM4 with PCIe 4.0 support', 129.00, 100, 1, 28, 28, FALSE),
('TP-Link Archer AX73', 'archer-ax73', 'WiFi 6 router with MU-MIMO technology', 129.00, 200, 1, 34, 29, FALSE),

('Netgear Orbi RBK50', 'orbi-rbk50', 'Mesh WiFi system covering up to 5000 sq ft', 399.00, 60, 1, 35, 30, FALSE),
('JBL Charge 5', 'jbl-charge-5', 'Portable powerful speaker with IP67 waterproof', 149.00, 150, 1, 43, 31, FALSE),
('Bose SoundLink Flex', 'bose-flex', 'Premium Bluetooth speaker with PositionIQ', 129.00, 120, 1, 43, 32, FALSE),
('Beats Studio Pro', 'beats-studio-pro', 'High-end headphones with lossless audio', 349.00, 90, 1, 15, 33, FALSE),
('Marshall Acton III', 'marshall-acton-3', 'Classic speaker design with Bluetooth 5.2', 249.00, 75, 1, 42, 34, FALSE),

('HyperX Cloud III', 'hyperx-cloud-3', 'Gaming headset with DTS Headphone:X', 129.00, 180, 1, 15, 35, FALSE),
('Canon EOS R8', 'canon-r8', 'Mirrorless camera with 24.2MP full-frame sensor', 1499.00, 30, 1, 50, 36, FALSE),
('Nikon Z6 II', 'nikon-z6-ii', 'Full frame mirrorless with dual processors', 1599.00, 25, 1, 50, 37, FALSE),
('GoPro Hero 12', 'gopro-hero-12', 'Best action camera with HyperSmooth 6.0', 499.00, 70, 1, 50, 38, FALSE),
('DJI Mini 4 Pro', 'dji-mini-4-pro', 'Professional drone under 249g with 4K/60fps', 999.00, 40, 1, 49, 39, FALSE),

('Philips Hue Lightstrip', 'philips-hue-lightstrip', 'Smart LED strip with 16 million colors', 59.00, 300, 1, 38, 40, FALSE),
('Panasonic 4K TV', 'panasonic-4k-tv', 'Affordable smart TV with HDR support', 499.00, 80, 1, 40, 41, FALSE),
('Toshiba Smart TV 55"', 'toshiba-55-tv', '55-inch smart TV with Fire TV built-in', 399.00, 70, 1, 40, 42, FALSE),
('BenQ GW2780', 'benq-gw2780', '27-inch IPS monitor with Eye-Care technology', 179.00, 100, 1, 16, 43, FALSE),
('ViewSonic VX2758', 'viewsonic-vx2758', '144Hz gaming monitor with FreeSync', 229.00, 90, 1, 18, 44, FALSE),

('Alienware AW3423DW', 'alienware-aw3423dw', 'OLED gaming monitor with 175Hz refresh rate', 1299.00, 20, 1, 18, 45, FALSE);


-- ============================
-- 4. PRODUCT IMAGES
-- ============================
INSERT INTO product_images (product_id, image_url, is_thumbnail)
SELECT id, 'https://placehold.co/800x600/png?text=Product+' || id, TRUE
FROM products
WHERE is_deleted = FALSE;

-- Add additional images for some products
INSERT INTO product_images (product_id, image_url, is_thumbnail)
VALUES
(1, 'https://placehold.co/800x600/png?text=MacBook+Air+Side', FALSE),
(1, 'https://placehold.co/800x600/png?text=MacBook+Air+Open', FALSE),
(3, 'https://placehold.co/800x600/png?text=iPhone+15+Back', FALSE),
(3, 'https://placehold.co/800x600/png?text=iPhone+15+Camera', FALSE),
(4, 'https://placehold.co/800x600/png?text=Galaxy+S24+Back', FALSE);


-- ============================
-- 5. PRODUCT ATTRIBUTES
-- ============================
INSERT INTO product_attributes (product_id, attribute_name, attribute_value)
SELECT id, 'Warranty', '12 months' FROM products WHERE is_deleted = FALSE;

INSERT INTO product_attributes (product_id, attribute_name, attribute_value)
SELECT id, 'Origin', 'Authentic Import' FROM products WHERE is_deleted = FALSE;

-- Add specific attributes for laptops
INSERT INTO product_attributes (product_id, attribute_name, attribute_value)
SELECT id, 'Screen Type', 'Retina Display' 
FROM products 
WHERE category_id IN (1, 2, 3, 4) AND brand_id = 1 AND is_deleted = FALSE;

INSERT INTO product_attributes (product_id, attribute_name, attribute_value)
SELECT id, 'Battery Life', 'Up to 18 hours' 
FROM products 
WHERE category_id IN (1, 3) AND is_deleted = FALSE
LIMIT 5;


-- ============================
-- 6. PRODUCT VARIANT OPTIONS
-- ============================
-- MacBook Air M2 (product_id = 1): Storage & Color
INSERT INTO product_variant_options (product_id, option_name, option_values, display_order)
VALUES
(1, 'Storage', ARRAY['256GB', '512GB', '1TB'], 1),
(1, 'Color', ARRAY['Midnight', 'Starlight', 'Space Gray', 'Silver'], 2);

-- MacBook Pro 16 M3 (product_id = 2): Chip & Storage & Color
INSERT INTO product_variant_options (product_id, option_name, option_values, display_order)
VALUES
(2, 'Chip', ARRAY['M3 Pro', 'M3 Max'], 1),
(2, 'Storage', ARRAY['512GB', '1TB', '2TB'], 2),
(2, 'Color', ARRAY['Space Black', 'Silver'], 3);

-- iPhone 15 Pro Max (product_id = 3): Storage & Color
INSERT INTO product_variant_options (product_id, option_name, option_values, display_order)
VALUES
(3, 'Storage', ARRAY['256GB', '512GB', '1TB'], 1),
(3, 'Color', ARRAY['Natural Titanium', 'Blue Titanium', 'White Titanium', 'Black Titanium'], 2);

-- Samsung Galaxy S24 Ultra (product_id = 4): Storage & Color
INSERT INTO product_variant_options (product_id, option_name, option_values, display_order)
VALUES
(4, 'Storage', ARRAY['256GB', '512GB', '1TB'], 1),
(4, 'Color', ARRAY['Titanium Gray', 'Titanium Black', 'Titanium Violet', 'Titanium Yellow'], 2);

-- Dell XPS 13 Plus (product_id = 5): RAM & Storage
INSERT INTO product_variant_options (product_id, option_name, option_values, display_order)
VALUES
(5, 'RAM', ARRAY['16GB', '32GB'], 1),
(5, 'Storage', ARRAY['512GB', '1TB', '2TB'], 2);

-- HP Spectre x360 (product_id = 6): RAM & Storage
INSERT INTO product_variant_options (product_id, option_name, option_values, display_order)
VALUES
(6, 'RAM', ARRAY['16GB', '32GB'], 1),
(6, 'Storage', ARRAY['512GB', '1TB'], 2);

-- Lenovo ThinkPad X1 Carbon (product_id = 7): RAM & Storage & Display
INSERT INTO product_variant_options (product_id, option_name, option_values, display_order)
VALUES
(7, 'RAM', ARRAY['16GB', '32GB'], 1),
(7, 'Storage', ARRAY['512GB', '1TB'], 2),
(7, 'Display', ARRAY['FHD', 'WQUXGA'], 3);

-- Asus ROG Strix G17 (product_id = 8): RAM & Storage & GPU
INSERT INTO product_variant_options (product_id, option_name, option_values, display_order)
VALUES
(8, 'RAM', ARRAY['16GB', '32GB'], 1),
(8, 'Storage', ARRAY['512GB', '1TB'], 2),
(8, 'GPU', ARRAY['RTX 4060', 'RTX 4070'], 3);

-- iPad Pro 12.9 M2 (product_id = 9): Storage & Connectivity & Color
INSERT INTO product_variant_options (product_id, option_name, option_values, display_order)
VALUES
(9, 'Storage', ARRAY['128GB', '256GB', '512GB', '1TB', '2TB'], 1),
(9, 'Connectivity', ARRAY['Wi-Fi', 'Wi-Fi + Cellular'], 2),
(9, 'Color', ARRAY['Space Gray', 'Silver'], 3);

-- Samsung Galaxy Tab S9 Ultra (product_id = 10): Storage & Color
INSERT INTO product_variant_options (product_id, option_name, option_values, display_order)
VALUES
(10, 'Storage', ARRAY['256GB', '512GB', '1TB'], 1),
(10, 'Color', ARRAY['Graphite', 'Beige'], 2);


-- ============================
-- 7. PRODUCT VARIANTS
-- ============================

-- MacBook Air M2 (256GB variants)
INSERT INTO product_variants (product_id, sku, variant_name, price, compare_at_price, stock_quantity, option_values, is_default, is_active, display_order)
VALUES
(1, 'MBA-M2-256-MIDNIGHT', '256GB - Midnight', 1299.00, NULL, 25, '{"Storage": "256GB", "Color": "Midnight"}', TRUE, TRUE, 1),
(1, 'MBA-M2-256-STARLIGHT', '256GB - Starlight', 1299.00, NULL, 30, '{"Storage": "256GB", "Color": "Starlight"}', FALSE, TRUE, 2),
(1, 'MBA-M2-256-SPACEGRAY', '256GB - Space Gray', 1299.00, NULL, 20, '{"Storage": "256GB", "Color": "Space Gray"}', FALSE, TRUE, 3),
(1, 'MBA-M2-256-SILVER', '256GB - Silver', 1299.00, NULL, 25, '{"Storage": "256GB", "Color": "Silver"}', FALSE, TRUE, 4),
-- 512GB variants
(1, 'MBA-M2-512-MIDNIGHT', '512GB - Midnight', 1499.00, NULL, 15, '{"Storage": "512GB", "Color": "Midnight"}', FALSE, TRUE, 5),
(1, 'MBA-M2-512-STARLIGHT', '512GB - Starlight', 1499.00, NULL, 18, '{"Storage": "512GB", "Color": "Starlight"}', FALSE, TRUE, 6),
(1, 'MBA-M2-512-SPACEGRAY', '512GB - Space Gray', 1499.00, NULL, 12, '{"Storage": "512GB", "Color": "Space Gray"}', FALSE, TRUE, 7),
(1, 'MBA-M2-512-SILVER', '512GB - Silver', 1499.00, NULL, 15, '{"Storage": "512GB", "Color": "Silver"}', FALSE, TRUE, 8);

-- iPhone 15 Pro Max (256GB variants)
INSERT INTO product_variants (product_id, sku, variant_name, price, compare_at_price, stock_quantity, option_values, is_default, is_active, display_order)
VALUES
(3, 'IP15PM-256-NATURAL', '256GB - Natural Titanium', 1199.00, NULL, 50, '{"Storage": "256GB", "Color": "Natural Titanium"}', TRUE, TRUE, 1),
(3, 'IP15PM-256-BLUE', '256GB - Blue Titanium', 1199.00, 1299.00, 45, '{"Storage": "256GB", "Color": "Blue Titanium"}', FALSE, TRUE, 2),
(3, 'IP15PM-256-WHITE', '256GB - White Titanium', 1199.00, NULL, 40, '{"Storage": "256GB", "Color": "White Titanium"}', FALSE, TRUE, 3),
(3, 'IP15PM-256-BLACK', '256GB - Black Titanium', 1199.00, NULL, 55, '{"Storage": "256GB", "Color": "Black Titanium"}', FALSE, TRUE, 4),
-- 512GB variants
(3, 'IP15PM-512-NATURAL', '512GB - Natural Titanium', 1399.00, NULL, 30, '{"Storage": "512GB", "Color": "Natural Titanium"}', FALSE, TRUE, 5),
(3, 'IP15PM-512-BLUE', '512GB - Blue Titanium', 1399.00, NULL, 25, '{"Storage": "512GB", "Color": "Blue Titanium"}', FALSE, TRUE, 6),
(3, 'IP15PM-512-BLACK', '512GB - Black Titanium', 1399.00, NULL, 35, '{"Storage": "512GB", "Color": "Black Titanium"}', FALSE, TRUE, 7);

-- Samsung Galaxy S24 Ultra
INSERT INTO product_variants (product_id, sku, variant_name, price, compare_at_price, stock_quantity, option_values, is_default, is_active, display_order)
VALUES
(4, 'S24U-256-GRAY', '256GB - Titanium Gray', 1099.00, NULL, 40, '{"Storage": "256GB", "Color": "Titanium Gray"}', TRUE, TRUE, 1),
(4, 'S24U-256-BLACK', '256GB - Titanium Black', 1099.00, NULL, 45, '{"Storage": "256GB", "Color": "Titanium Black"}', FALSE, TRUE, 2),
(4, 'S24U-256-VIOLET', '256GB - Titanium Violet', 1099.00, 1199.00, 35, '{"Storage": "256GB", "Color": "Titanium Violet"}', FALSE, TRUE, 3),
(4, 'S24U-512-GRAY', '512GB - Titanium Gray', 1299.00, NULL, 20, '{"Storage": "512GB", "Color": "Titanium Gray"}', FALSE, TRUE, 4),
(4, 'S24U-512-BLACK', '512GB - Titanium Black', 1299.00, NULL, 25, '{"Storage": "512GB", "Color": "Titanium Black"}', FALSE, TRUE, 5);

-- Dell XPS 13 Plus
INSERT INTO product_variants (product_id, sku, variant_name, price, compare_at_price, stock_quantity, option_values, is_default, is_active, display_order)
VALUES
(5, 'XPS13-16-512', '16GB RAM - 512GB SSD', 1499.00, NULL, 20, '{"RAM": "16GB", "Storage": "512GB"}', TRUE, TRUE, 1),
(5, 'XPS13-16-1TB', '16GB RAM - 1TB SSD', 1699.00, NULL, 15, '{"RAM": "16GB", "Storage": "1TB"}', FALSE, TRUE, 2),
(5, 'XPS13-32-1TB', '32GB RAM - 1TB SSD', 1899.00, NULL, 12, '{"RAM": "32GB", "Storage": "1TB"}', FALSE, TRUE, 3),
(5, 'XPS13-32-2TB', '32GB RAM - 2TB SSD', 2199.00, NULL, 8, '{"RAM": "32GB", "Storage": "2TB"}', FALSE, TRUE, 4);

-- iPad Pro 12.9 M2 (Wi-Fi variants)
INSERT INTO product_variants (product_id, sku, variant_name, price, compare_at_price, stock_quantity, option_values, is_default, is_active, display_order)
VALUES
(9, 'IPADPRO-128-WIFI-GRAY', '128GB Wi-Fi - Space Gray', 1099.00, NULL, 25, '{"Storage": "128GB", "Connectivity": "Wi-Fi", "Color": "Space Gray"}', TRUE, TRUE, 1),
(9, 'IPADPRO-128-WIFI-SILVER', '128GB Wi-Fi - Silver', 1099.00, NULL, 20, '{"Storage": "128GB", "Connectivity": "Wi-Fi", "Color": "Silver"}', FALSE, TRUE, 2),
(9, 'IPADPRO-256-WIFI-GRAY', '256GB Wi-Fi - Space Gray', 1299.00, NULL, 18, '{"Storage": "256GB", "Connectivity": "Wi-Fi", "Color": "Space Gray"}', FALSE, TRUE, 3),
(9, 'IPADPRO-256-CELLULAR-GRAY', '256GB Wi-Fi+Cellular - Space Gray', 1499.00, NULL, 15, '{"Storage": "256GB", "Connectivity": "Wi-Fi + Cellular", "Color": "Space Gray"}', FALSE, TRUE, 4),
(9, 'IPADPRO-512-WIFI-GRAY', '512GB Wi-Fi - Space Gray', 1499.00, NULL, 12, '{"Storage": "512GB", "Connectivity": "Wi-Fi", "Color": "Space Gray"}', FALSE, TRUE, 5);

-- Asus ROG Strix G17
INSERT INTO product_variants (product_id, sku, variant_name, price, compare_at_price, stock_quantity, option_values, is_default, is_active, display_order)
VALUES
(8, 'ROG-G17-16-512-4060', '16GB - 512GB - RTX 4060', 1899.00, NULL, 15, '{"RAM": "16GB", "Storage": "512GB", "GPU": "RTX 4060"}', TRUE, TRUE, 1),
(8, 'ROG-G17-16-1TB-4060', '16GB - 1TB - RTX 4060', 2099.00, NULL, 12, '{"RAM": "16GB", "Storage": "1TB", "GPU": "RTX 4060"}', FALSE, TRUE, 2),
(8, 'ROG-G17-32-1TB-4070', '32GB - 1TB - RTX 4070', 2499.00, 2699.00, 10, '{"RAM": "32GB", "Storage": "1TB", "GPU": "RTX 4070"}', FALSE, TRUE, 3),
(8, 'ROG-G17-32-1TB-4060', '32GB - 1TB - RTX 4060', 2299.00, NULL, 8, '{"RAM": "32GB", "Storage": "1TB", "GPU": "RTX 4060"}', FALSE, TRUE, 4);

-- ============================
-- 8. PRODUCT PRICE HISTORY
-- ============================

-- Price history for products WITHOUT variants (simple products)
-- These are products with availability > 0 (products 11-46)
INSERT INTO product_price_history (product_id, variant_id, old_price, new_price, changed_at)
SELECT 
    id, 
    NULL,
    price + 100.00,
    price,
    NOW() - INTERVAL '30 days'
FROM products
WHERE availability > 0 AND is_deleted = FALSE;

-- Add another price change for some simple products
INSERT INTO product_price_history (product_id, variant_id, old_price, new_price, changed_at)
VALUES
(14, NULL, 749.00, 699.00, NOW() - INTERVAL '15 days'),
(15, NULL, 449.00, 399.00, NOW() - INTERVAL '10 days'),
(21, NULL, 139.00, 119.00, NOW() - INTERVAL '20 days'),
(24, NULL, 109.00, 99.00, NOW() - INTERVAL '25 days'),
(28, NULL, 649.00, 599.00, NOW() - INTERVAL '12 days');


-- Price history for VARIANTS
-- MacBook Air M2 variants price history
INSERT INTO product_price_history (product_id, variant_id, old_price, new_price, changed_at)
SELECT 
    NULL,
    id,
    price + 100.00,
    price,
    NOW() - INTERVAL '45 days'
FROM product_variants
WHERE product_id = 1 AND deleted_at IS NULL;

-- iPhone 15 Pro Max variants price history  
INSERT INTO product_price_history (product_id, variant_id, old_price, new_price, changed_at)
SELECT 
    NULL,
    id,
    price + 150.00,
    price,
    NOW() - INTERVAL '30 days'
FROM product_variants
WHERE product_id = 3 AND deleted_at IS NULL;

-- Samsung S24 Ultra price drop
INSERT INTO product_price_history (product_id, variant_id, old_price, new_price, changed_at)
SELECT 
    NULL,
    id,
    1199.00,
    price,
    NOW() - INTERVAL '20 days'
FROM product_variants
WHERE product_id = 4 AND sku LIKE 'S24U-256%' AND deleted_at IS NULL;

-- Dell XPS 13 Plus variants
INSERT INTO product_price_history (product_id, variant_id, old_price, new_price, changed_at)
SELECT 
    NULL,
    id,
    price + 200.00,
    price,
    NOW() - INTERVAL '60 days'
FROM product_variants
WHERE product_id = 5 AND deleted_at IS NULL;

-- iPad Pro variants
INSERT INTO product_price_history (product_id, variant_id, old_price, new_price, changed_at)
SELECT 
    NULL,
    id,
    price + 100.00,
    price,
    NOW() - INTERVAL '40 days'
FROM product_variants
WHERE product_id = 9 AND deleted_at IS NULL;

-- Asus ROG Strix price changes
INSERT INTO product_price_history (product_id, variant_id, old_price, new_price, changed_at)
SELECT 
    NULL,
    id,
    price + 300.00,
    price,
    NOW() - INTERVAL '35 days'
FROM product_variants
WHERE product_id = 8 AND deleted_at IS NULL;

-- Black Friday discount for some variants
INSERT INTO product_price_history (product_id, variant_id, old_price, new_price, changed_at)
VALUES
-- iPhone discount
((SELECT id FROM product_variants WHERE sku = 'IP15PM-256-BLUE'), NULL, 1299.00, 1199.00, NOW() - INTERVAL '7 days'),
-- Samsung discount  
((SELECT id FROM product_variants WHERE sku = 'S24U-256-VIOLET'), NULL, 1199.00, 1099.00, NOW() - INTERVAL '7 days'),
-- Asus gaming laptop discount
((SELECT id FROM product_variants WHERE sku = 'ROG-G17-32-1TB-4070'), NULL, 2699.00, 2499.00, NOW() - INTERVAL '5 days');

