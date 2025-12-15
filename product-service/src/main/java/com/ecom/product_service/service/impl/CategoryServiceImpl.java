package com.ecom.product_service.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecom.product_service.dto.CategoryRequest;
import com.ecom.product_service.exception.BadRequestException;
import com.ecom.product_service.exception.ResourceNotFoundException;
import com.ecom.product_service.mapper.CategoryMapper;
import com.ecom.product_service.model.Category;
import com.ecom.product_service.repository.CategoryRepository;
import com.ecom.product_service.response.CategoryResponse;
import com.ecom.product_service.response.PageResponse;
import com.ecom.product_service.service.CategoryService;
import com.ecom.product_service.util.SlugUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<CategoryResponse> getAllCategories(int page, int size, String search) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Category> categoryPage = categoryRepository.findAllWithSearch(search, pageable);

        List<CategoryResponse> categoryresponse = categoryPage.getContent().stream()
                .map(categoryMapper::toCategoryResponse)
                .collect(Collectors.toList());

        return PageResponse.<CategoryResponse>builder()
                .content(categoryresponse)
                .pageNumber(categoryPage.getNumber())
                .pageSize(categoryPage.getSize())
                .totalElements(categoryPage.getTotalElements())
                .totalPages(categoryPage.getTotalPages())
                .last(categoryPage.isLast())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục với ID: " + id));

        CategoryResponse response = categoryMapper.toCategoryResponse(category);

        // Lay ten danh muc cha
        if (category.getParentId() != null) {
            categoryRepository.findById(category.getParentId())
                    .ifPresent(parent -> response.setParentName(parent.getName()));
        }

        // Lay child category
        List<Category> children = categoryRepository.findByParentId(id);
        if (!children.isEmpty()) {
            List<CategoryResponse> childrenresponse = children.stream()
                    .map(categoryMapper::toCategoryResponse)
                    .collect(Collectors.toList());
            response.setChildren(childrenresponse);
        }

        return response;
    }

    @Override
    @Transactional
    public CategoryResponse createCategory(CategoryRequest request) {
        // Validate
        if (categoryRepository.existsByName(request.getName())) {
            throw new BadRequestException("Tên danh mục đã tồn tại");
        }
        if (request.getParentId() != null) {
            if (!categoryRepository.existsById(request.getParentId())) {
                throw new ResourceNotFoundException("Không tìm thấy danh mục cha với ID: " + request.getParentId());
            }
        }

        // Tao slug tu name
        String slug = SlugUtils.toSlug(request.getName());

        // check slug co chua
        if (categoryRepository.existsBySlug(slug)) {
            slug = slug + "-" + System.currentTimeMillis();
        }

        Category category = new Category();

        category.setName(request.getName());
        category.setSlug(slug);
        category.setParentId(request.getParentId());

        Category savedCategory = categoryRepository.save(category);
        CategoryResponse response = categoryMapper.toCategoryResponse(savedCategory);
        
        // Lay ten danh muc cha neu co
        if (savedCategory.getParentId() != null) {
            categoryRepository.findById(savedCategory.getParentId())
                    .ifPresent(parent -> response.setParentName(parent.getName()));
        }
        
        return response;
    }

    @Override
    @Transactional
    public CategoryResponse updateCategory(Long id, CategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục với ID: " + id));

        if (request.getParentId() != null) {
            if (!categoryRepository.existsById(request.getParentId())) {
                throw new ResourceNotFoundException("Không tìm thấy danh mục cha với ID: " + request.getParentId());
            }
            if (request.getParentId().equals(id)) {
                throw new BadRequestException("Không thể đặt danh mục làm danh mục cha của chính nó");
            }
        }

        // Cap nhat slug khi name thay doi
        if (!category.getName().equals(request.getName())) {
            if (categoryRepository.existsByName(request.getName())) {
                throw new BadRequestException("Tên danh mục đã tồn tại");
            }
            String slug = SlugUtils.toSlug(request.getName());
            if (categoryRepository.existsBySlug(slug) && !category.getSlug().equals(slug)) {
                slug = slug + "-" + System.currentTimeMillis();
            }
            category.setSlug(slug);
        }

        category.setName(request.getName());
        category.setParentId(request.getParentId());

        Category updatedCategory = categoryRepository.save(category);
        CategoryResponse response = categoryMapper.toCategoryResponse(updatedCategory);
        
        // Lay ten danh muc cha neu co
        if (updatedCategory.getParentId() != null) {
            categoryRepository.findById(updatedCategory.getParentId())
                    .ifPresent(parent -> response.setParentName(parent.getName()));
        }
        
        return response;
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục với ID: " + id));

        category.setIsDeleted(true);
        categoryRepository.save(category);
    }
}
