package com.ecom.product_service.util;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.ecom.product_service.exception.BadRequestException;


public class FileValidator {
    
    private static final List<String> ALLOWED_EXTENSIONS = List.of("jpg", "jpeg", "png", "gif", "webp");
    private static final List<String> ALLOWED_MIME_TYPES = List.of(
        "image/jpeg", 
        "image/png", 
        "image/gif", 
        "image/webp"
    );
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB in bytes
    
    public static void validateImageFile(MultipartFile file) {
        // Check file is not empty
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("File is empty or not provided");
        }
        
        // Check file has a name
        String filename = file.getOriginalFilename();
        if (filename == null || filename.trim().isEmpty()) {
            throw new BadRequestException("File name is invalid");
        }
        
        // Check file extension (basic check)
        if (!filename.contains(".")) {
            throw new BadRequestException("File must have an extension");
        }
        
        String extension = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new BadRequestException(
                "Invalid file extension. Only image files are allowed: " + ALLOWED_EXTENSIONS
            );
        }
        
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_MIME_TYPES.contains(contentType.toLowerCase())) {
            throw new BadRequestException(
                "Invalid file type. Allowed MIME types: " + ALLOWED_MIME_TYPES + 
                ". Received: " + contentType
            );
        }
        
        if (file.getSize() > MAX_FILE_SIZE) {
            double sizeMB = file.getSize() / (1024.0 * 1024.0);
            throw new BadRequestException(
                String.format("File size (%.2f MB) exceeds maximum allowed size of 10MB", sizeMB)
            );
        }
        
        if (filename.contains("\0")) {
            throw new BadRequestException("File name contains invalid characters");
        }
    }
}
