package com.ecom.product_service.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecom.product_service.dto.ProductAttributeRequest;
import com.ecom.product_service.dto.ProductRequest;
import com.ecom.product_service.exception.BadRequestException;
import com.ecom.product_service.exception.ResourceNotFoundException;
import com.ecom.product_service.mapper.ProductMapper;
import com.ecom.product_service.model.Brand;
import com.ecom.product_service.model.Category;
import com.ecom.product_service.model.Product;
import com.ecom.product_service.model.ProductAttribute;
import com.ecom.product_service.model.ProductPriceHistory;
import com.ecom.product_service.model.ProductStatus;
import com.ecom.product_service.repository.BrandRepository;
import com.ecom.product_service.repository.CategoryRepository;
import com.ecom.product_service.repository.ProductAttributeRepository;
import com.ecom.product_service.repository.ProductPriceHistoryRepository;
import com.ecom.product_service.repository.ProductRepository;
import com.ecom.product_service.repository.ProductStatusRepository;
import com.ecom.product_service.response.CreateProductResponse;
import com.ecom.product_service.response.PageResponse;
import com.ecom.product_service.response.ProductResponse;
import com.ecom.product_service.response.ProductVariantOptionResponse;
import com.ecom.product_service.response.ProductVariantResponse;
import com.ecom.product_service.service.MessageService;
import com.ecom.product_service.service.ProductService;
import com.ecom.product_service.service.ProductVariantService;
import com.ecom.product_service.util.SlugUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final ProductStatusRepository productStatusRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final ProductPriceHistoryRepository productPriceHistoryRepository;
    private final ProductAttributeRepository productAttributeRepository;
    private final ProductMapper productMapper;
    private final MessageService messageService;
    private final ProductVariantService productVariantService;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ProductResponse> getAllProducts(int page, int size, String search,
            Long statusId, Long categoryId, Long brandId) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Product> productPage = productRepository.findAllWithFilters(search, statusId, categoryId, brandId,
                pageable);

        List<ProductResponse> productresponse = productPage.getContent().stream()
                .map(productMapper::toProductResponse)
                .collect(Collectors.toList());

        return PageResponse.<ProductResponse>builder()
                .content(productresponse)
                .pageNumber(productPage.getNumber())
                .pageSize(productPage.getSize())
                .totalElements(productPage.getTotalElements())
                .totalPages(productPage.getTotalPages())
                .last(productPage.isLast())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findByIdWithDetails(id);
        if (product == null) {
            throw new ResourceNotFoundException(
                messageService.getMessage("error.product.not-found", new Object[]{id})
            );
        }
        return productMapper.toProductResponse(product);
    }

    @Override
    @Transactional
    public CreateProductResponse createProduct(ProductRequest request) {
        if (productRepository.existsByName(request.getName())) {
            throw new BadRequestException(
                messageService.getMessage("error.product.name.exists")
            );
        }

        ProductStatus status = productStatusRepository.findById(request.getStatusId())
                .orElseThrow(() -> new ResourceNotFoundException(
                    messageService.getMessage("error.product-status.not-found", new Object[]{request.getStatusId()})
                ));

        Product product = new Product();
        product.setName(request.getName());
        product.setSlug(SlugUtils.toSlug(request.getName()));
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setAvailability(request.getAvailability());
        product.setStatus(status);

        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                        messageService.getMessage("error.category.not-found", new Object[]{request.getCategoryId()})
                    ));
            product.setCategory(category);
        }

        if (request.getBrandId() != null) {
            Brand brand = brandRepository.findById(request.getBrandId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                        messageService.getMessage("error.brand.not-found", new Object[]{request.getBrandId()})
                    ));
            product.setBrand(brand);
        }

        Product savedProduct = productRepository.save(product);

        // Thêm attributes nếu có (trong cùng transaction)
        if (request.getAttributes() != null && !request.getAttributes().isEmpty()) {
            // Validate trùng lặp trong request
            validateDuplicateAttributes(request.getAttributes());

            for (ProductAttributeRequest attrRequest : request.getAttributes()) {
                ProductAttribute attribute = new ProductAttribute();
                attribute.setProduct(savedProduct);
                attribute.setAttributeName(attrRequest.getAttributeName());
                attribute.setAttributeValue(attrRequest.getAttributeValue());
                savedProduct.getAttributes().add(attribute);
            }
            savedProduct = productRepository.save(savedProduct);
        }

        // Xu ly tao variants neu co
        if (request.getVariants() != null) {
            List<ProductVariantResponse> createdVariants =
                    productVariantService.createVariantsBulk(savedProduct.getId(), request.getVariants());
            List<ProductVariantOptionResponse> createdOptions =
                    productVariantService.getVariantOptions(savedProduct.getId());
            return CreateProductResponse.builder()
                    .product(productMapper.toProductResponse(savedProduct))
                    .variantOptions(createdOptions)
                    .variants(createdVariants)
                    .build();
        }

        return CreateProductResponse.builder()
                .product(productMapper.toProductResponse(savedProduct))
                .build();
    }

    @Override
    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product product = productRepository.findByIdWithDetails(id);
        if (product == null) {
            throw new ResourceNotFoundException(
                messageService.getMessage("error.product.not-found", new Object[]{id})
            );
        }

        if (!product.getName().equals(request.getName()) && productRepository.existsByName(request.getName())) {
            throw new BadRequestException(
                messageService.getMessage("error.product.name.exists")
            );
        }

        ProductStatus status = productStatusRepository.findById(request.getStatusId())
                .orElseThrow(() -> new ResourceNotFoundException(
                    messageService.getMessage("error.product-status.not-found", new Object[]{request.getStatusId()})
                ));

        // Lưu lịch sử giá nếu giá thay đổi
        BigDecimal oldPrice = product.getPrice();
        BigDecimal newPrice = request.getPrice();

        if (oldPrice != null && newPrice != null && oldPrice.compareTo(newPrice) != 0) {
            ProductPriceHistory priceHistory = new ProductPriceHistory();
            priceHistory.setProduct(product);
            priceHistory.setOldPrice(oldPrice);
            priceHistory.setNewPrice(newPrice);
            priceHistory.setChangedAt(LocalDateTime.now());
            productPriceHistoryRepository.save(priceHistory);
        }

        product.setName(request.getName());
        product.setSlug(SlugUtils.toSlug(request.getName()));
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setAvailability(request.getAvailability());
        product.setStatus(status);

        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                        messageService.getMessage("error.category.not-found", new Object[]{request.getCategoryId()})
                    ));
            product.setCategory(category);
        } else {
            product.setCategory(null);
        }

        if (request.getBrandId() != null) {
            Brand brand = brandRepository.findById(request.getBrandId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                        messageService.getMessage("error.brand.not-found", new Object[]{request.getBrandId()})
                    ));
            product.setBrand(brand);
        } else {
            product.setBrand(null);
        }

        if (request.getAttributes() != null) {
            validateDuplicateAttributes(request.getAttributes());
            List<Long> requestAttributeIds = request.getAttributes().stream()
                    .map(ProductAttributeRequest::getId)
                    .filter(attrId -> attrId != null)
                    .collect(Collectors.toList());

            product.getAttributes().removeIf(attr -> !requestAttributeIds.contains(attr.getId()));

            validateAttributeConflicts(id, request.getAttributes());
            
            for (ProductAttributeRequest attrRequest : request.getAttributes()) {
                if (attrRequest.getId() != null) {
                    ProductAttribute existingAttr = product.getAttributes().stream()
                            .filter(attr -> attr.getId().equals(attrRequest.getId()))
                            .findFirst()
                            .orElseThrow(() -> new ResourceNotFoundException(
                                messageService.getMessage("error.product.attribute.not-found", new Object[]{attrRequest.getId()})
                            ));

                    existingAttr.setAttributeName(attrRequest.getAttributeName());
                    existingAttr.setAttributeValue(attrRequest.getAttributeValue());
                } else {

                    ProductAttribute newAttr = new ProductAttribute();
                    newAttr.setProduct(product);
                    newAttr.setAttributeName(attrRequest.getAttributeName());
                    newAttr.setAttributeValue(attrRequest.getAttributeValue());
                    product.getAttributes().add(newAttr);
                }
            }
        }

        Product updatedProduct = productRepository.save(product);
        return productMapper.toProductResponse(updatedProduct);
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                    messageService.getMessage("error.product.not-found", new Object[]{id})
                ));
        
        validateProductNotAlreadyDeleted(product);
        
        product.setIsDeleted(true);
        productRepository.save(product);
    }
    
    private void validateProductNotAlreadyDeleted(Product product) {
        if (Boolean.TRUE.equals(product.getIsDeleted())) {
            throw new BadRequestException(
                messageService.getMessage("error.product.already-deleted")
            );
        }
    }

    // Validate neu cung 1 request co thuoc tinh bi trung lap
    private void validateDuplicateAttributes(List<ProductAttributeRequest> attributes) {
        if (attributes == null || attributes.isEmpty()) {
            return;
        }

        Set<String> seen = new HashSet<>();

        for (ProductAttributeRequest attr : attributes) {
            String key = attr.getAttributeName() + "=" + attr.getAttributeValue();

            if (seen.contains(key)) {
                throw new BadRequestException(
                    messageService.getMessage("error.product.attribute.duplicate", new Object[]{key})
                );
            }

            seen.add(key);
        }
    }


    // Validate khong cho phep them thuoc tinh trung lap voi nhung thuoc tinh da co cua san pham
    private void validateAttributeConflicts(Long productId, List<ProductAttributeRequest> requestAttrs) {
        if (requestAttrs == null || requestAttrs.isEmpty()) {
            return;
        }

        List<ProductAttribute> existingAttrs = productAttributeRepository.findByProductId(productId);

        for (ProductAttributeRequest newAttr : requestAttrs) {
            if (newAttr.getId() != null) {
                continue;
            }

            for (ProductAttribute existingAttr : existingAttrs) {
                boolean isSameName = existingAttr.getAttributeName().equals(newAttr.getAttributeName());
                boolean isSameValue = existingAttr.getAttributeValue().equals(newAttr.getAttributeValue());

                if (isSameName && isSameValue) {
                    String attributeKey = newAttr.getAttributeName() + " = " + newAttr.getAttributeValue();
                    throw new BadRequestException(
                        messageService.getMessage("error.product.attribute.exists", new Object[]{attributeKey})
                    );
                }
            }
        }
    }
}

