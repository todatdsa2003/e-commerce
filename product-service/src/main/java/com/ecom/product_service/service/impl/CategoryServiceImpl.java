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
import com.ecom.product_service.service.MessageService;
import com.ecom.product_service.util.SlugUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final MessageService messageService;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<CategoryResponse> getAllCategories(int page, int size, String search) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Category> categoryPage = categoryRepository.findAllWithSearch(search, pageable);

        List<CategoryResponse> categoryResponses = categoryPage.getContent().stream()
                .map(categoryMapper::toCategoryResponse)
                .collect(Collectors.toList());

        return buildPageResponse(categoryPage, categoryResponses);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(Long id) {
        Category category = findCategoryByIdOrThrow(id);
        CategoryResponse response = categoryMapper.toCategoryResponse(category);
        
        setParentNameIfExists(category, response);
        setChildrenIfExists(id, response);
        
        return response;
    }

    @Override
    @Transactional
    public CategoryResponse createCategory(CategoryRequest request) {
        validateCategoryName(request.getName());
        validateParentCategoryIfProvided(request.getParentId());

        String slug = generateUniqueSlug(request.getName());

        Category category = buildCategory(request, slug);
        Category savedCategory = categoryRepository.save(category);
        
        return buildCategoryResponse(savedCategory);
    }

    @Override
    @Transactional
    public CategoryResponse updateCategory(Long id, CategoryRequest request) {
        Category category = findCategoryByIdOrThrow(id);
        
        validateParentCategory(id, request.getParentId());
        updateCategoryNameAndSlug(category, request.getName());
        
        category.setParentId(request.getParentId());
        Category updatedCategory = categoryRepository.save(category);
        
        return buildCategoryResponse(updatedCategory);
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        Category category = findCategoryByIdOrThrow(id);
        
        // Validate category chua bi xoa
        validateCategoryNotDeleted(category);
        
        // Validate category khong co san pham nao phu thuoc
        validateCategoryNotInUse(id);
        
        // Validate category khong co children chua bi xoa
        validateNoActiveChildren(id);
        
        category.setIsDeleted(true);
        categoryRepository.save(category);
    }
    
    private Category findCategoryByIdOrThrow(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                    messageService.getMessage("error.category.not-found", new Object[]{id})
                ));
    }

    private void validateCategoryName(String name) {
        if (categoryRepository.existsByName(name)) {
            throw new BadRequestException(
                messageService.getMessage("error.category.name.exists")
            );
        }
    }

    private void validateParentCategoryIfProvided(Long parentId) {
        if (parentId != null && !categoryRepository.existsById(parentId)) {
            throw new ResourceNotFoundException(
                messageService.getMessage("error.category.parent.not-found", new Object[]{parentId})
            );
        }
    }

    private void validateParentCategory(Long categoryId, Long parentId) {
        if (parentId == null) {
            return;
        }

        if (!categoryRepository.existsById(parentId)) {
            throw new ResourceNotFoundException(
                messageService.getMessage("error.category.parent.not-found", new Object[]{parentId})
            );
        }

        if (parentId.equals(categoryId)) {
            throw new BadRequestException(
                messageService.getMessage("error.category.self-parent")
            );
        }
    }

    private String generateUniqueSlug(String name) {
        String slug = SlugUtils.toSlug(name);
        
        if (categoryRepository.existsBySlug(slug)) {
            slug = slug + "-" + System.currentTimeMillis();
        }
        
        return slug;
    }

    private void updateCategoryNameAndSlug(Category category, String newName) {
        if (category.getName().equals(newName)) {
            return;
        }

        validateCategoryName(newName);
        
        String newSlug = SlugUtils.toSlug(newName);
        if (categoryRepository.existsBySlug(newSlug) && !category.getSlug().equals(newSlug)) {
            newSlug = newSlug + "-" + System.currentTimeMillis();
        }
        
        category.setName(newName);
        category.setSlug(newSlug);
    }

    private Category buildCategory(CategoryRequest request, String slug) {
        Category category = new Category();
        category.setName(request.getName());
        category.setSlug(slug);
        category.setParentId(request.getParentId());
        return category;
    }

    private CategoryResponse buildCategoryResponse(Category category) {
        CategoryResponse response = categoryMapper.toCategoryResponse(category);
        setParentNameIfExists(category, response);
        return response;
    }

    private void setParentNameIfExists(Category category, CategoryResponse response) {
        if (category.getParentId() != null) {
            categoryRepository.findById(category.getParentId())
                    .ifPresent(parent -> response.setParentName(parent.getName()));
        }
    }

    private void setChildrenIfExists(Long categoryId, CategoryResponse response) {
        List<Category> children = categoryRepository.findByParentId(categoryId);
        
        if (!children.isEmpty()) {
            List<CategoryResponse> childrenResponses = children.stream()
                    .map(categoryMapper::toCategoryResponse)
                    .collect(Collectors.toList());
            response.setChildren(childrenResponses);
        }
    }

    private PageResponse<CategoryResponse> buildPageResponse(Page<Category> categoryPage, List<CategoryResponse> content) {
        return PageResponse.<CategoryResponse>builder()
                .content(content)
                .pageNumber(categoryPage.getNumber())
                .pageSize(categoryPage.getSize())
                .totalElements(categoryPage.getTotalElements())
                .totalPages(categoryPage.getTotalPages())
                .last(categoryPage.isLast())
                .build();
    }

    // Validation Methods for Delete
    
    private void validateCategoryNotDeleted(Category category) {
        if (Boolean.TRUE.equals(category.getIsDeleted())) {
            throw new BadRequestException(
                messageService.getMessage("error.category.already-deleted", new Object[]{category.getId()})
            );
        }
    }

    private void validateCategoryNotInUse(Long categoryId) {
        Long productCount = categoryRepository.countProductsByCategoryId(categoryId);
        if (productCount > 0) {
            throw new BadRequestException(
                messageService.getMessage("error.category.in-use", new Object[]{productCount})
            );
        }
    }

    private void validateNoActiveChildren(Long categoryId) {
        List<Category> activeChildren = categoryRepository.findActiveChildrenByParentId(categoryId);
        if (!activeChildren.isEmpty()) {
            String childrenNames = activeChildren.stream()
                    .map(Category::getName)
                    .collect(Collectors.joining(", "));
            throw new BadRequestException(
                messageService.getMessage("error.category.has-active-children", new Object[]{childrenNames})
            );
        }
    }
}
