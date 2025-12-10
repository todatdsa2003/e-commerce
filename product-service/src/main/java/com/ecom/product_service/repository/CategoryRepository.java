package com.ecom.product_service.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ecom.product_service.model.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    boolean existsBySlug(String slug);

    @Query("SELECT c FROM Category c " +
            "WHERE (:search IS NULL OR :search = '') OR LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Category> findAllWithSearch(@Param("search") String search, Pageable pageable);

    List<Category> findByParentId(Long parentId);

    boolean existsByParentId(Long parentId);

    @Query("SELECT COUNT(c) > 0 FROM Category c WHERE LOWER(c.name) = LOWER(:name)")
    boolean existsByName(@Param("name") String name);

    @Query("SELECT COUNT(p) > 0 FROM Product p WHERE p.category.id = :categoryId")
    boolean hasProducts(@Param("categoryId") Long categoryId);
}
