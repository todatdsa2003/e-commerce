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

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BrandServiceImpl implements BrandService {
    private final BrandRepository brandRepository;
    private final BrandMapper brandMapper;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<BrandResponse> getAllBrands(int page, int size, String search) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Brand> brandPage = brandRepository.findAllWithSearch(search, pageable);

        List<BrandResponse> brandresponse = brandPage.getContent().stream()
                .map(brand -> {
                    Long productCount = brandRepository.countProductsByBrandId(brand.getId());
                    return brandMapper.toBrandResponse(brand, productCount);
                })
                .collect(Collectors.toList());

        return PageResponse.<BrandResponse>builder()
                .content(brandresponse)
                .pageNumber(brandPage.getNumber())
                .pageSize(brandPage.getSize())
                .totalElements(brandPage.getTotalElements())
                .totalPages(brandPage.getTotalPages())
                .last(brandPage.isLast())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public BrandResponse getBrandById(Long id) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thương hiệu với ID: " + id));

        Long productCount = brandRepository.countProductsByBrandId(brand.getId());
        return brandMapper.toBrandResponse(brand, productCount);
    }

    @Override
    @Transactional
    public BrandResponse createBrand(BrandRequest request) {
        if (brandRepository.existsByName(request.getName())) {
            throw new BadRequestException("Tên thương hiệu đã tồn tại");
        }

        Brand brand = new Brand();
        brand.setName(request.getName());

        Brand savedBrand = brandRepository.save(brand);
        Long productCount = brandRepository.countProductsByBrandId(savedBrand.getId());
        return brandMapper.toBrandResponse(savedBrand, productCount);
    }

    @Override
    @Transactional
    public BrandResponse updateBrand(Long id, BrandRequest request) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thương hiệu với ID: " + id));

        if (!brand.getName().equals(request.getName()) && brandRepository.existsByName(request.getName())) {
            throw new BadRequestException("Tên thương hiệu đã tồn tại");
        }

        brand.setName(request.getName());

        Brand updatedBrand = brandRepository.save(brand);
        Long productCount = brandRepository.countProductsByBrandId(updatedBrand.getId());
        return brandMapper.toBrandResponse(updatedBrand, productCount);
    }

    @Override
    @Transactional
    public void deleteBrand(Long id) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thương hiệu với ID: " + id));

        if (brandRepository.hasProducts(id)) {
            throw new BadRequestException("Không thể xóa thương hiệu vì còn sản phẩm đang sử dụng");
        }

        brandRepository.delete(brand);
    }
}
