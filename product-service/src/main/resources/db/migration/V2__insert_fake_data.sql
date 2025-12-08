-- Categories
INSERT INTO categories (name, slug, parent_id) VALUES
('Laptop', 'laptop', NULL),
('Gaming Laptop', 'gaming-laptop', 1),
('Ultrabook', 'ultrabook', 1),
('Business Laptop', 'business-laptop', 1),
('Smartphone', 'smartphone', NULL),
('Android Phones', 'android-phones', 5),
('iPhone', 'iphone', 5),
('Tablets', 'tablets', NULL),
('iPad', 'ipad', 8),
('Android Tablets', 'android-tablets', 8),
('Smartwatch', 'smartwatch', NULL),
('Fitness Band', 'fitness-band', 11),
('Headphones', 'headphones', NULL),
('Wireless Earbuds', 'wireless-earbuds', 13),
('Over-ear Headphones', 'over-ear-headphones', 13),
('Monitors', 'monitors', NULL),
('4K Monitors', '4k-monitors', 16),
('Gaming Monitors', 'gaming-monitors', 16),
('Keyboards', 'keyboards', NULL),
('Mechanical Keyboards', 'mechanical-keyboards', 19),
('Mice', 'mice', NULL),
('Gaming Mice', 'gaming-mice', 21),
('Printers', 'printers', NULL),
('Inkjet Printers', 'inkjet-printers', 23),
('Laser Printers', 'laser-printers', 23),
('PC Components', 'pc-components', NULL),
('Graphics Cards', 'graphics-cards', 26),
('Motherboards', 'motherboards', 26),
('Power Supplies', 'power-supplies', 26),
('Storage Devices', 'storage-devices', NULL),
('SSD', 'ssd', 30),
('HDD', 'hdd', 30),
('Networking', 'networking', NULL),
('Routers', 'routers', 33),
('Mesh WiFi', 'mesh-wifi', 33),
('Smart Home', 'smart-home', NULL),
('Security Cameras', 'security-cameras', 36),
('Smart Lighting', 'smart-lighting', 36),
('TV', 'tv', NULL),
('Smart TV', 'smart-tv', 39),
('4K TV', '4k-tv', 39),
('Speakers', 'speakers', NULL),
('Bluetooth Speakers', 'bluetooth-speakers', 42),
('Home Theater', 'home-theater', 42),
('Consoles', 'consoles', NULL),
('PlayStation', 'playstation', 45),
('Xbox', 'xbox', 45),
('Nintendo', 'nintendo', 45),
('Drones', 'drones', NULL),
('Action Cameras', 'action-cameras', NULL);


-- brands
INSERT INTO brands (name) VALUES
('Apple'),
('Samsung'),
('Dell'),
('HP'),
('Lenovo'),
('Asus'),
('Acer'),
('MSI'),
('Razer'),
('LG'),
('Sony'),
('Huawei'),
('Xiaomi'),
('Oppo'),
('Vivo'),
('Nokia'),
('Realme'),
('Google'),
('Microsoft'),
('Logitech'),
('Corsair'),
('SteelSeries'),
('Kingston'),
('Sandisk'),
('Seagate'),
('Western Digital'),
('Gigabyte'),
('ASRock'),
('TP-Link'),
('Netgear'),
('JBL'),
('Bose'),
('Beats'),
('Marshall'),
('HyperX'),
('Canon'),
('Nikon'),
('GoPro'),
('DJI'),
('Philips'),
('Panasonic'),
('Toshiba'),
('BenQ'),
('ViewSonic'),
('Alienware'),
('Redmi'),
('OnePlus'),
('Nothing'),
('Infinix');


-- products
INSERT INTO products (name, slug, description, price, availability, status_id, category_id, brand_id)
VALUES
('MacBook Air M2 2024', 'macbook-air-m2-2024', 'Powerful laptop with M2 chip', 1299.00, 120, 1, 1, 1),
('MacBook Pro 16 M3', 'macbook-pro-16-m3', 'High-end Macbook for professionals', 2799.00, 40, 1, 1, 1),
('iPhone 15 Pro Max', 'iphone-15-pro-max', 'Flagship iPhone with titanium frame', 1199.00, 200, 1, 7, 1),
('Samsung Galaxy S24 Ultra', 'galaxy-s24-ultra', 'Samsung flagship phone', 1099.00, 150, 1, 6, 2),
('Dell XPS 13 Plus', 'dell-xps-13-plus', 'Ultrabook premium', 1499.00, 60, 1, 3, 3),
('HP Spectre x360', 'hp-spectre-x360', '2-in-1 premium ultrabook', 1399.00, 70, 1, 3, 4),
('Lenovo ThinkPad X1 Carbon', 'thinkpad-x1-carbon', 'Business class laptop', 1599.00, 55, 1, 4, 5),
('Asus ROG Strix G17', 'asus-rog-strix-g17', 'Gaming laptop with RTX GPU', 1899.00, 45, 1, 2, 6),
('Acer Predator Helios', 'predator-helios', 'Gaming laptop powerful', 1699.00, 33, 1, 2, 7),
('MSI Stealth 15', 'msi-stealth-15', 'Slim gaming laptop', 1799.00, 20, 1, 2, 8),

