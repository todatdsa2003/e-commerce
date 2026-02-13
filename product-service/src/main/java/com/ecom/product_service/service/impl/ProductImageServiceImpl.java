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
import lombok.extern.slf4j.Slf4j;

@Slf4j
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

    private static final List<String> ALLOWED_IMAGE_TYPES = List.of(
        "image/jpeg",
        "image/jpg",
        "image/png",
        "image/gif",
        "image/webp"
    );

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

        Product product = findProductOrThrow(productId, locale);

        Long currentImageCount = productImageRepository.countByProductId(productId);
        if (currentImageCount >= MAX_IMAGES_PER_PRODUCT) {
            throw new BadRequestException(
                messageSource.getMessage("error.product-image.max-images", new Object[]{MAX_IMAGES_PER_PRODUCT}, locale));
        }

        if (Boolean.TRUE.equals(isThumbnail)) {
            Long currentThumbnailCount = productImageRepository.countThumbnailByProductId(productId);
            if (currentThumbnailCount >= MAX_THUMBNAILS_PER_PRODUCT) {
                throw new BadRequestException(
                    messageSource.getMessage("error.product-image.max-thumbnails", new Object[]{MAX_THUMBNAILS_PER_PRODUCT}, locale));
            }
        }

        if (file.isEmpty()) {
            throw new BadRequestException(
                messageSource.getMessage("error.product-image.file-empty", null, locale));
        }

        validateImageFormat(file, locale);
        validateFileSize(file, locale);

        String imageUrl = saveFile(file);
        ProductImage productImage = new ProductImage();
        productImage.setProduct(product);
        productImage.setImageUrl(imageUrl);
        productImage.setIsThumbnail(isThumbnail != null ? isThumbnail : false);

        return productImageMapper.toProductImageResponse(productImageRepository.save(productImage));
    }

    @Override
    @Transactional
    public List<ProductImageResponse> addMultipleImages(Long productId, List<MultipartFile> files, Integer thumbnailIndex) {
        Locale locale = LocaleContextHolder.getLocale();

        Product product = findProductOrThrow(productId, locale);

        if (files == null || files.isEmpty()) {
            throw new BadRequestException(
                messageSource.getMessage("error.product-image.file-list-empty", null, locale));
        }

        Long currentImageCount = productImageRepository.countByProductId(productId);
        if (currentImageCount + files.size() > MAX_IMAGES_PER_PRODUCT) {
            throw new BadRequestException(
                messageSource.getMessage("error.product-image.max-images-detail",
                    new Object[]{MAX_IMAGES_PER_PRODUCT, currentImageCount, files.size()}, locale));
        }

        if (thumbnailIndex != null) {
            if (thumbnailIndex < 0 || thumbnailIndex >= files.size()) {
                throw new BadRequestException(
                    messageSource.getMessage("error.product-image.thumbnail-index-invalid",
                        new Object[]{thumbnailIndex, files.size() - 1}, locale));
            }
            Long currentThumbnailCount = productImageRepository.countThumbnailByProductId(productId);
            if (currentThumbnailCount >= MAX_THUMBNAILS_PER_PRODUCT) {
                throw new BadRequestException(
                    messageSource.getMessage("error.product-image.max-thumbnails", new Object[]{MAX_THUMBNAILS_PER_PRODUCT}, locale));
            }
        }

        for (int i = 0; i < files.size(); i++) {
            MultipartFile file = files.get(i);
            if (file.isEmpty()) {
                throw new BadRequestException(
                    messageSource.getMessage("error.product-image.file-index-empty", new Object[]{i + 1}, locale));
            }
            validateImageFormat(file, i + 1, locale);
            validateFileSize(file, i + 1, locale);
        }

        List<ProductImageResponse> responses = new ArrayList<>();
        for (int i = 0; i < files.size(); i++) {
            String imageUrl = saveFile(files.get(i));
            ProductImage productImage = new ProductImage();
            productImage.setProduct(product);
            productImage.setImageUrl(imageUrl);
            productImage.setIsThumbnail(thumbnailIndex != null && thumbnailIndex == i);
            responses.add(productImageMapper.toProductImageResponse(productImageRepository.save(productImage)));
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

        return productImageRepository.findByProductId(productId).stream()
            .map(productImageMapper::toProductImageResponse)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ProductImageResponse setThumbnail(Long productId, Long imageId) {
        Locale locale = LocaleContextHolder.getLocale();

        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException(
                messageSource.getMessage("error.product.not-found", new Object[]{productId}, locale));
        }

        ProductImage image = productImageRepository.findByIdAndProductId(imageId, productId)
            .orElseThrow(() -> new ResourceNotFoundException(
                messageSource.getMessage("error.product-image.not-found", new Object[]{imageId, productId}, locale)));

        if (Boolean.TRUE.equals(image.getIsThumbnail())) {
            return productImageMapper.toProductImageResponse(image);
        }

        productImageRepository.findThumbnailByProductId(productId).ifPresent(current -> {
            current.setIsThumbnail(false);
            productImageRepository.save(current);
        });

        image.setIsThumbnail(true);
        return productImageMapper.toProductImageResponse(productImageRepository.save(image));
    }

    @Override
    @Transactional
    public void deleteImage(Long productId, Long imageId) {
        Locale locale = LocaleContextHolder.getLocale();

        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException(
                messageSource.getMessage("error.product.not-found", new Object[]{productId}, locale));
        }

        ProductImage image = productImageRepository.findByIdAndProductId(imageId, productId)
            .orElseThrow(() -> new ResourceNotFoundException(
                messageSource.getMessage("error.product-image.not-found", new Object[]{imageId, productId}, locale)));

        deletePhysicalFile(image.getImageUrl());
        productImageRepository.delete(image);
    }

    private Product findProductOrThrow(Long productId, Locale locale) {
        return productRepository.findById(productId)
            .orElseThrow(() -> new ResourceNotFoundException(
                messageSource.getMessage("error.product.not-found", new Object[]{productId}, locale)));
    }

    private void validateImageFormat(MultipartFile file, Locale locale) {
        String contentType = file.getContentType();
        String originalFilename = file.getOriginalFilename();

        if (contentType == null || !ALLOWED_IMAGE_TYPES.contains(contentType.toLowerCase())) {
            throw new BadRequestException(
                messageSource.getMessage("error.product-image.file-invalid-format", null, locale));
        }

        if (originalFilename == null || !originalFilename.contains(".")) {
            throw new BadRequestException(
                messageSource.getMessage("error.product-image.file-invalid-format", null, locale));
        }

        String extension = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new BadRequestException(
                messageSource.getMessage("error.product-image.file-invalid-format", null, locale));
        }
    }

    private void validateImageFormat(MultipartFile file, int index, Locale locale) {
        String contentType = file.getContentType();
        String originalFilename = file.getOriginalFilename();

        if (contentType == null || !ALLOWED_IMAGE_TYPES.contains(contentType.toLowerCase())) {
            throw new BadRequestException(
                messageSource.getMessage("error.product-image.file-index-invalid-format", new Object[]{index}, locale));
        }

        if (originalFilename == null || !originalFilename.contains(".")) {
            throw new BadRequestException(
                messageSource.getMessage("error.product-image.file-index-invalid-format", new Object[]{index}, locale));
        }

        String extension = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new BadRequestException(
                messageSource.getMessage("error.product-image.file-index-invalid-format", new Object[]{index}, locale));
        }
    }

    private void validateFileSize(MultipartFile file, Locale locale) {
        if (file.getSize() > MAX_FILE_SIZE_MB * 1024 * 1024) {
            throw new BadRequestException(
                messageSource.getMessage("error.product-image.file-too-large", new Object[]{MAX_FILE_SIZE_MB}, locale));
        }
    }

    private void validateFileSize(MultipartFile file, int index, Locale locale) {
        if (file.getSize() > MAX_FILE_SIZE_MB * 1024 * 1024) {
            throw new BadRequestException(
                messageSource.getMessage("error.product-image.file-index-too-large", new Object[]{index, MAX_FILE_SIZE_MB}, locale));
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
            String extension = (originalFilename != null && originalFilename.contains("."))
                ? originalFilename.substring(originalFilename.lastIndexOf("."))
                : "";
            String filename = UUID.randomUUID().toString() + extension;
            Files.copy(file.getInputStream(), uploadPath.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
            return "/uploads/products/" + filename;
        } catch (IOException e) {
            throw new BadRequestException(
                messageSource.getMessage("error.product-image.save-failed", new Object[]{e.getMessage()}, locale));
        }
    }

    private void deletePhysicalFile(String imageUrl) {
        try {
            String filename = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
            Files.deleteIfExists(Paths.get(uploadDir).resolve(filename));
        } catch (IOException e) {
            log.warn("Could not delete physical image file: {}", imageUrl, e);
        }
    }
}
