package com.ecom.product_service.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ecom.product_service.model.ProductPriceHistory;

@Repository
public interface ProductPriceHistoryRepository extends JpaRepository<ProductPriceHistory, Long> {


    @Query("SELECT pph FROM ProductPriceHistory pph " +
            "LEFT JOIN FETCH pph.product p " +
            "WHERE p.id = :productId")
    Page<ProductPriceHistory> findByProductId(@Param("productId") Long productId, Pageable pageable);

    @Query("SELECT pph FROM ProductPriceHistory pph " +
            "LEFT JOIN FETCH pph.product p " +
            "WHERE p.id = :productId " +
            "AND (:minPrice IS NULL OR pph.newPrice >= :minPrice) " +
            "AND (:maxPrice IS NULL OR pph.newPrice <= :maxPrice)")
    Page<ProductPriceHistory> findByProductIdWithFilters(@Param("productId") Long productId,
                                                          @Param("minPrice") java.math.BigDecimal minPrice,
                                                          @Param("maxPrice") java.math.BigDecimal maxPrice,
                                                          Pageable pageable);
}
