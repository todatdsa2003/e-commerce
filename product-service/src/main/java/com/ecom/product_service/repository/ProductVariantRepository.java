package com.ecom.product_service.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ecom.product_service.model.ProductVariant;

@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {

       List<ProductVariant> findByProductIdAndDeletedAtIsNullOrderByDisplayOrder(Long productId);

       List<ProductVariant> findByProductIdAndIsActiveTrueAndDeletedAtIsNullOrderByDisplayOrder(Long productId);

       Optional<ProductVariant> findByProductIdAndIsDefaultTrueAndDeletedAtIsNull(Long productId);

       Optional<ProductVariant> findBySkuAndDeletedAtIsNull(String sku);

       Optional<ProductVariant> findByIdAndDeletedAtIsNull(Long id);

       boolean existsByIdAndDeletedAtIsNull(Long id);

       boolean existsBySkuAndIdNotAndDeletedAtIsNull(String sku, Long id);

       boolean existsBySkuAndDeletedAtIsNull(String sku);

       // Sum stock quantity for all variants of a product
       @Query("SELECT COALESCE(SUM(v.stockQuantity), 0) FROM ProductVariant v " +
                     "WHERE v.product.id = :productId AND v.deletedAt IS NULL")
       Integer sumStockByProductId(@Param("productId") Long productId);

       // Find minimum price among active variants for a product
       @Query("SELECT MIN(v.price) FROM ProductVariant v " +
                     "WHERE v.product.id = :productId AND v.isActive = true AND v.deletedAt IS NULL")
       BigDecimal findMinPriceByProductId(@Param("productId") Long productId);

       // Find maximum price among active variants for a product
       @Query("SELECT MAX(v.price) FROM ProductVariant v " +
                     "WHERE v.product.id = :productId AND v.isActive = true AND v.deletedAt IS NULL")
       BigDecimal findMaxPriceByProductId(@Param("productId") Long productId);

       // Count active variants for a product
       @Query("SELECT COUNT(v) FROM ProductVariant v " +
                     "WHERE v.product.id = :productId AND v.isActive = true AND v.deletedAt IS NULL")
       Integer countActiveVariantsByProductId(@Param("productId") Long productId);

       // Count variants excluding soft-deleted ones
       long countByProductIdAndDeletedAtIsNull(Long productId);

       // Find variants that are low in stock
       @Query("SELECT v FROM ProductVariant v " +
                     "WHERE v.product.id = :productId " +
                     "AND v.deletedAt IS NULL " +
                     "AND v.stockQuantity <= v.lowStockThreshold " +
                     "ORDER BY v.stockQuantity ASC")
       List<ProductVariant> findLowStockVariants(@Param("productId") Long productId);
}
