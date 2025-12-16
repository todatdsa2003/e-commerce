package com.ecom.product_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ecom.product_service.model.ProductImage;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
    
    @Query("SELECT pi FROM ProductImage pi WHERE pi.product.id = :productId ORDER BY pi.isThumbnail DESC, pi.createdAt ASC")
    List<ProductImage> findByProductId(@Param("productId") Long productId);

    @Query("SELECT COUNT(pi) FROM ProductImage pi WHERE pi.product.id = :productId")
    Long countByProductId(@Param("productId") Long productId);

    @Query("SELECT COUNT(pi) FROM ProductImage pi WHERE pi.product.id = :productId AND pi.isThumbnail = true")
    Long countThumbnailByProductId(@Param("productId") Long productId);
}