('Razer Blade 15', 'razer-blade-15', 'Premium gaming laptop', 2499.00, 12, 1, 2, 9),
('LG UltraFine 4K Monitor', 'lg-ultrafine-4k', 'High resolution 4K monitor', 699.00, 80, 1, 16, 10),
('Sony WH-1000XM5', 'sony-wh-1000xm5', 'Noise-cancelling headphones', 399.00, 140, 1, 15, 11),
('iPad Pro 12.9 M2', 'ipad-pro-12-9-m2', 'Professional tablet', 1099.00, 90, 1, 9, 1),
('Xiaomi 14 Ultra', 'xiaomi-14-ultra', 'Flagship camera phone', 899.00, 110, 1, 6, 13),

('Oppo Find X7', 'oppo-find-x7', 'High-end smartphone', 799.00, 120, 1, 6, 14),
('Vivo X100 Pro', 'vivo-x100-pro', 'Camera phone AI features', 899.00, 115, 1, 6, 15),
('Google Pixel 8 Pro', 'google-pixel-8-pro', 'Best AI smartphone', 999.00, 100, 1, 6, 18),
('Microsoft Surface Laptop 6', 'surface-laptop-6', 'Premium laptop from Microsoft', 1599.00, 40, 1, 3, 19),
('Logitech MX Master 3S', 'mx-master-3s', 'Top productivity mouse', 119.00, 300, 1, 21, 20),

('Corsair K95 RGB', 'corsair-k95-rgb', 'Mechanical keyboard', 199.00, 160, 1, 20, 21),
('SteelSeries Apex Pro', 'steelseries-apex-pro', 'High-end mechanical keyboard', 229.00, 120, 1, 20, 22),
('Kingston NV2 1TB', 'kingston-nv2-1tb', 'High-speed NVMe SSD', 59.00, 500, 1, 31, 23),
('Sandisk Extreme Portable', 'sandisk-extreme-portable', 'Portable SSD', 99.00, 350, 1, 31, 24),
('Seagate Barracuda 2TB', 'seagate-2tb', 'Reliable hard drive', 49.00, 400, 1, 32, 25),

('WD Blue 1TB', 'wd-blue-1tb', 'Standard HDD for PCs', 45.00, 300, 1, 32, 26),
('Gigabyte RTX 4070', 'gigabyte-rtx-4070', 'High-end graphics card', 599.00, 50, 1, 27, 27),
('ASRock B550M Steel Legend', 'asrock-b550m', 'Motherboard AM4', 129.00, 100, 1, 28, 28),
('TP-Link Archer AX73', 'archer-ax73', 'WiFi 6 router', 129.00, 200, 1, 34, 29),
('Netgear Orbi RBK50', 'orbi-rbk50', 'Mesh WiFi system', 399.00, 60, 1, 35, 30),

('JBL Charge 5', 'jbl-charge-5', 'Portable powerful speaker', 149.00, 150, 1, 43, 31),
('Bose SoundLink Flex', 'bose-flex', 'Premium Bluetooth speaker', 129.00, 120, 1, 43, 32),
('Beats Studio Pro', 'beats-studio-pro', 'High-end headphones', 349.00, 90, 1, 15, 33),
('Marshall Acton III', 'marshall-acton-3', 'Classic speaker design', 249.00, 75, 1, 42, 34),
('HyperX Cloud III', 'hyperx-cloud-3', 'Gaming headset', 129.00, 180, 1, 15, 35),

('Canon EOS R8', 'canon-r8', 'Mirrorless camera', 1499.00, 30, 1, 38, 36),
('Nikon Z6 II', 'nikon-z6-ii', 'Full frame mirrorless', 1599.00, 25, 1, 38, 37),
('GoPro Hero 12', 'gopro-hero-12', 'Best action camera', 499.00, 70, 1, 50, 38),
('DJI Mini 4 Pro', 'dji-mini-4-pro', 'Professional drone', 999.00, 40, 1, 50, 39),
('Philips Hue Lightstrip', 'philips-hue-lightstrip', 'Smart LED strip', 59.00, 300, 1, 37, 40),

('Panasonic 4K TV', 'panasonic-4k-tv', 'Affordable smart TV', 499.00, 80, 1, 40, 41),
('Toshiba Smart TV 55"', 'toshiba-55-tv', '55-inch smart TV', 399.00, 70, 1, 40, 42),
('BenQ GW2780', 'benq-gw2780', '27-inch IPS monitor', 179.00, 100, 1, 16, 43),
('ViewSonic VX2758', 'viewsonic-vx2758', '144Hz gaming monitor', 229.00, 90, 1, 18, 44),
('Alienware AW3423DW', 'alienware-aw3423dw', 'OLED gaming monitor', 1299.00, 20, 1, 18, 45);


-- product images
INSERT INTO product_images (product_id, image_url, is_thumbnail)
SELECT id, 'https://example.com/images/product_' || id || '.jpg', TRUE
FROM products;

--product attribute
INSERT INTO product_attributes (product_id, attribute_name, attribute_value)
SELECT id, 'Color', 'Black' FROM products;

INSERT INTO product_attributes (product_id, attribute_name, attribute_value)
SELECT id, 'Warranty', '12 months' FROM products;


-- product price history
INSERT INTO product_price_history (product_id, old_price, new_price)
SELECT id, price + 50, price
FROM products;
