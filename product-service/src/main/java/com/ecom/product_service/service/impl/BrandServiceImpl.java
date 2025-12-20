package com.ecom.product_service.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecom.product_service.dto.BrandRequest;
import com.ecom.product_service.exception.BadRequestException;
import com.ecom.product_service.exception.ResourceNotFoundException;
import com.ecom.product_service.mapper.BrandMapper;
import com.ecom.product_service.model.Brand;
import com.ecom.product_service.repository.BrandRepository;
import com.ecom.product_service.response.BrandResponse;
import com.ecom.product_service.response.PageResponse;
import com.ecom.product_service.service.BrandService;
import com.ecom.product_service.service.MessageService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BrandServiceImpl implements BrandService {
    
    private final BrandRepository brandRepository;
    private final BrandMapper brandMapper;
    private final MessageService messageService;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<BrandResponse> getAllBrands(int page, int size, String search) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Brand> brandPage = brandRepository.findAllWithSearch(search, pageable);

        List<BrandResponse> brandResponses = brandPage.getContent().stream()
                .map(this::mapToBrandResponse)
                .collect(Collectors.toList());

        return buildPageResponse(brandPage, brandResponses);
    }

    @Override
    @Transactional(readOnly = true)
    public BrandResponse getBrandById(Long id) {
        Brand brand = findBrandByIdOrThrow(id);
        return mapToBrandResponse(brand);
    }

    @Override
    @Transactional
    public BrandResponse createBrand(BrandRequest request) {
        validateBrandNameUnique(request.getName());

        Brand brand = new Brand();
        brand.setName(request.getName());

        Brand savedBrand = brandRepository.save(brand);
        return mapToBrandResponse(savedBrand);
    }

    @Override
    @Transactional
    public BrandResponse updateBrand(Long id, BrandRequest request) {
        Brand brand = findBrandByIdOrThrow(id);

        if (isBrandNameChanged(brand, request.getName())) {
            validateBrandNameUnique(request.getName());
        }

        brand.setName(request.getName());
        Brand updatedBrand = brandRepository.save(brand);
        
        return mapToBrandResponse(updatedBrand);
    }

    @Override
    @Transactional
    public void deleteBrand(Long id) {
        Brand brand = findBrandByIdOrThrow(id);
        
        validateBrandNotAlreadyDeleted(brand);
        validateBrandNotInUse(id);
        
        brand.setIsDeleted(true);
        brandRepository.save(brand);
    }
    
    private Brand findBrandByIdOrThrow(Long id) {
        return brandRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                    messageService.getMessage("error.brand.not-found", new Object[]{id})
                ));
    }

    private void validateBrandNotAlreadyDeleted(Brand brand) {
        if (Boolean.TRUE.equals(brand.getIsDeleted())) {
            throw new BadRequestException(
                messageService.getMessage("error.brand.already-deleted")
            );
        }
    }

    private void validateBrandNotInUse(Long brandId) {
        Long productCount = brandRepository.countProductsByBrandId(brandId);
        if (productCount > 0) {
            throw new BadRequestException(
                messageService.getMessage("error.brand.has-products", new Object[]{productCount})
            );
        }
    }

    private void validateBrandNameUnique(String name) {
        if (brandRepository.existsByName(name)) {
            throw new BadRequestException(
                messageService.getMessage("error.brand.name.exists")
            );
        }
    }

    private boolean isBrandNameChanged(Brand brand, String newName) {
        return !brand.getName().equals(newName);
    }

    private BrandResponse mapToBrandResponse(Brand brand) {
        Long productCount = brandRepository.countProductsByBrandId(brand.getId());
        return brandMapper.toBrandResponse(brand, productCount);
    }

    private PageResponse<BrandResponse> buildPageResponse(Page<Brand> brandPage, List<BrandResponse> content) {
        return PageResponse.<BrandResponse>builder()
                .content(content)
                .pageNumber(brandPage.getNumber())
                .pageSize(brandPage.getSize())
                .totalElements(brandPage.getTotalElements())
                .totalPages(brandPage.getTotalPages())
                .last(brandPage.isLast())
                .build();
    }
}
