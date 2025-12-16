package com.ecom.product_service.service.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
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

    @Value("${file.upload-dir:uploads/products}")
    private String uploadDir;

    private static final int MAX_IMAGES_PER_PRODUCT = 6;
    private static final int MAX_THUMBNAILS_PER_PRODUCT = 1;

    @Override
    @Transactional
    public ProductImageResponse addImage(Long productId, MultipartFile file, Boolean isThumbnail) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm với ID: " + productId));

        // Check số lượng ảnh
        Long currentImageCount = productImageRepository.countByProductId(productId);
        if (currentImageCount >= MAX_IMAGES_PER_PRODUCT) {
            throw new BadRequestException("Sản phẩm chỉ được phép có tối đa " + MAX_IMAGES_PER_PRODUCT + " ảnh");
        }

        // Check số lượng ảnh đại diện
        if (isThumbnail != null && isThumbnail) {
            Long currentThumbnailCount = productImageRepository.countThumbnailByProductId(productId);
            if (currentThumbnailCount >= MAX_THUMBNAILS_PER_PRODUCT) {
                throw new BadRequestException("Sản phẩm chỉ được phép có tối đa " + MAX_THUMBNAILS_PER_PRODUCT + " ảnh đại diện");
            }
        }

        if (file.isEmpty()) {
            throw new BadRequestException("File không được để trống");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new BadRequestException("File phải là ảnh (jpg, png, gif, ...)");
        }

        long maxSize = 15 * 1024 * 1024; 
        if (file.getSize() > maxSize) {
            throw new BadRequestException("Kích thước file không được vượt quá 15MB");
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
    @Transactional(readOnly = true)
    public List<ProductImageResponse> getImagesByProductId(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("Không tìm thấy sản phẩm với ID: " + productId);
        }

        List<ProductImage> images = productImageRepository.findByProductId(productId);
        return images.stream()
                .map(productImageMapper::toProductImageResponse)
                .collect(Collectors.toList());
    }

    private String saveFile(MultipartFile file) {
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
            throw new BadRequestException("Không thể lưu file: " + e.getMessage());
        }
    }
}
