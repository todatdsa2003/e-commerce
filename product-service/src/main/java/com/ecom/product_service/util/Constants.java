package com.ecom.product_service.util;

/**
 * Constants for Product Service
 * Centralized location for all magic strings and numbers
 */
public final class Constants {
    
    private Constants() {
        // Prevent instantiation
        throw new AssertionError("Cannot instantiate Constants class");
    }
    
    //  Price History 
    public static final String PRICE_CHANGE_REASON_VARIANT_UPDATE = "Price updated via variant update";
    public static final String PRICE_CHANGE_REASON_PRODUCT_UPDATE = "Price updated via product update";
    public static final String PRICE_CHANGED_BY_SYSTEM = "SYSTEM";
    public static final String PRICE_CHANGED_BY_ADMIN = "ADMIN";
    
    //  File Upload 
    public static final long BYTES_PER_MB = 1024L * 1024L;
    public static final int DEFAULT_MAX_FILE_SIZE_MB = 10;
    
    //  Pagination 
    public static final int DEFAULT_PAGE_SIZE = 10;
    public static final int DEFAULT_PAGE_NUMBER = 0;
    public static final int MAX_PAGE_SIZE = 100;
    
    //  Variant Defaults 
    public static final int DEFAULT_LOW_STOCK_THRESHOLD = 5;
    public static final int DEFAULT_DISPLAY_ORDER = 0;
    public static final boolean DEFAULT_IS_ACTIVE = true;
    
    //  Image Types 
    public static final String IMAGE_TYPE_JPEG = "image/jpeg";
    public static final String IMAGE_TYPE_PNG = "image/png";
    public static final String IMAGE_TYPE_GIF = "image/gif";
    public static final String IMAGE_TYPE_WEBP = "image/webp";
    
    //  File Extensions 
    public static final String EXT_JPG = ".jpg";
    public static final String EXT_JPEG = ".jpeg";
    public static final String EXT_PNG = ".png";
    public static final String EXT_GIF = ".gif";
    public static final String EXT_WEBP = ".webp";
    
    //  AI Configuration 
    public static final double AI_TEMPERATURE = 0.7;
    public static final int AI_MAX_OUTPUT_TOKENS = 1000;
    public static final int AI_ALTERNATIVE_MAX_TOKENS = 300;
    
    //  Entity Types 
    public static final String ENTITY_TYPE_PRODUCT = "PRODUCT";
    public static final String ENTITY_TYPE_VARIANT = "VARIANT";
}
