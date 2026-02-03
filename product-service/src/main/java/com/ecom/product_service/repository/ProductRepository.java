package com.ecom.product_service.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ecom.product_service.dto.ProductListDTO;
import com.ecom.product_service.model.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

        @Query("SELECT COUNT(p) > 0 FROM Product p WHERE LOWER(p.name) = LOWER(:name)")
        boolean existsByName(@Param("name") String name);

        @Query("SELECT p FROM Product p " +
                        "LEFT JOIN FETCH p.status " +
                        "LEFT JOIN FETCH p.category " +
                        "LEFT JOIN FETCH p.brand " +
                        "WHERE (:search IS NULL OR :search = '') OR " +
                        "(LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
                        "LOWER(p.description) LIKE LOWER(CONCAT('%', :search, '%')))")
        Page<Product> findAllWithSearch(@Param("search") String search, Pageable pageable);

        // Prevents N+1 query problem when mapping to ProductResponse
        @EntityGraph(attributePaths = {
                        "status",
                        "category",
                        "brand",
                        "attributes"
        })
        @Query("SELECT DISTINCT p FROM Product p " +
                        "LEFT JOIN p.status s " +
                        "LEFT JOIN p.category c " +
                        "LEFT JOIN p.brand b " +
                        "WHERE (:search IS NULL OR :search = '' OR " +
                        "LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
                        "LOWER(p.description) LIKE LOWER(CONCAT('%', :search, '%'))) " +
                        "AND (:statusId IS NULL OR s.id = :statusId) " +
                        "AND (:categoryId IS NULL OR c.id = :categoryId) " +
                        "AND (:brandId IS NULL OR b.id = :brandId) " +
                        "AND p.isDeleted = false")
        Page<Product> findAllWithFilters(@Param("search") String search,
                        @Param("statusId") Long statusId,
                        @Param("categoryId") Long categoryId,
                        @Param("brandId") Long brandId,
                        Pageable pageable);

        // Fetch product with all details for ProductResponse mapping                
        @Query("SELECT p FROM Product p " +
                        "LEFT JOIN FETCH p.status " +
                        "LEFT JOIN FETCH p.category " +
                        "LEFT JOIN FETCH p.brand " +
                        "WHERE p.id = :id")
        Product findByIdWithDetails(@Param("id") Long id);

        // DTO Projection - Best Performance for List View
        // Constructor projection: Fetch chỉ columns cần thiết, không load entities
        // No N+1 problem vì không có lazy collections
        // Single query với minimal data transfer
        @Query("""
                        SELECT new com.ecom.product_service.dto.ProductListDTO(
                            p.id,
                            p.name,
                            p.slug,
                            p.description,
                            p.price,
                            p.availability,
                            s.id,
                            s.label,
                            c.id,
                            c.name,
                            b.id,
                            b.name,
                            p.isDeleted,
                            p.createdAt,
                            p.updatedAt
                        )
                        FROM Product p
                        LEFT JOIN p.status s
                        LEFT JOIN p.category c
                        LEFT JOIN p.brand b
                        WHERE (:search IS NULL OR :search = '' OR
                               LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')) OR
                               LOWER(p.description) LIKE LOWER(CONCAT('%', :search, '%')))
                        AND (:statusId IS NULL OR s.id = :statusId)
                        AND (:categoryId IS NULL OR c.id = :categoryId)
                        AND (:brandId IS NULL OR b.id = :brandId)
                        AND p.isDeleted = false
                        """)
        Page<ProductListDTO> findAllProductsOptimized(
                        @Param("search") String search,
                        @Param("statusId") Long statusId,
                        @Param("categoryId") Long categoryId,
                        @Param("brandId") Long brandId,
                        Pageable pageable);
}
