package com.ecom.product_service.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
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
import com.ecom.product_service.repository.ProductPriceHistoryRepository;
import com.ecom.product_service.repository.ProductRepository;
import com.ecom.product_service.repository.ProductStatusRepository;
import com.ecom.product_service.response.PageResponse;
import com.ecom.product_service.response.ProductResponse;
import com.ecom.product_service.service.ProductService;
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
    private final ProductMapper productMapper;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ProductResponse> getAllProducts(int page, int size, String search, 
                                                         Long statusId, Long categoryId, Long brandId) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Product> productPage = productRepository.findAllWithFilters(search, statusId, categoryId, brandId, pageable);

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
            throw new ResourceNotFoundException("Không tìm thấy sản phẩm với ID: " + id);
        }
        return productMapper.toProductResponse(product);
    }

    @Override
    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        if (productRepository.existsByName(request.getName())) {
            throw new BadRequestException("Tên sản phẩm đã tồn tại");
        }

        ProductStatus status = productStatusRepository.findById(request.getStatusId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy trạng thái với ID: " + request.getStatusId()));

        Product product = new Product();
        product.setName(request.getName());
        product.setSlug(SlugUtils.toSlug(request.getName()));
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setAvailability(request.getAvailability());
        product.setStatus(status);

        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục với ID: " + request.getCategoryId()));
            product.setCategory(category);
        }

        if (request.getBrandId() != null) {
            Brand brand = brandRepository.findById(request.getBrandId())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thương hiệu với ID: " + request.getBrandId()));
            product.setBrand(brand);
        }

        Product savedProduct = productRepository.save(product);
        
        // Thêm attributes
        if (request.getAttributes() != null && !request.getAttributes().isEmpty()) {
            for (ProductAttributeRequest attrRequest : request.getAttributes()) {
                ProductAttribute attribute = new ProductAttribute();
                attribute.setProduct(savedProduct);
                attribute.setAttributeName(attrRequest.getAttributeName());
                attribute.setAttributeValue(attrRequest.getAttributeValue());
                savedProduct.getAttributes().add(attribute);
            }
            savedProduct = productRepository.save(savedProduct);
        }
        
        return productMapper.toProductResponse(savedProduct);
    }

    @Override
    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product product = productRepository.findByIdWithDetails(id);
        if (product == null) {
            throw new ResourceNotFoundException("Không tìm thấy sản phẩm với ID: " + id);
        }

        if (!product.getName().equals(request.getName()) && productRepository.existsByName(request.getName())) {
            throw new BadRequestException("Tên sản phẩm đã tồn tại");
        }

        ProductStatus status = productStatusRepository.findById(request.getStatusId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy trạng thái với ID: " + request.getStatusId()));

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
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục với ID: " + request.getCategoryId()));
            product.setCategory(category);
        } else {
            product.setCategory(null);
        }

        if (request.getBrandId() != null) {
            Brand brand = brandRepository.findById(request.getBrandId())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thương hiệu với ID: " + request.getBrandId()));
            product.setBrand(brand);
        } else {
            product.setBrand(null);
        }

        // Cập nhật attributes (xóa thuộc tính cũ hoặc thêm mới)
        if (request.getAttributes() != null) {
            product.getAttributes().clear();
            
            if (!request.getAttributes().isEmpty()) {
                for (ProductAttributeRequest attrRequest : request.getAttributes()) {
                    ProductAttribute attribute = new ProductAttribute();
                    attribute.setProduct(product);
                    attribute.setAttributeName(attrRequest.getAttributeName());
                    attribute.setAttributeValue(attrRequest.getAttributeValue());
                    product.getAttributes().add(attribute);
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
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm với ID: " + id));

        productRepository.delete(product);
    }
}
