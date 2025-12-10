package com.ecom.product_service.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ecom.product_service.model.Brand;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Long> {

    @Query("SELECT COUNT(b) > 0 FROM Brand b WHERE LOWER(b.name) = LOWER(:name)")
    boolean existsByName(@Param("name") String name);

    @Query("SELECT b FROM Brand b " +
            "WHERE (:search IS NULL OR :search = '') OR LOWER(b.name) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Brand> findAllWithSearch(@Param("search") String search, Pageable pageable);

    @Query("SELECT COUNT(p) FROM Product p WHERE p.brand.id = :brandId")
    Long countProductsByBrandId(@Param("brandId") Long brandId);

    @Query("SELECT COUNT(p) > 0 FROM Product p WHERE p.brand.id = :brandId")
    boolean hasProducts(@Param("brandId") Long brandId);
}
