package com.ecom.product_service.service.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.ecom.product_service.exception.BadRequestException;
import com.ecom.product_service.exception.ResourceNotFoundException;
import com.ecom.product_service.mapper.ProductImageMapper;
import com.ecom.product_service.model.Product;
import com.ecom.product_service.model.ProductImage;
import com.ecom.product_service.repository.ProductImageRepository;
import com.ecom.product_service.repository.ProductRepository;
import com.ecom.product_service.response.ProductImageResponse;
import com.ecom.product_service.service.ProductImageService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductImageServiceImpl implements ProductImageService {
    
    private final ProductImageRepository productImageRepository;
    private final ProductRepository productRepository;
    private final ProductImageMapper productImageMapper;
    private final MessageSource messageSource;

    @Value("${file.upload-dir:uploads/products}")
    private String uploadDir;

    private static final int MAX_IMAGES_PER_PRODUCT = 6;
    private static final int MAX_THUMBNAILS_PER_PRODUCT = 1;
    private static final long MAX_FILE_SIZE_MB = 15;
    
    // Allowed image MIME types
    private static final List<String> ALLOWED_IMAGE_TYPES = List.of(
        "image/jpeg",
        "image/jpg",
        "image/png",
        "image/gif",
        "image/webp"
    );
    
    // Allowed file extensions
    private static final List<String> ALLOWED_EXTENSIONS = List.of(
        ".jpg",
        ".jpeg",
        ".png",
        ".gif",
        ".webp"
    );

    @Override
    @Transactional
    public ProductImageResponse addImage(Long productId, MultipartFile file, Boolean isThumbnail) {
        Locale locale = LocaleContextHolder.getLocale();
        
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException(
                    messageSource.getMessage("error.product.not-found", new Object[]{productId}, locale)));

        Long currentImageCount = productImageRepository.countByProductId(productId);
        if (currentImageCount >= MAX_IMAGES_PER_PRODUCT) {
            throw new BadRequestException(
                messageSource.getMessage("error.product-image.max-images", 
                    new Object[]{MAX_IMAGES_PER_PRODUCT}, locale));
        }

        if (isThumbnail != null && isThumbnail) {
            Long currentThumbnailCount = productImageRepository.countThumbnailByProductId(productId);
            if (currentThumbnailCount >= MAX_THUMBNAILS_PER_PRODUCT) {
                throw new BadRequestException(
                    messageSource.getMessage("error.product-image.max-thumbnails", 
                        new Object[]{MAX_THUMBNAILS_PER_PRODUCT}, locale));
            }
        }

        if (file.isEmpty()) {
            throw new BadRequestException(
                messageSource.getMessage("error.product-image.file-empty", null, locale));
        }


        validateImageFormat(file, locale);

        long maxSize = MAX_FILE_SIZE_MB * 1024 * 1024; 
        if (file.getSize() > maxSize) {
            throw new BadRequestException(
                messageSource.getMessage("error.product-image.file-too-large", 
                    new Object[]{MAX_FILE_SIZE_MB}, locale));
        }

        String imageUrl = saveFile(file);
        ProductImage productImage = new ProductImage();
        productImage.setProduct(product);
        productImage.setImageUrl(imageUrl);
        productImage.setIsThumbnail(isThumbnail != null ? isThumbnail : false);

        ProductImage savedImage = productImageRepository.save(productImage);
        
        return productImageMapper.toProductImageResponse(savedImage);
    }

    @Override
    @Transactional
    public List<ProductImageResponse> addMultipleImages(Long productId, List<MultipartFile> files) {
        Locale locale = LocaleContextHolder.getLocale();
        
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException(
                    messageSource.getMessage("error.product.not-found", new Object[]{productId}, locale)));

        if (files == null || files.isEmpty()) {
            throw new BadRequestException(
                messageSource.getMessage("error.product-image.file-list-empty", null, locale));
        }

        Long currentImageCount = productImageRepository.countByProductId(productId);
        Long totalImagesAfterUpload = currentImageCount + files.size();
        
        if (totalImagesAfterUpload > MAX_IMAGES_PER_PRODUCT) {
            throw new BadRequestException(
                messageSource.getMessage("error.product-image.max-images-detail", 
                    new Object[]{MAX_IMAGES_PER_PRODUCT, currentImageCount, files.size()}, locale));
        }

        for (int i = 0; i < files.size(); i++) {
            MultipartFile file = files.get(i);
            
            if (file.isEmpty()) {
                throw new BadRequestException(
                    messageSource.getMessage("error.product-image.file-index-empty", 
                        new Object[]{i + 1}, locale));
            }

            // Validate image format (MIME type and extension)
            validateImageFormat(file, i + 1, locale);
            
            long maxSize = MAX_FILE_SIZE_MB * 1024 * 1024;
            if (file.getSize() > maxSize) {
                throw new BadRequestException(
                    messageSource.getMessage("error.product-image.file-index-too-large", 
                        new Object[]{i + 1, MAX_FILE_SIZE_MB}, locale));
            }
        }

        List<ProductImageResponse> responses = new ArrayList<>();
        for (MultipartFile file : files) {
            String imageUrl = saveFile(file);
            
            ProductImage productImage = new ProductImage();
            productImage.setProduct(product);
            productImage.setImageUrl(imageUrl);
            productImage.setIsThumbnail(false);

            ProductImage savedImage = productImageRepository.save(productImage);
            ProductImageResponse response = productImageMapper.toProductImageResponse(savedImage);
            responses.add(response);
        }

        return responses;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductImageResponse> getImagesByProductId(Long productId) {
        Locale locale = LocaleContextHolder.getLocale();
        
        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException(
                messageSource.getMessage("error.product.not-found", new Object[]{productId}, locale));
        }

        List<ProductImage> images = productImageRepository.findByProductId(productId);
        return images.stream()
                .map(productImageMapper::toProductImageResponse)
                .collect(Collectors.toList());
    }

    /**
     * Validate image format by checking MIME type and file extension
     * @param file the file to validate
     * @param locale the locale for error messages
     * @throws BadRequestException if the file is not a valid image
     */
    private void validateImageFormat(MultipartFile file, Locale locale) {
        String contentType = file.getContentType();
        String originalFilename = file.getOriginalFilename();
        
        // Check MIME type
        if (contentType == null || !ALLOWED_IMAGE_TYPES.contains(contentType.toLowerCase())) {
            throw new BadRequestException(
                messageSource.getMessage("error.product-image.file-invalid-format", null, locale));
        }
        
        // Check file extension
        if (originalFilename != null && originalFilename.contains(".")) {
            String extension = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
            if (!ALLOWED_EXTENSIONS.contains(extension)) {
                throw new BadRequestException(
                    messageSource.getMessage("error.product-image.file-invalid-format", null, locale));
            }
        } else {
            throw new BadRequestException(
                messageSource.getMessage("error.product-image.file-invalid-format", null, locale));
        }
    }
    
    /**
     * Validate image format for indexed file (used in batch upload)
     * @param file the file to validate
     * @param index the file index (1-based)
     * @param locale the locale for error messages
     * @throws BadRequestException if the file is not a valid image
     */
    private void validateImageFormat(MultipartFile file, int index, Locale locale) {
        String contentType = file.getContentType();
        String originalFilename = file.getOriginalFilename();
        
        // Check MIME type
        if (contentType == null || !ALLOWED_IMAGE_TYPES.contains(contentType.toLowerCase())) {
            throw new BadRequestException(
                messageSource.getMessage("error.product-image.file-index-invalid-format", 
                    new Object[]{index}, locale));
        }
        
        // Check file extension
        if (originalFilename != null && originalFilename.contains(".")) {
            String extension = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
            if (!ALLOWED_EXTENSIONS.contains(extension)) {
                throw new BadRequestException(
                    messageSource.getMessage("error.product-image.file-index-invalid-format", 
                        new Object[]{index}, locale));
            }
        } else {
            throw new BadRequestException(
                messageSource.getMessage("error.product-image.file-index-invalid-format", 
                    new Object[]{index}, locale));
        }
    }

    private String saveFile(MultipartFile file) {
        Locale locale = LocaleContextHolder.getLocale();
        
        try {
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null && originalFilename.contains(".") 
                    ? originalFilename.substring(originalFilename.lastIndexOf(".")) 
                    : "";
            String filename = UUID.randomUUID().toString() + extension;
            Path filePath = uploadPath.resolve(filename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            return "/uploads/products/" + filename;

        } catch (IOException e) {
            throw new BadRequestException(
                messageSource.getMessage("error.product-image.save-failed", 
                    new Object[]{e.getMessage()}, locale));
        }
    }
}
